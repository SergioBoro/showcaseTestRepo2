package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;

/**
 * Исключение, вызванное тем, что на сервер из клиентской части или из слоя
 * связи с данными передан элемент с некорректным состоянием.
 * 
 * @author den
 * 
 */
public class IncorrectElementException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES =
		"Получено описание элемента с некорректным состоянием или неподходящий по типу элемент";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 2762191427245015158L;

	public IncorrectElementException(final String message) {
		super(ExceptionType.SOLUTION, ERROR_MES + ": " + message);
	}

	public IncorrectElementException() {
		super(ExceptionType.SOLUTION, ERROR_MES);
	}

}
