package ru.curs.showcase.security.esia;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * ESIA провайдер авторизации.
 * 
 */
public class ESIAAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(final Authentication auth) {

		// auth.setAuthenticated(true);

		return auth;

	}

	@Override
	public boolean supports(final Class<?> arg0) {
		return ESIAAuthenticationToken.class.isAssignableFrom(arg0);
	}

}
