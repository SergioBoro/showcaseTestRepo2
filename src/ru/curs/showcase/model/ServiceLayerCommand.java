package ru.curs.showcase.model;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.UUID;

import org.slf4j.*;

import ru.curs.showcase.app.api.ExcludeFromSerialization;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.runtime.AppInfoSingleton;

import com.google.gson.*;

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

	protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerCommand.class);

	/**
	 * Идентификатор текущего запроса к сервисному слою.
	 */
	private final UUID requestId = UUID.randomUUID();

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId;

	private T result;

	/**
	 * Контекст вызова команды. Должен быть у любой команды!
	 */
	private final CompositeContext context;

	public T getResult() {
		return result;
	}

	public void setResult(final T aResult) {
		result = aResult;
	}

	public ServiceLayerCommand(final String aSessionId, final CompositeContext aContext) {
		super();
		sessionId = aSessionId;
		context = aContext;
	}

	public T execute() throws GeneralException {
		try {
			initContext();
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
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unused")
	protected void preProcess() throws GeneralException {
		// по умолчанию ничего не делаем
	}

	protected void postProcess() {
		// по умолчанию ничего не делаем
	}

	protected void logOutput() {
		if (LOGGER.isInfoEnabled()) {
			ExclusionStrategy es = new ExclusionStrategy() {
				@Override
				public boolean shouldSkipClass(final Class<?> aClass) {
					return false;
				}

				@Override
				public boolean shouldSkipField(final FieldAttributes fa) {
					return fa.getAnnotation(ExcludeFromSerialization.class) != null;
				}
			};
			Gson gson =
				new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
						.setExclusionStrategies(es).serializeNulls()
						.excludeFieldsWithModifiers(Modifier.TRANSIENT + Modifier.STATIC).create();

			LOGGER.info(String.format("SL output \r\n %s \r\n %s", requestId, gson.toJson(result)));
		}
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

	public CompositeContext getContext() {
		return context;
	}

	public String getSessionId() {
		return sessionId;
	}
}
