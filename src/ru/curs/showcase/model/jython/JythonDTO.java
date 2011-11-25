package ru.curs.showcase.model.jython;

/**
 * DTO класс с сырыми данными для элементов Showcase: навигатора, инф. панели
 * или ее элементов. Данные передаются в виде строк.
 * 
 * @author den
 * 
 */
public final class JythonDTO {
	/**
	 * Данные (в формате HTML или XML).
	 */
	private String data;
	/**
	 * Настройки элемента в формате XML.
	 */
	private String settings;

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

	public JythonDTO(final String aData, final String aSettings) {
		super();
		data = aData;
		settings = aSettings;
	}

	public JythonDTO(final String aData) {
		super();
		data = aData;
	}
}
