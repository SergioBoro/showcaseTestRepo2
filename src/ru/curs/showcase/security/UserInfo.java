package ru.curs.showcase.security;

import java.io.InputStream;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Информация о пользователе, полученная из LDAP.
 * 
 */
final class UserInfo implements UserData {
	/**
	 * Логин пользователя.
	 */
	private final String login;
	/**
	 * SID пользователя.
	 */
	private final String sid;
	/**
	 * Имя пользователя.
	 */
	private final String name;
	/**
	 * Почтовый адрес пользователя.
	 */
	private final String email;
	/**
	 * Телефон пользователя.
	 */
	private final String phone;

	/**
	 * Код ответа AuthServer.
	 */
	private int responseCode;

	UserInfo(final String aLogin, final String aSid, final String aName, final String aEmail,
			final String aPhone) {
		this.login = aLogin;
		this.sid = aSid;
		this.name = aName;
		this.email = aEmail;
		this.phone = aPhone;
	}

	/**
	 * Метод для обработки полученного XML для извлечения из него данных.
	 * 
	 * @param is
	 *            - поток.
	 * @throws TransformerException
	 */
	static List<UserData> parseStream(final InputStream is) throws TransformerException {
		final List<UserData> result = new LinkedList<UserData>();
		final ContentHandler ch = new DefaultHandler() {
			@Override
			public void startElement(final String uri, final String localName,
					final String prefixedName, final Attributes atts) throws SAXException {
				if ("user".equals(localName)) {
					UserInfo ui =
						new UserInfo(atts.getValue("login"), atts.getValue("SID"),
								atts.getValue("name"), atts.getValue("email"),
								atts.getValue("phone"));
					result.add(ui);
				}
			}
		};
		// TODO нужна ли здесь трансформация или хватит про SAXParser
		TransformerFactory.newInstance().newTransformer()
				.transform(new StreamSource(is), new SAXResult(ch));
		return result;
	}

	@Override
	public String getSid() {
		return sid;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getCaption() {
		return login;
	}

	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public void setResponseCode(final int aResponseCode) {
		responseCode = aResponseCode;
	}
}