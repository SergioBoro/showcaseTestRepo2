package ru.curs.showcase.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import ru.curs.showcase.app.api.UserInfo;

/**
 * 
 * A holder of selected HTTP details and UserInfo related to a web
 * authentication request.
 * 
 * @author anlug
 * 
 */
public class UserAndSessionDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 8550679539357144098L;

	private UserInfo userInfo = null;

	private Boolean authViaAuthServer = false;

	public UserAndSessionDetails(final HttpServletRequest request) {
		super(request);
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(final UserInfo auserInfo) {
		this.userInfo = auserInfo;
	}

	public Boolean isAuthViaAuthServer() {
		return authViaAuthServer;
	}

	public void setAuthViaAuthServer(final Boolean aauthViaAuthServer) {
		this.authViaAuthServer = aauthViaAuthServer;
	}

}
