package ru.curs.showcase.model.command;

import org.slf4j.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.NoSuchUserDataException;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Фабрика по созданию GeneralException на основе серверного Exception.
 * 
 * @author den
 * 
 */
public final class GeneralExceptionFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseException.class);

	private static final String ERROR_CAPTION = "Сообщение об ошибке";

	private GeneralExceptionFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Выводит в лог полную информацию об исключении.
	 * 
	 * @param e
	 *            - исключение.
	 */
	private static void logAll(final Throwable e) {
		LOGGER.error(ERROR_CAPTION, e);
	}

	/**
	 * Основная функция фабрики.
	 * 
	 * @return - исключение.
	 * @param original
	 *            - оригинальное исключение.
	 * @param aCurrentContext
	 *            - текущий контекст.
	 */
	public static GeneralException build(final Throwable original,
			final DataPanelElementContext aCurrentContext) {
		log(original);
		GeneralException res = new GeneralException(original, getUserMessage(original));
		res.setOriginalExceptionClass(original.getClass().getName());
		res.setOriginalMessage(getOriginalMessage(original));
		res.setType(getType(original));
		res.setContext(getContext(original, aCurrentContext));
		res.setMessageType(getMessageType(original));
		res.setNeedDatailedInfo(getNeedDatailedInfo(original));
		return res;
	}

	public static GeneralException build(final Throwable original) {
		return build(original, null);

	}

	private static void log(final Throwable original) {
		if (!(original instanceof BaseException)) {
			logAll(original);
		}
	}

	private static Boolean getNeedDatailedInfo(final Throwable e) {
		return (e.getClass() != NoSuchUserDataException.class)
				&& (e.getClass() != SPNotExistsException.class)
				&& (e.getClass() != ValidateException.class);
	}

	private static MessageType getMessageType(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getType();
		}
		return MessageType.ERROR;
	}

	private static DataPanelElementContext getContext(final Throwable e,
			final DataPanelElementContext aCurrentContext) {
		if (e instanceof BaseException) {
			DataPanelElementContext result = ((BaseException) e).getContext();
			if (result == null) {
				return aCurrentContext;
			}
			return ((BaseException) e).getContext();
		}
		return aCurrentContext;
	}

	private static ExceptionType getType(final Throwable e) {
		if (e instanceof BaseException) {
			return ((BaseException) e).getType();
		}
		return ExceptionType.JAVA;
	}

	private static String getUserMessage(final Throwable exc) {
		if (exc instanceof ValidateException) {
			return ((ValidateException) exc).getUserMessage().getText();
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
