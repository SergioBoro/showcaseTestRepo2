package ru.curs.showcase.model.jython;

import ru.curs.showcase.app.api.UserMessage;

/**
 * DTO класс с сырыми данными для элементов Showcase: навигатора, инф. панели
 * или ее элементов. Данные передаются в виде строк.
 * 
 * @author den
 * 
 */
public final class JythonDTO {
	/**
	 * Сообщение для пользователя. По умолчанию не задается. Выдача сообщения
	 * пользователю означает, что данные получены не были!
	 */
	private UserMessage userMessage;
	/**
	 * Данные (в формате HTML или XML).
	 */
	private String data;
	/**
	 * Настройки элемента в формате XML.
	 */
	private String settings;

	public UserMessage getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(final UserMessage aUserMessage) {
		userMessage = aUserMessage;
	}

	public String getData() {
		return data;
	}

	public void setData(final String aData) {
		data = aData;
	}

	public String getSettings() {
		return settings;
	}

	public void setSettings(final String aSettings) {
		settings = aSettings;
	}

	public static JythonDTO createResult(final String aData, final String aSettings) {
		JythonDTO res = new JythonDTO();
		res.data = aData;
		res.settings = aSettings;
		return res;
	}

	public static JythonDTO createResult(final String aData) {
		JythonDTO res = new JythonDTO();
		res.data = aData;
		return res;
	}

	private JythonDTO() {
		super();
	}

	public static JythonDTO createError(final UserMessage aUserMessage) {
		JythonDTO res = new JythonDTO();
		res.userMessage = aUserMessage;
		return res;
	}
}
