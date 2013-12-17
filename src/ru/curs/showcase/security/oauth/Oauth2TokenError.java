package ru.curs.showcase.security.oauth;

import javax.xml.bind.annotation.*;

/**
 * Детали ошибки получения oauth токена.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
public class Oauth2TokenError {
	@XmlElement(name = "error")
	private String error;
	@XmlElement(name = "error_description")
	private String errorDescription;

	public String getError() {
		return error;
	}

	public void setAccessToken(final String sError) {
		this.error = sError;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(final String sErrorDescription) {
		this.errorDescription = sErrorDescription;
	}

}
