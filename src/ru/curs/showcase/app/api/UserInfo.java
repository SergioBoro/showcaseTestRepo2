package ru.curs.showcase.app.api;

/**
 * Информация о деталях пользователя (таких как e-mail, sid пользователя, полное
 * имя пользователя, телефон), например получаемая из AuthServer.
 * 
 * @author anlug
 * 
 */
public final class UserInfo implements SerializableElement {

	private static final long serialVersionUID = -5321878237734917288L;

	/**
	 * Группа провайдеров пользователя или домен.
	 */
	private String groupProviders;
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
	// /**
	// * Дополнительный параметр.
	// */
	// private String additionalParameter;
	private String[] additionalParameters;

	/**
	 * Код ответа AuthServer.
	 */
	private int responseCode;

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String agroupProviders,
			final String... anAdditionalParameter) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = agroupProviders;
		this.additionalParameters = anAdditionalParameter;
	}

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String... anAdditionalParameter) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = null;
		this.additionalParameters = anAdditionalParameter;
	}

	public UserInfo(final String aLogin, final String aSid, final String aName,
			final String aEmail, final String aPhone, final String agroupProviders) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
		this.groupProviders = agroupProviders;
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

	public String getGroupProviders() {
		return groupProviders;
	}

	public void setGrouProviders(final String agroupProviders) {
		this.groupProviders = agroupProviders;
	}

	public String[] getAdditionalParameters() {
		return additionalParameters;
	}
}