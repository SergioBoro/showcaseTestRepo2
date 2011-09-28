package ru.curs.showcase.util.exception;

/**
 * Тип Файла настроек приложения.
 * 
 * @author den
 * 
 */
public enum SettingsFileType {
	PATH_PROPERTIES("Файл с общими настройками приложения"),
	APP_PROPERTIES("Файл с настройками userdata"),
	GRID_PROPERTIES("Профайл настроек грида"),
	VERSION("Файл версии приложения"),
	DATAPANEL("Файл инф. панели"),
	NAVIGATOR("Файл навигатора"),
	XFORM("Файл шаблона XForm"),
	XSLT("Файл XSL трансформации"),
	SOLUTION_MESSAGES("Файл с сообщениями решения для пользователя"),
	SCHEMA("Файл XSD схемы"),
	FRAME("Фрейм главной страницы"),
	SQLSCRIPT("Файл SQL скрипта");

	/**
	 * Название файла.
	 */
	private String name;

	SettingsFileType(final String aName) {
		name = aName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
	}
}
