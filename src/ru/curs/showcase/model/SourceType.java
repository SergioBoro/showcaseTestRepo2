package ru.curs.showcase.model;

/**
 * Тип источника для SourceSelector.
 * 
 * @author den
 * 
 */
public enum SourceType {
	/**
	 * Хранимая процедура - тип по умолчанию.
	 */
	SP,
	/**
	 * Jython скрипт на диске.
	 */
	JYTHON,
	/**
	 * Файл(ы) на диске.
	 */
	FILE
}
