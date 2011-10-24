package ru.curs.showcase.util;

import org.slf4j.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Вспомогательные функции для получение информации о текущей сессии.
 * 
 * @author anlug
 * 
 */
public final class SessionUtils {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUtils.class);
	public static final String APP_PROP_READ_ERROR =
		"Не удалось считать security.authserverurl из app.properties";
	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	public static final String TEST_SESSION = "testSession";
	public static final String TEST_SID = "testSID";

	private SessionUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Возвращает имя пользователя из текущей сессии приложения.
	 * 
	 * @return - имя пользователя.
	 */
	public static String getCurrentSessionUserName() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		} else {
			return "";
		}
	}

	/**
	 * Возвращает идентификатор текущей сессии приложения.
	 * 
	 * @return - идентификатор текущей сессии приложения.
	 */
	public static String getCurrentSessionId() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return ((WebAuthenticationDetails) SecurityContextHolder.getContext()
					.getAuthentication().getDetails()).getSessionId();
		} else {
			return TEST_SESSION;
		}
	}

	/**
	 * Возвращает SID пользователя из текущей сессии приложения.
	 * 
	 * @return - SID пользователя.
	 */
	public static String getCurrentUserSID() {
		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			LOGGER.error(APP_PROP_READ_ERROR);
		}
		AuthServerUtils.init(url);
		String sessionId = getCurrentSessionId();
		if (AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(sessionId)) {
			UserData ud = AuthServerUtils.getTheAuthServerAlias().isAuthenticated(sessionId);
			if (ud != null) {
				return ud.getSid();
			}
		} else if (TEST_SESSION.equals(sessionId)) {
			return TEST_SID;
		}

		return null;
	}
}
