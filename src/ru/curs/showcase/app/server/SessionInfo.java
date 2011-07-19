package ru.curs.showcase.app.server;

/**
 * Информация о сессии пользователя.
 * 
 * @author den
 * 
 */
public class SessionInfo {
	/**
	 * Признак того, что авторизация была произведена через AuthServer.
	 */
	private boolean authViaAuthServer = false;

	/**
	 * Уникальный временный пароль, сгенерированный для данной сессии при
	 * аутентификации через AuthServer.
	 */
	private String authServerCrossAppPassword = null;

	public String getAuthServerCrossAppPassword() {
		return authServerCrossAppPassword;
	}

	public void setAuthServerCrossAppPassword(final String aAuthServerCrossAppPassword) {
		authServerCrossAppPassword = aAuthServerCrossAppPassword;
	}

	public boolean isAuthViaAuthServer() {
		return authViaAuthServer;
	}

	public void setAuthViaAuthServer(final boolean aAuthViaAuthServer) {
		authViaAuthServer = aAuthViaAuthServer;
	}
}
