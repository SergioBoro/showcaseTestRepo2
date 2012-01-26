package ru.curs.showcase.core;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, вызванное тем, что на сервер из клиентской части или из слоя
 * связи с данными передан элемент с некорректным состоянием.
 * 
 * @author den
 * 
 */
public class IncorrectElementException extends BaseException {

	private static final long serialVersionUID = 2762191427245015158L;

	public IncorrectElementException(final String message) {
		super(ExceptionType.SOLUTION, message);
	}

}
