package ru.curs.showcase.model;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае отсутствия файла.
 * 
 */
public class FileIsAbsentException extends BaseException {
	private static final long serialVersionUID = -6928633456458398538L;

	private static final String ERROR_MES = "Файл отсутствует.";

	public FileIsAbsentException() {
		super(ExceptionType.USER, ERROR_MES);
	}
}
