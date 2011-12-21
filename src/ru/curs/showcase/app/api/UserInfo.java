package ru.curs.showcase.app.api;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Информация о деталях пользователя (таких как e-mail, sid пользователя, полное
 * имя пользователя, телефон), например получаемая из AuthServer.
 * 
 * @author anlug
 * 
 */
public final class UserInfo implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5321878237734917288L;
	/**
	 * Логин пользователя.
	 */
	private String login;
	/**
	 * SID пользователя.
	 */
	private String sid;
	/**
	 * Имя пользователя.
	 */
	private String name;
	/**
	 * Почтовый адрес пользователя.
	 */
	private String email;
	/**
	 * Телефон пользователя.
	 */
	private String phone;

	/**
	 * Код ответа AuthServer.
	 */
	private int responseCode;

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
	}

	public UserInfo() {
		super();
	}

	public String getSid() {
		return sid;
	}

	public String getEmail() {
		return email;
	}

	public String getCaption() {
		return login;
	}

	public String getFullName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(final int aResponseCode) {
		responseCode = aResponseCode;
	}
}