package ru.curs.showcase.security;

import java.util.Arrays;

import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.DigestUtils;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;

public class IPTokenBasedRememberMeServices extends TokenBasedRememberMeServices {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IPTokenBasedRememberMeServices.class);

	private static final ThreadLocal<HttpServletRequest> requestHolder =
		new ThreadLocal<HttpServletRequest>();

	public HttpServletRequest getContext() {
		return requestHolder.get();
	}

	public void setContext(HttpServletRequest context) {
		requestHolder.set(context);
	}

	protected String getUserIPAddress(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	@Override
	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication) {
		try {
			setContext(request);
			String username = request.getParameter("j_username");
			String password = request.getParameter("j_password");
			String domain = request.getParameter("j_domain");
			SignedUsernamePasswordAuthenticationToken authRequest =
				new SignedUsernamePasswordAuthenticationToken(username, password);
			SecurityContextHolder.getContext().setAuthentication(authRequest);
			UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
			userAndSessionDetails.setUserInfo(new UserInfo(username, username, username, null,
					null, (String) null));
			userAndSessionDetails.setOauth2Token(null);
			userAndSessionDetails.setAuthViaAuthServer(false);
			authRequest.setDetails(userAndSessionDetails);
			// SecurityContextHolder.getContext().setAuthentication(successfulAuthentication);
			request.getSession(false).setAttribute("remembermeAuthenticated", "true");
			AppInfoSingleton.getAppInfo().setSesid(request.getSession(false).getId());
			// try {
			// Celesta.getInstance().login(request.getSession(false).getId(),
			// ((UserAndSessionDetails)
			// authRequest.getDetails()).getUserInfo().getSid());
			// } catch (CelestaException e) {
			// e.printStackTrace();
			// if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
			// LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta",
			// e);
			// }
			// }

			super.onLoginSuccess(request, response, authRequest);
		} finally {
			setContext(null);
		}
	}

	@Override
	protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		String signature =
			DigestUtils.md5DigestAsHex((username + ":" + tokenExpiryTime + ":" + password + ":"
					+ getKey() + ":" + getUserIPAddress(getContext())).getBytes());
		return signature;
		// SignedUsernamePasswordAuthenticationToken authToken =
		// new SignedUsernamePasswordAuthenticationToken(username, password);
		// SecurityContextHolder.getContext().setAuthentication(authToken);
		//
		// return super.makeTokenSignature(tokenExpiryTime, username, password);
	}

	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request,
			HttpServletResponse response) {
		if (tokens.length < 4) {
			String pwd = request.getParameter("j_password");

			String[] tokensWithPassword = Arrays.copyOf(tokens, tokens.length + 1);
			tokensWithPassword[tokensWithPassword.length - 1] = pwd;
			// getUserIPAddress(request);
			super.setCookie(tokensWithPassword, maxAge, request, response);
		} else
			super.setCookie(tokens, maxAge, request, response);
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			setContext(request);
			String sss = cookieTokens[3];
			SignedUsernamePasswordAuthenticationToken authToken =
				new SignedUsernamePasswordAuthenticationToken(cookieTokens[0], cookieTokens[3]);
			UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
			userAndSessionDetails.setUserInfo(new UserInfo(cookieTokens[0], cookieTokens[0],
					cookieTokens[0], null, null, (String) null));
			userAndSessionDetails.setOauth2Token(null);
			userAndSessionDetails.setAuthViaAuthServer(false);
			authToken.setDetails(userAndSessionDetails);
			SecurityContextHolder.getContext().setAuthentication(authToken);
			request.getSession().setAttribute("remembermeAuthenticated", "true");
			try {
				Celesta.getInstance().login(AppInfoSingleton.getAppInfo().getSesid(),
						((UserAndSessionDetails) authToken.getDetails()).getUserInfo().getSid());
			} catch (CelestaException e) {
				e.printStackTrace();
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta", e);
				}
			}
		} finally {
			// setContext(null);
		}
		return super.processAutoLoginCookie(Arrays.copyOf(cookieTokens, cookieTokens.length - 1),
				request, response);

	}
}
