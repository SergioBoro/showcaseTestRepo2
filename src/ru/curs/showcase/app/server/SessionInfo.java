package ru.curs.showcase.app.server;

/**
 * Информация о сессии пользователя.
 * 
 * @author den
 * 
 */
public class SessionInfo {
	/**
	 * Параметры из URL.
	 */
	private String context;

	/**
	 * Признак того, что авторизация была произведена через AuthServer.
	 */
	private boolean authViaAuthServer = false;

	/**
	 * Уникальный временный пароль, сгенерированный для данной сессии при
	 * аутентификации через AuthServer.
	 */
	private String authServerCrossAppPassword = null;

	/**
	 * Идентификатор userdata из URL.
	 */
	private String userdataId;

	public SessionInfo() {
		super();
	}

	public SessionInfo(final String aSessionContext) {
		super();
		context = aSessionContext;
	}

	public String getContext() {
		return context;
	}

	public void setContext(final String aContext) {
		context = aContext;
	}

	public String getAuthServerCrossAppPassword() {
		return authServerCrossAppPassword;
	}

	public void setAuthServerCrossAppPassword(final String aAuthServerCrossAppPassword) {
		authServerCrossAppPassword = aAuthServerCrossAppPassword;
	}

	public boolean getAuthViaAuthServer() {
		return authViaAuthServer;
	}

	public void setAuthViaAuthServer(final boolean aAuthViaAuthServer) {
		authViaAuthServer = aAuthViaAuthServer;
	}

	public String getUserdataId() {
		return userdataId;
	}

	public void setUserdataId(final String aUserdataId) {
		userdataId = aUserdataId;
	}
}
