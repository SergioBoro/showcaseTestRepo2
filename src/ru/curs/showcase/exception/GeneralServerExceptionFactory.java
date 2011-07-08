package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
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
		res.setContext(getContext(original));
		res.setMessageType(getMessageType(original));
		res.setNeedDatailedInfo(getNeedDatailedInfo(original));
		return res;
	}

	private static Boolean getNeedDatailedInfo(final Throwable e) {
		return ((e.getClass() != NoSuchUserDataException.class)
				&& (e.getClass() != SPNotExistsException.class) && (e.getClass() != ValidateInDBException.class));
	}

	private static MessageType getMessageType(final Throwable exc) {
		if (exc instanceof ValidateInDBException) {
			return ((ValidateInDBException) exc).getUserMessage().getType();
		}
		return MessageType.ERROR;
	}

	private static DataPanelElementContext getContext(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getContext();
		}
		return null;
	}

	private static ExceptionType getType(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getType();
		}
		return ExceptionType.JAVA;
	}

	private static String getUserMessage(final Throwable exc) {
		if (exc instanceof ValidateInDBException) {
			return ((ValidateInDBException) exc).getUserMessage().getText();
		}
		if (exc.getLocalizedMessage() != null) {
			return exc.getLocalizedMessage();
		} else {
			return exc.getClass().getName();
		}
	}

	private static String getOriginalMessage(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getOriginalMessage();
		}
		return null;
	}
}
