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
	 * Тип данных в теле ответа на рест запрос (параметр Content-Type в хедере
	 * ответа).
	 */
	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String acontentType) {
		this.contentType = acontentType;
	}

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
		this(aresponseData, aresponseCode, "application/json");
	}

	public JythonRestResult(final String aresponseData, final Integer aresponseCode,
			final String acontentType) {
		super();
		this.responseData = aresponseData;
		this.responseCode = aresponseCode;
		this.contentType = acontentType;
	}

}
