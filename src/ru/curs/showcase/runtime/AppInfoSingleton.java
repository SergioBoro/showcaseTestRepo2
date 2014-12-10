package ru.curs.showcase.runtime;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.*;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.ServerLogicError;

/**
 * Синглетон для хранения информации о сессиях приложения и глобальной
 * информации в приложении. Хранить данные пользовательских сессий на сервере
 * нежелательно - это усложняет приложение - но в данном случае это вынужденная
 * мера.
 * 
 * @author den
 * 
 */
public final class AppInfoSingleton {

	public static final String GRID_STATE_CACHE = "gridStateCache";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInfoSingleton.class);

	/** Список userdata. */
	private final Map<String, UserData> userdatas = new HashMap<String, UserData>();

	/**
	 * Синглетон.
	 */
	private static final AppInfoSingleton INSTANCE = new AppInfoSingleton();

	/**
	 * Карта пользовательских сессий.
	 */
	private final Map<String, SessionInfo> sessionInfoMap = Collections
			.synchronizedMap(new HashMap<String, SessionInfo>());

	private final SortedSet<String> executedProc = Collections
			.synchronizedSortedSet(new TreeSet<String>());

	/**
	 * Идентификатор userdata в текущем запросе.
	 */
	private final ThreadLocal<String> curUserDataId = new ThreadLocal<String>();

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	private SortedSet<LoggingEventDecorator> lastLogEvents;

	private String webAppPath;

	private String userdataRoot;

	private String userDataLogConfFile = "logback.xml";

	private boolean enableLogLevelInfo = true;
	private boolean enableLogLevelWarning = true;
	private boolean enableLogLevelError = true;

	/**
	 * Переменная, которая содержит в себе информацию о том, будет ли хоть в
	 * одной userdata приложения использоваться сторонняя компонента Activiti.
	 * По умолчанию она false. Если переменная true, то в памяти серверной части
	 * Showcase будет создаваться экземпляр движка Activiti, который будет
	 * передаваться или будет доступен в celesta скриптах. Свойства берется
	 * иззначения параметра activiti.enable главного файла общего файла
	 * app.propertes.
	 */
	private boolean enableActiviti = false;

	/**
	 * Переменная, которая сожержит в себе движок Activiti (сторонней
	 * компоненты) настроенный на текущую базу данных userdata.
	 */
	private ProcessEngine activitiProcessEngine = null;

	/**
	 * Переменная, которая содержит в себе значение: false - если в процессе
	 * запуска прилдожений не произошла ошибка с инициализацией celesta и
	 * calesta не инициализировалась. В противном случае переменная равна true.
	 */
	private Boolean isCelestaInitialized = false;

	/**
	 * Переменная, которая содержит в себе exception, который произошел при
	 * инициализации celesta в процессе запуска прилдожения Showcase. Если в
	 * процессе запуска произошла ошибка инициализации celesta ( переменная
	 * isCelestaInitialized = false), то необходимо при запуске на сервере
	 * Showcase jython скриптов celesta (когда пользоватлеь запрашивает инфу от
	 * челесты) на клиента Showcase возвращать ошибку, что челеста не была
	 * инициализирована и причину этого, хранящуюся в этом exception.
	 */
	private Exception celestainitializationException = null;

	private final CacheManager cacheManager = new CacheManager();

	/**
	 * Словарь (карта) соответствия событий Activiti и Celesta-скриптов.
	 */
	private Map<ActivitiEventType, String> activitiEventScriptDictionary =
		new HashMap<ActivitiEventType, String>();

	public synchronized Collection<LoggingEventDecorator> getLastLogEvents() {
		return lastLogEvents;
	}

	public synchronized Collection<LoggingEventDecorator> getLastLogEvents(
			final ServletRequest request) {
		SortedMap<String, List<String>> params;
		try {
			params = ServletUtils.prepareURLParamsMap((HttpServletRequest) request);
		} catch (UnsupportedEncodingException e) {
			throw new ServerLogicError(e);
		}

		return getLastLogEvents(params);
	}

	public Collection<LoggingEventDecorator> getLastLogEvents(
			final Map<String, List<String>> params) {
		Collection<LoggingEventDecorator> result = new ArrayList<>();

		skip: for (LoggingEventDecorator event : lastLogEvents) {
			if (params != null) {
				for (Map.Entry<String, List<String>> entry : params.entrySet()) {
					if (!event.isSatisfied(entry.getKey(), entry.getValue().get(0))) {
						continue skip;
					}
				}
			}
			result.add(event);
		}
		return result;
	}

	public synchronized void addLogEvent(final LoggingEventDecorator event) {
		lastLogEvents.add(event);
	}

	private AppInfoSingleton() {
		super();
	}

	public Map<String, SessionInfo> getSessionInfoMap() {
		return sessionInfoMap;
	}

	public String getServletContainerVersion() {
		return servletContainerVersion;
	}

	public void setServletContainerVersion(final String aServletContainerVersion) {
		servletContainerVersion = aServletContainerVersion;
	}

	public static AppInfoSingleton getAppInfo() {
		return INSTANCE;
	}

	/**
	 * Добавляет сессию в список без параметров URL. Функция используется в
	 * тестовых целях.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void addSession(final String sessionId) {
		getOrInitSessionInfoObject(sessionId);
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("Число пользовательских сессий: " + getAppInfo().sessionInfoMap.size());
		}
	}

	/**
	 * Получает идентификатор Userdata из параметров URL.
	 * 
	 * @return - строку с идентификатором.
	 * @param aMap
	 *            - параметры URL.
	 */
	private String getUserdataIdFromURLParams(final Map<String, ArrayList<String>> aMap) {
		String userdataId = null;

		for (Map.Entry<String, ArrayList<String>> entry : aMap.entrySet()) {
			if (ExchangeConstants.URL_PARAM_USERDATA.equals(entry.getKey())) {
				if (aMap.get(entry.getKey()) != null) {
					userdataId = Arrays.toString(entry.getValue().toArray()).trim();
					userdataId = userdataId.replace("[", "").replace("]", "");
					break;
				}
			}
		}

		return userdataId;
	}

	public Map<String, UserData> getUserdatas() {
		return userdatas;
	}

	/**
	 * Добавляет UserData в список.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @param path
	 *            Путь к userdata
	 */
	public void addUserData(final String userdataId, final String path) {
		userdatas.put(userdataId, new UserData(path));
	}

	/**
	 * Возвращает UserData по его идентификатору.
	 * 
	 * @param userdataId
	 *            Идентификатор UserData
	 * 
	 * @return UserData
	 */
	public UserData getUserData(final String userdataId) {
		UserData us = null;
		if (userdataId != null) {
			us = userdatas.get(userdataId);
		}
		return us;
	}

	/**
	 * Устанавливает признак authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param authViaAuthServer
	 *            - признак того, что аутентификация прошла через AuthServer.
	 */
	public void setAuthViaAuthServerForSession(final String sessionId,
			final boolean authViaAuthServer) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthViaAuthServer(authViaAuthServer);
	}

	/**
	 * Получает значение признака authViaAuthServer для сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - значение признака.
	 */
	public boolean getAuthViaAuthServerForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.isAuthViaAuthServer();
	}

	/**
	 * Устанавливает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @param pass
	 *            - пароль.
	 */
	public void setAuthServerCrossAppPasswordForSession(final String sessionId, final String pass) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		si.setAuthServerCrossAppPassword(pass);
	}

	/**
	 * Получает временный уникальный пароль для пользователя, который
	 * аутентифицировался через AuthServer.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - пароль.
	 */
	public String getAuthServerCrossAppPasswordForSession(final String sessionId) {
		SessionInfo si = getOrInitSessionInfoObject(sessionId);
		return si.getAuthServerCrossAppPassword();
	}

	public Integer getGridColumnGapWidth() {
		return getUserdatas().get(getCurUserDataId()).getGridColumnGapWidth();
	}

	/**
	 * Инициализирует пустой объект с информацией о сессии в карте сессий.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 * @return - объект с информацией о сессии.
	 */
	public SessionInfo getOrInitSessionInfoObject(final String sessionId) {
		SessionInfo res = sessionInfoMap.get(sessionId);
		if (res == null) {
			res = new SessionInfo();
			sessionInfoMap.put(sessionId, res);
		}
		return res;
	}

	/**
	 * Удаляет информацию о сессии.
	 * 
	 * @param sessionId
	 *            - идентификатор сессии.
	 */
	public void removeSessionInfo(final String sessionId) {
		sessionInfoMap.remove(sessionId);
	}

	/**
	 * Очищает карту сессий.
	 */
	public void clearSessions() {
		sessionInfoMap.clear();
	}

	public String getCurUserDataId() {
		String res = curUserDataId.get();
		return res;
	}

	public String getCurUserDataIdSafe() {
		String res = curUserDataId.get();
		return res != null ? res : "";
	}

	/**
	 * Устанавливает новое значение текущей userdata.
	 * 
	 * @param aMap
	 *            - параметры URL.
	 */
	public void setCurUserDataIdFromMap(final Map<String, ArrayList<String>> aMap) {
		String userDataId = getUserdataIdFromURLParams(aMap);
		if (userDataId == null) {
			userDataId = ExchangeConstants.DEFAULT_USERDATA;
		}

		if (!userdatas.containsKey(userDataId)) {
			throw new NoSuchUserDataException(userDataId);
		}

		curUserDataId.set(userDataId);
	}

	/**
	 * Метод для прямой установки currentUserDataId.
	 * 
	 * @param aUserDataId
	 *            - новое значение currentUserDataId.
	 */
	public void setCurUserDataId(final String aUserDataId) {
		curUserDataId.set(aUserDataId);
	}

	public Object getElementState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		Cache cache = cacheManager.getCache(GRID_STATE_CACHE);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		Element el = cache.get(key);
		if (el != null) {
			return el.getValue();
		}
		return null;
	}

	private String getSessionKeyForCaching(final String sessionId,
			final DataPanelElementInfo dpei, final CompositeContext context) {
		return sessionId + AppInfoSingleton.getAppInfo().getCurUserDataId()
				+ dpei.getKeyForCaching(context);
	}

	public void storeElementState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context, final Object state) {
		Cache cache = cacheManager.getCache(GRID_STATE_CACHE);
		String key = getSessionKeyForCaching(sessionId, dpei, context);
		Element cacheEl = new Element(key, state);
		cache.put(cacheEl);
	}

	public UserData getCurUserData() {
		return userdatas.get(getCurUserDataId());
	}

	public String getWebAppPath() {
		return webAppPath;
	}

	public void setWebAppPath(final String aPath) {
		webAppPath = aPath;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public String getUserdataRoot() {
		return userdataRoot;
	}

	public void setUserdataRoot(final String aUserdataRoot) {
		userdataRoot = aUserdataRoot;
	}

	public SortedSet<String> getExecutedProc() {
		return executedProc;
	}

	public void addExecutedProc(final String procName) {
		executedProc.add(procName);
	}

	public String getUserDataLogConfFile() {
		return userDataLogConfFile;
	}

	public void setUserDataLogConfFile(final String aUserDataLogConfFile) {
		userDataLogConfFile = aUserDataLogConfFile;
	}

	public void initWebConsole() {
		lastLogEvents = new LastLogEvents();
	}

	public Boolean getIsCelestaInitialized() {
		return isCelestaInitialized;
	}

	public void setIsCelestaInitialized(final Boolean aisCelestaInitialized) {
		this.isCelestaInitialized = aisCelestaInitialized;
	}

	public Exception getCelestaInitializationException() {
		return celestainitializationException;
	}

	public void setCelestaInitializationException(final Exception acelestainitializationException) {
		this.celestainitializationException = acelestainitializationException;
	}

	public boolean isEnableLogLevelInfo() {
		return enableLogLevelInfo;
	}

	public void setEnableLogLevelInfo(final boolean aEnableLogLevelInfo) {
		enableLogLevelInfo = aEnableLogLevelInfo;
	}

	public boolean isEnableLogLevelWarning() {
		return enableLogLevelWarning;
	}

	public void setEnableLogLevelWarning(final boolean aEnableLogLevelWarning) {
		enableLogLevelWarning = aEnableLogLevelWarning;
	}

	public boolean isEnableLogLevelError() {
		return enableLogLevelError;
	}

	public void setEnableLogLevelError(final boolean aEnableLogLevelError) {
		enableLogLevelError = aEnableLogLevelError;
	}

	public boolean isEnableActiviti() {
		return enableActiviti;
	}

	public void setEnableActiviti(final boolean aenableActiviti) {
		this.enableActiviti = aenableActiviti;
	}

	public ProcessEngine getActivitiProcessEngine() {
		return activitiProcessEngine;
	}

	public void setActivitiProcessEngine(final ProcessEngine aactivitiProcessEngine) {
		this.activitiProcessEngine = aactivitiProcessEngine;
	}

	public Map<ActivitiEventType, String> getActivitiEventScriptDictionary() {
		return activitiEventScriptDictionary;
	}

	public void setActivitiEventScriptDictionary(
			Map<ActivitiEventType, String> anActivitiEventScriptDictionary) {
		this.activitiEventScriptDictionary = anActivitiEventScriptDictionary;
	}
}
