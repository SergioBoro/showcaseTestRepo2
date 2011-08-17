package ru.curs.showcase.util;

/**
 * Тип Файла настроек приложения.
 * 
 * @author den
 * 
 */
public enum SettingsFileType {
	PATH_PROPERTIES("Файл с путями к другим ресурсам"),
	APP_PROPERTIES("Файл с общими настройками решения"),
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
