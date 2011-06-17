package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.services.*;

/**
 * Фабрика по созданию GeneralServerException на основе серверного Exception.
 * 
 * @author den
 * 
 */
public final class GeneralServerExceptionFactory {
	private GeneralServerExceptionFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основная функция фабрики.
	 * 
	 * @return - исключение.
	 * @param original
	 *            - оригинальное исключение.
	 */
	public static GeneralServerException build(final Throwable original) {
		GeneralServerException res =
			new GeneralServerException(original, getUserMessage(original));
		res.setOriginalExceptionClass(original.getClass().getName());
		res.setOriginalTrace(GeneralServerException.getStackText(original));
		res.setOriginalMessage(getOriginalMessage(original));
		res.setType(getType(original));
		return res;
	}

	private static ExceptionType getType(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getType();
		}
		return ExceptionType.JAVA;
	}

	private static UserMessage getUserMessage(final Throwable exc) {
		if (exc instanceof ValidateInDBException) {
			return ((ValidateInDBException) exc).getUserMessage();
		}
		return new UserMessage(getMessageText(exc), MessageType.ERROR);
	}

	private static String getMessageText(final Throwable original) {
		if (original.getLocalizedMessage() != null) {
			return original.getLocalizedMessage();
		} else {
			return original.getClass().getName();
		}
	}

	private static String getOriginalMessage(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getOriginalMessage();
		}
		return null;
	}
}
