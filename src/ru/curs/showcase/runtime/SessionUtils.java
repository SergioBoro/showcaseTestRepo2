package ru.curs.showcase.runtime;

import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.security.oauth.Oauth2Token;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Вспомогательные функции для получение информации о текущей сессии.
 * 
 * @author anlug
 * 
 */
public final class SessionUtils {

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	public static final String TEST_SESSION = "testSession";
	public static final String TEST_SID = "testSID";

	private SessionUtils() {
		throw new UnsupportedOperationException();
	}

	private static UserAndSessionDetails getUserAndSessionDetails() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return (UserAndSessionDetails) SecurityContextHolder.getContext().getAuthentication()
					.getDetails();
		} else {
			return null;
		}
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
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getSessionId();
		} else {
			return TEST_SESSION;
		}
	}

	public static String getCurrentUserSID() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getSid();
		} else {
			return TEST_SID;
		}

	}

	public static String getCurrentUserEmail() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getEmail();
		} else {
			return null;
		}
	}

	public static String getCurrentUserFullName() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getFullName();
		} else {
			return null;
		}
	}

	public static String getCurrentUserPhone() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getUserInfo().getPhone();
		} else {
			return null;
		}
	}

	public static String getRemoteAddress() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getRemoteAddress();
		}
		return null;
	}

	public static Oauth2Token getOauth2Token() {
		if (getUserAndSessionDetails() != null) {
			return getUserAndSessionDetails().getOauth2Token();
		}
		return null;
	}
}
