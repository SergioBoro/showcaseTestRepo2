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

	/**
	 * @return the userInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo
	 *            the userInfo to set
	 */
	public void setUserInfo(final UserInfo auserInfo) {
		this.userInfo = auserInfo;
	}

	/**
	 * @return the authViaAuthServer
	 */
	public Boolean isAuthViaAuthServer() {
		return authViaAuthServer;
	}

	/**
	 * @param authViaAuthServer
	 *            the authViaAuthServer to set
	 */
	public void setAuthViaAuthServer(final Boolean aauthViaAuthServer) {
		this.authViaAuthServer = aauthViaAuthServer;
	}

}
