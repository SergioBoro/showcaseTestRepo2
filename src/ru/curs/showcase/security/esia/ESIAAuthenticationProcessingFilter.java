package ru.curs.showcase.security.esia;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.security.AuthFailureHandler;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Фильтр ESIA авторизации.
 * 
 */
public class ESIAAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER =
		LoggerFactory.getLogger(ESIAAuthenticationProcessingFilter.class);

	protected ESIAAuthenticationProcessingFilter() {
		super("/esia");
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {

		String auth = request.getParameter("auth");
		if (auth != null && !auth.isEmpty()) {
			response.sendRedirect(ESIAManager.getAuthorizationURL());
			return null;
		}

		String code = request.getParameter("code");

		ESIAAuthenticationToken authRequest = null;
		boolean esiaAuthenticated = false;
		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);
		if (code != null) {

			ESIAUserInfo esiaUI = ESIAManager.getUserInfo(code);

			authRequest = new ESIAAuthenticationToken(esiaUI.getSnils());

			UserInfo ui = new UserInfo(esiaUI.getSnils(),
					String.valueOf(esiaUI.getOid()), esiaUI.getLastName() + " "
							+ esiaUI.getFirstName() + " " + esiaUI.getMiddleName(),
					null, null, (String) null);
			ui.setSnils(esiaUI.getSnils());
			ui.setGender(esiaUI.getGender());
			ui.setBirthDate(esiaUI.getBirthDate());
			ui.setBirthPlace(esiaUI.getBirthPlace());

			userAndSessionDetails.setUserInfo(ui);

			authRequest.setDetails(userAndSessionDetails);

			esiaAuthenticated = esiaUI.isTrusted();

		} else {

			authRequest = new ESIAAuthenticationToken("notAuthenticated");

			authRequest.setDetails(userAndSessionDetails);

			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				String error = request.getParameter("error");
				String errorDescription = request.getParameter("error_description");
				LOGGER.error(
						"Ошибка аутентификации через ESIA: " + error + ", " + errorDescription);
			}

			esiaAuthenticated = false;

		}

		AuthFailureHandler authFailureHandler = new AuthFailureHandler("ESIA");
		authFailureHandler.add("code", "notAuthenticated");
		setAuthenticationFailureHandler(authFailureHandler);

		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);

		if (esiaAuthenticated) {
			request.getSession(false).setAttribute("esiaAuthenticated", "true");
			request.getSession(false).setAttribute("Details", userAndSessionDetails);
		} else {
			request.getSession(false).setAttribute("esiaAuthenticated", "false");
			authentication.setAuthenticated(false);
		}

		return authentication;

	}

}
