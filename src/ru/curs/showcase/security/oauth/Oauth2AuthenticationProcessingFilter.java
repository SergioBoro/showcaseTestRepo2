package ru.curs.showcase.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.app.server.PreProcessFilter;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Фильтр Oauth2 авторизации.
 * 
 * @author bogatov
 * 
 */
public class Oauth2AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	protected Oauth2AuthenticationProcessingFilter() {
		super("/oauth");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {
		String auth = request.getParameter("auth");
		if (auth != null && !auth.isEmpty()) {
			response.sendRedirect("http://localhost:9080/oauth2/endpoint/DemoProvider/authorize"
					+ "?client_id=showcase&client_secret=secret&response_type=code");
			return null;
		}
		final String code = request.getParameter("code");
		Oauth2AuthenticationToken authRequest = new Oauth2AuthenticationToken(code);
		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		java.security.Principal principal = request.getUserPrincipal();
		userAndSessionDetails.setUserInfo(new UserInfo(principal != null ? principal.toString()
				: null, null, null, null, null, null));
		authRequest.setDetails(userAndSessionDetails);

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
