package ru.curs.showcase.security.oauth;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

/**
 * Данные Oauth2 токена.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement(name = "oauth2token")
public class Oauth2Token implements Serializable {
	private static final long serialVersionUID = 1L;

	private String accessToken;
	private String tokenType;
	private int expiresIn;
	private String scope;
	private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	@XmlElement(name = "access_token")
	public void setAccessToken(final String sAccessToken) {
		this.accessToken = sAccessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	@XmlElement(name = "token_type")
	public void setTokenType(final String sTokenType) {
		this.tokenType = sTokenType;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	@XmlElement(name = "expires_in")
	public void setExpiresIn(final int iExpiresIn) {
		this.expiresIn = iExpiresIn;
	}

	public String getScope() {
		return scope;
	}

	@XmlElement(name = "scope")
	public void setScope(final String sScope) {
		this.scope = sScope;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	@XmlElement(name = "refresh_token")
	public void setRefreshToken(final String sRefreshToken) {
		this.refreshToken = sRefreshToken;
	}

}
