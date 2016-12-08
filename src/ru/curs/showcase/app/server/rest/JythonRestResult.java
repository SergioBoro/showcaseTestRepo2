package ru.curs.showcase.app.server.rest;

/**
 * Класс для обмена данными для Restfull сервисов между Showcase и jython
 * скриптами.
 * 
 * @author anlug
 * 
 */
public class JythonRestResult {
	/**
	 * Данные.
	 */
	private String responseData;

	/**
	 * Код возврата.
	 */
	private Integer responseCode;

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(final String aresponseData) {
		this.responseData = aresponseData;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(final Integer aresponseCode) {
		this.responseCode = aresponseCode;
	}

	public JythonRestResult(final String aresponseData, final Integer aresponseCode) {
		super();
		responseData = aresponseData;
		responseCode = aresponseCode;
	}

}
