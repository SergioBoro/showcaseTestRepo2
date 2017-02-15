package ru.curs.showcase.security.esia;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * Фильтр ESIA авторизации.
 * 
 */
public class ESIAAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

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

		ESIAAuthenticationToken authRequest = new ESIAAuthenticationToken(code);
		UserAndSessionDetails userAndSessionDetails = new UserAndSessionDetails(request);

		ESIAUserInfo esiaUI = ESIAManager.getUserInfo(code);

		// System.out.println("oid = " + esiaUI.getOid());
		// System.out.println("snils = " + esiaUI.getSnils());
		// System.out.println("trusted = " + esiaUI.isTrusted());
		// System.out.println("firstName = " + esiaUI.getFirstName());
		// System.out.println("lastName = " + esiaUI.getLastName());
		// System.out.println("middleName = " + esiaUI.getMiddleName());
		// System.out.println("gender = " + esiaUI.getGender());
		// System.out.println("birthDate = " + esiaUI.getBirthDate());
		// System.out.println("birthPlace = " + esiaUI.getBirthPlace());

		UserInfo ui = new UserInfo(esiaUI.getSnils(), String.valueOf(esiaUI.getOid()),
				esiaUI.getLastName() + " " + esiaUI.getFirstName() + " " + esiaUI.getMiddleName(),
				null, null, (String) null);

		userAndSessionDetails.setUserInfo(ui);

		authRequest.setDetails(userAndSessionDetails);

		if (esiaUI.isTrusted()) {
			request.getSession(false).setAttribute("esiaAuthenticated", "true");
		} else {
			request.getSession(false).setAttribute("esiaAuthenticated", "false");
		}

		// AuthFailureHandler authFailureHandler = new
		// AuthFailureHandler("OAUTH2");
		// authFailureHandler.add("code", code);
		// setAuthenticationFailureHandler(authFailureHandler);

		Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
		return authentication;

	}

}
