package ru.curs.showcase.core;

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
	FILE,
	/**
	 * файл, содержащий SQL код, выполняемый на сервере БД.
	 */
	SQL
}
