package ru.curs.showcase.runtime;

import java.util.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

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

	private String sid;

	private final Map<String, Object> elementStates = Collections
			.synchronizedMap(new HashMap<String, Object>());

	public Object getElementState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		String key = generateStateKey(sessionId, dpei, context);
		return elementStates.get(key);
	}

	private String generateStateKey(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context) {
		return sessionId + AppInfoSingleton.getAppInfo().getCurUserDataId() + dpei.getFullId()
				+ context.getMain();
	}

	public void storeElementState(final String sessionId, final DataPanelElementInfo dpei,
			final CompositeContext context, final Object state) {
		String key = generateStateKey(sessionId, dpei, context);
		elementStates.put(key, state);
	}

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

	public String getSid() {
		return sid;
	}

	public void setSid(final String aSid) {
		sid = aSid;
	}
}
