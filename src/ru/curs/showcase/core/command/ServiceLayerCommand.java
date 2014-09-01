package ru.curs.showcase.core.command;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

import org.slf4j.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.AppRegistry;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
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

	private static final String ID_CASESENSITIVE = "id.casesensitive";

	private static final int MAX_LOG_OBJECT_SIZE = 100_000;

	public static final String SERVLET_MARKER = "Servlet";

	protected static final Logger LOGGER = LoggerFactory.getLogger(ServiceLayerCommand.class);

	private static final String NAVIGATOR = "NAVIGATOR";

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId = SessionUtils.getCurrentSessionId();

	private T result;

	private final ObjectSerializer serializer = AppRegistry.getObjectSerializer();

	/**
	 * Имя файла с настройками userdata для переопределения значения по
	 * умолчанию.
	 */
	private String propFile;

	private Map<String, String> props;

	protected ObjectSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Контекст вызова команды. Должен быть у любой команды! При этом может
	 * содержать только один атрибут - session.
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
			throw GeneralExceptionFactory.build(e, generateDataPanelElementContext());
		}
	}

	protected DataPanelElementContext generateDataPanelElementContext() {
		return new DataPanelElementContext(context);
	}

	private T templateMethod() throws Exception {

		Date dt1 = new Date();

		initSessionContext();
		initCommandContext();
		initIDSettings();
		preProcess();
		logInputParams();

		Date dt2 = new Date();
		if ((result != null) && NAVIGATOR.equalsIgnoreCase(result.getClass().getSimpleName())) {
			LoggerHelper.profileToLog("Navigator. Предварительные действия.", dt1, dt2, NAVIGATOR,
					"");
		}

		mainProc();

		dt1 = new Date();
		logOutput();
		dt2 = new Date();
		if ((result != null) && NAVIGATOR.equalsIgnoreCase(result.getClass().getSimpleName())) {
			LoggerHelper.profileToLog("Navigator. Вызов уборки мусора.", dt1, dt2, NAVIGATOR, "");
		}

		dt1 = new Date();
		postProcess();
		dt2 = new Date();
		if ((result != null) && NAVIGATOR.equalsIgnoreCase(result.getClass().getSimpleName())) {
			LoggerHelper.profileToLog("Navigator. Очистка пула XSL-трансформаций.", dt1, dt2,
					NAVIGATOR, "");
		}

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

	protected void initCommandContext() {
		MDC.clear();
		CommandContext commandContext =
			new CommandContext(this.getClass().getSimpleName(), UUID.randomUUID().toString());
		commandContext.setPropFile(propFile);
		if (props != null) {
			for (Entry<String, String> prop : props.entrySet()) {
				MDC.put(prop.getKey(), prop.getValue());
			}
		}
		commandContext.toMDC();
	}

	public void initIDSettings() {
		String idCS = UserDataUtils.getGeneralOptionalProp(ID_CASESENSITIVE);
		if (idCS != null) {
			IDSettings.getInstance().setCaseSensivity(Boolean.valueOf(idCS));
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
					Marker marker = MarkerFactory.getDetachedMarker(SERVLET_MARKER);
					marker.add(HandlingDirection.INPUT.getMarker());
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
		XSLTransformerPoolFactory.cleanup();
		MDC.clear();
	}

	/**
	 * Примечание: в виде эксперимента - вызываем явно уборку мусора, если
	 * объект получился слишком большой.
	 */
	protected void logOutput() {
		if (!LOGGER.isInfoEnabled()) {
			return;
		}
		if (result == null) {
			return;
		}

		Marker marker = MarkerFactory.getDetachedMarker(SERVLET_MARKER);
		marker.add(HandlingDirection.OUTPUT.getMarker());
		marker.add(MarkerFactory.getMarker(String.format("class=%s", result.getClass()
				.getSimpleName())));
		if (result instanceof SizeEstimate) {
			SizeEstimate se = (SizeEstimate) result;
			long esimateValue = se.sizeEstimate();
			if (esimateValue > MAX_LOG_OBJECT_SIZE) {
				Runtime.getRuntime().gc();
				LOGGER.info(
						marker,
						String.format(
								"Оценка размера возвращаемого объекта: %d байт. Объект не будет выведен в лог.",
								esimateValue));
				return;
			}
		}
		LOGGER.info(marker, serializer.serialize(result));
	}

	protected abstract void mainProc() throws Exception;

	/**
	 * Инициализация userdata и формирование строки с контекстом сессии для
	 * текущего мультиконтекста. Не выполняется если строка контекста сессии уже
	 * сформирована - что означает выполнение одной команды из другой.
	 */
	protected void initSessionContext() {
		if (getContext().getSession() != null) {
			return;
		}
		XMLSessionContextGenerator generator = setupGenerator();
		String sessionContext = generator.generate();

		getContext().setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(getContext().getSessionParamsMap());
		getContext().getSessionParamsMap().clear();
	}

	protected XMLSessionContextGenerator setupGenerator() {
		return new XMLSessionContextGenerator(getContext());
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setPropFile(final String aPropFile) {
		propFile = aPropFile;
	}

	public void setProps(final Map<String, String> aProps) {
		props = aProps;
	}
}
