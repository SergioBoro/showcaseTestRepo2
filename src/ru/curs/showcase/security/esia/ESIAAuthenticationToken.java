package ru.curs.showcase.security.esia;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * ESIA токен аутентификации.
 * 
 */
public class ESIAAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;
	private final String code;

	public ESIAAuthenticationToken(final String sCode) {
		super(null);
		this.code = sCode;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.code;
	}
}
