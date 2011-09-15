package ru.curs.showcase.util.exception;

import ru.curs.showcase.app.api.ExceptionType;

/**
 * Ошибка при создании объекта. Используется для перехвата исключений,
 * возникающих при создании объектов - как из системных библиотек, так и своих,
 * а также при ошибке в момент вызова class.newInstance().
 * 
 * @author den
 * 
 */
public class CreateObjectError extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES = "Ошибка создания объекта";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7749067251383439818L;

	public CreateObjectError(final Throwable cause) {
		super(ExceptionType.APP, ERROR_MES, cause);
	}

}
