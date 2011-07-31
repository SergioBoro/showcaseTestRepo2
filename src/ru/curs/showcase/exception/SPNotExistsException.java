package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Исключение, возникающее при отсутствии в БД требуемой хранимой процедуры.
 * Является по сути частным случаем DBQueryException.
 * 
 * @author den
 * 
 */
public class SPNotExistsException extends BaseException {

	private static final String ERROR_MES = "Процедура '%s' отсутствует в БД";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1310610425002788976L;

	public SPNotExistsException(final String procName) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, procName));
	}
}
