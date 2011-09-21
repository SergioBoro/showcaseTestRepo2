package ru.curs.showcase.model.command;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.AppRegistry;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerLogicError;
import ru.curs.showcase.util.xml.*;

/**
 * Абстрактный класс команды сервисного уровня приложения. Весь функционал
 * приложение экспонирует посредством команд.
 * 
 * @author den
 * 
 * @param <T>
 *            - класс результата работы команды.
 */
public abstract class ServiceLayerCommand<T> {

	private static final String LOG_TEMPLATE = "Command %s \r\n objectClass=%s \r\n "
			+ "getObjectMethodName=%s \r\n %s";

	protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerCommand.class);

	private final CommandContext commandContext = new CommandContext(this.getClass()
			.getSimpleName(), UUID.randomUUID().toString());

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId = ServletUtils.getCurrentSessionId();

	private T result;

	private final ObjectToLogSerializer serializer = AppRegistry.getObjectSerializer();

	protected ObjectToLogSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Контекст вызова команды. Должен быть у любой команды!
	 */
	private final CompositeContext context;

	@InputParam
	public CompositeContext getContext() {
		return context;
	}

	public T getResult() {
		return result;
	}

	public void setResult(final T aResult) {
		result = aResult;
	}

	public ServiceLayerCommand(final CompositeContext aContext) {
		super();
		context = aContext;
	}

	private void setupMDC() {
		MDC.put(GeneralXMLHelper.USERNAME_TAG, ServletUtils.getCurrentSessionUserName());
		MDC.put(ExchangeConstants.URL_PARAM_USERDATA, AppInfoSingleton.getAppInfo()
				.getCurUserDataIdSafe());
		MDC.put(GeneralXMLHelper.REQUEST_ID_TAG, commandContext.getRequestId());
		MDC.put(GeneralXMLHelper.COMMAND_NAME_TAG, commandContext.getCommandName());
	}

	public T execute() throws GeneralException {
		try {
			initContext();
			setupMDC();
			preProcess();
			logInputParams();
			mainProc();
			logOutput();
			postProcess();
			return result;
		} catch (Throwable e) {
			throw GeneralServerExceptionFactory.build(e);
		}
	}

	protected void logInputParams() {
		if (!LOGGER.isInfoEnabled()) {
			return;
		}

		for (Method method : getClass().getMethods()) {
			if (method.getAnnotation(InputParam.class) != null) {
				try {
					Object methodResult = method.invoke(this);
					if (methodResult == null) {
						continue;
					}
					LOGGER.info(String.format(LOG_TEMPLATE, LastLogEvents.INPUT, method
							.getReturnType().getSimpleName(), method.getName(), serializer
							.serialize(methodResult)));
				} catch (Exception e) {
					throw new ServerLogicError(e);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	protected void preProcess() throws GeneralException {
		// по умолчанию ничего не делаем
	}

	protected void postProcess() {
		// по умолчанию ничего не делаем
	}

	protected void logOutput() {
		if (!LOGGER.isInfoEnabled()) {
			return;
		}
		if (result == null) {
			return;
		}

		LOGGER.info(String.format(LOG_TEMPLATE, LastLogEvents.OUTPUT, result.getClass()
				.getSimpleName(), "", serializer.serialize(result)));
	}

	protected abstract void mainProc() throws Exception;

	protected void initContext() throws UnsupportedEncodingException {
		if (getContext().getSession() != null) {
			return;
		}
		String sessionContext = SessionContextGenerator.generate(getContext());

		getContext().setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(getContext().getSessionParamsMap());
		getContext().getSessionParamsMap().clear();
	}

	public String getSessionId() {
		return sessionId;
	}
}
