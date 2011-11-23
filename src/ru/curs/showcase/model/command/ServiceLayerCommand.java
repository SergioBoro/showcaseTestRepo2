package ru.curs.showcase.model.command;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.util.UUID;

import org.slf4j.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.AppRegistry;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.ObjectSerializer;
import ru.curs.showcase.util.exception.ServerLogicError;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

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

	public static final String SERVLET_MARKER = "Servlet";

	protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerCommand.class);

	private CommandContext commandContext;

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId = SessionUtils.getCurrentSessionId();

	private T result;

	private final ObjectSerializer serializer = AppRegistry.getObjectSerializer();

	protected ObjectSerializer getSerializer() {
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

	public final T execute() {
		try {
			return templateMethod();
			// CHECKSTYLE:OFF
		} catch (Throwable e) {
			// CHECKSTYLE:ON
			throw exceptionFactory(e);
		}
	}

	private T templateMethod() throws Exception {
		initSessionContext();
		initCommandContext();
		preProcess();
		logInputParams();
		mainProc();
		logOutput();
		postProcess();
		return result;
	}

	public final T executeForExport() throws ShowcaseExportException {
		try {
			return templateMethod();
			// CHECKSTYLE:OFF
		} catch (Throwable e) {
			// CHECKSTYLE:ON
			throw new ShowcaseExportException(e);
		}
	}

	protected GeneralException exceptionFactory(final Throwable e) {
		return GeneralExceptionFactory.build(e);
	}

	private void initCommandContext() {
		commandContext =
			new CommandContext(this.getClass().getSimpleName(), UUID.randomUUID().toString());
		commandContext.toMDC();
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
					Marker marker = MarkerFactory.getDetachedMarker(SERVLET_MARKER);
					marker.add(MarkerFactory.getMarker(LastLogEvents.INPUT));
					marker.add(MarkerFactory.getMarker(String.format("class=%s \r\nmethod=%s",
							method.getReturnType().getSimpleName(), method.getName())));
					LOGGER.info(marker, serializer.serialize(methodResult));
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new ServerLogicError(e);
				}
			}
		}
	}

	protected void preProcess() {
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

		Marker marker = MarkerFactory.getDetachedMarker(SERVLET_MARKER);
		marker.add(MarkerFactory.getMarker(LastLogEvents.OUTPUT));
		marker.add(MarkerFactory.getMarker(String.format("class=%s", result.getClass()
				.getSimpleName())));
		LOGGER.info(marker, serializer.serialize(result));
	}

	protected abstract void mainProc() throws Exception;

	protected void initSessionContext() throws UnsupportedEncodingException {
		if (getContext().getSession() != null) {
			return;
		}
		String sessionContext = XMLSessionContextGenerator.generate(getContext());

		getContext().setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(getContext().getSessionParamsMap());
		getContext().getSessionParamsMap().clear();
	}

	public String getSessionId() {
		return sessionId;
	}
}
