package ru.curs.showcase.util;

import java.io.File;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Источник XSD схем.
 * 
 * @author den
 * 
 */
interface XSDSource {

	/**
	 * Возвращает файл схемы.
	 * 
	 * @param fileName
	 *            - имя файла.
	 * @return - файл.
	 */
	File getSchema(String fileName);

	/**
	 * Возвращает тип исключения при проверке схемы из данного источника.
	 * 
	 * @return - тип.
	 */
	ExceptionType getExceptionType();
}
