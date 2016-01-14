package ru.curs.showcase.security;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;

//imports omitted
/**
 * Тестовый фильтр аутентификации Spring Security.
 * 
 * @author den
 * 
 */

public class RequestHeaderProcessingFilter extends AbstractAuthenticationProcessingFilter {

	/**
	 * Параметр HttpServletRequest с именем пользователя.
	 */
	private static final String USERNAME_HEADER = "j_username";

	/**
	 * Параметр HttpServletRequest с паролем пользователя.
	 */
	private static final String PASS_HEADER = "j_password";

	private static final String DOMAIN = "j_domain";

	protected RequestHeaderProcessingFilter() {
		super("/j_spring_security_check");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter(USERNAME_HEADER);
		String password = request.getParameter(PASS_HEADER);
		String domain = request.getParameter(DOMAIN);
		SignedUsernamePasswordAuthenticationToken authRequest =
			new SignedUsernamePasswordAuthenticationToken(username, password);

		HttpSession session = request.getSession();
		AppInfoSingleton.getAppInfo().setSesid(session.getId());

		Enumeration en = session.getAttributeNames();
		Map<String, Object> values = new HashMap<String, Object>();
		while (en.hasMoreElements()) {
			String s = (String) en.nextElement();
			values.put(s, session.getAttribute(s));
		}

		session.invalidate();

		HttpSession newSession = request.getSession(true);
		for (String str : values.keySet()) {
			newSession.setAttribute(str, values.get(str));
		}

		request.getSession(false).setAttribute("username", username);
		// request.getSession(false).setAttribute("password", password);

		request.getSession(false).setAttribute("newSession", request.getSession(false));

		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		// установка деталей внутреннего пользователя
		userAndSessionDetails.setUserInfo(new UserInfo(username, null, username, null, null,
				domain));

		authRequest.setDetails(userAndSessionDetails);

		// обработчик устанавливающий что будет происходить в случае когда в
		// процессе аутентификации произошла ошибка
		AuthFailureHandler authFailureHandler = new AuthFailureHandler();
		authFailureHandler.add("username", username);
		authFailureHandler.add("password", password);
		authFailureHandler.add("domain", domain);
		setAuthenticationFailureHandler(authFailureHandler);
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		return authentication;
	}
}