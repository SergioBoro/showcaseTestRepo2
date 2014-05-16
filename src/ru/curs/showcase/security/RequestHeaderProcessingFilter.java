package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.app.server.PreProcessFilter;
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

		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		// установка деталей внутреннего пользователя
		userAndSessionDetails.setUserInfo(new UserInfo(username, null, username, null, null,
				domain));

		authRequest.setDetails(userAndSessionDetails);

		// обработчик устанавливающий что будет происходить в случае когда в
		// процессе аутентификации произошла ошибка - тоесть redirect на
		// страницу ввода пароля
		setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(final HttpServletRequest request,
					final HttpServletResponse response, final AuthenticationException exception)
					throws IOException, ServletException {
				super.setDefaultFailureUrl("/" + PreProcessFilter.LOGIN_PAGE + "?error=true");
				super.onAuthenticationFailure(request, response, exception);

			}

		});
		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		return authentication;
	}
}