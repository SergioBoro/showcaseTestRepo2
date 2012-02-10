package ru.curs.showcase.runtime;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.*;

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
	private static AppInfoSingleton instance;

	/**
	 * Карта пользовательских сессий.
	 */
	private final Map<String, SessionInfo> sessionInfoMap = Collections
			.synchronizedMap(new HashMap<String, SessionInfo>());

	/**
	 * Идентификатор userdata в текущем запросе.
	 */
	private final ThreadLocal<String> curUserDataId = new ThreadLocal<String>();

	/**
	 * Версия контейнера сервлетов.
	 */
	private String servletContainerVersion;

	private final SortedSet<LoggingEventDecorator> lastLogEvents = new LastLogEvents();

	private String webAppPath;

	private String userdataRoot;

	private final CacheManager cacheManager = new CacheManager();

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
		if (instance == null) {
			instance = new AppInfoSingleton();
		}
		return instance;
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
		LOGGER.info("Число пользовательских сессий: " + getAppInfo().sessionInfoMap.size());
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

}
