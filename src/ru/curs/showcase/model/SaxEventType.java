package ru.curs.showcase.model;

/**
 * Тип события в SAX парсере.
 * 
 * @author den
 * 
 */
public enum SaxEventType {
	/**
	 * Начало тэга.
	 */
	STARTTAG,
	/**
	 * Конец тэга.
	 */
	ENDTAG,
	/**
	 * Содержимое тэга.
	 */
	CHARACTERS

}
