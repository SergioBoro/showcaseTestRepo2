package ru.curs.showcase.app.api.services;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;

/**
 * Класс общего серверного исключения. Данное исключение передается на GWT
 * клиент вместо любого конкретного исключения. Сообщение данного исключения
 * копирует
 * 
 * @author den
 * 
 */
public class GeneralServerException extends Exception {
	static final String EXCEPTION_TRACE = "Стек ошибки:";

	static final String EXCEPTION_CLASS = "Класс ошибки: ";

	static final String ORIGINAL_MESSAGE = "Исходное сообщение: ";

	static final String CONTEXT_MES = "Контекст выполнения: ";

	static final String CAUSE_EXC_CAPTION = "Источник ошибки";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5928650256788448347L;

	/**
	 * Оригинальное сообщение об ошибке. Задается в случае, если ошибка Showcase
	 * базируется на другой ошибке.
	 */
	private String originalMessage;

	/**
	 * Тип сообщения, выводимого пользователю.
	 */
	private MessageType messageType;

	/**
	 * Тип исключения.
	 */
	private ExceptionType type;

	/**
	 * Класс оригинального исключения. Необходимо хранить здесь, т.к. при
	 * сериализации gwt cause не сохраняется (нет метода setCause()).
	 */
	private String originalExceptionClass;

	/**
	 * Стек оригинального исключения. Необходимо хранить здесь, т.к. при
	 * сериализации gwt cause не сохраняется (нет метода setCause()).
	 */
	private String originalTrace;

	/**
	 * Информация о контексте и элементе в момент возникновения ошибки.
	 * Сохраняется только в случае, когда данные могут помочь понять причину
	 * ошибки.
	 */
	private DataPanelElementContext context;

	public final String getOriginalMessage() {
		return originalMessage;
	}

	public final void setOriginalMessage(final String aOriginalMessage) {
		originalMessage = aOriginalMessage;
	}

	public GeneralServerException() {
		super();
	}

	public GeneralServerException(final Throwable original, final String aUserMessage) {
		super(aUserMessage, original);
	}

	/**
	 * 
	 * Возвращает текст стэка исключения.
	 * 
	 * @param original
	 *            - Throwable
	 * @return String
	 */
	public static String getStackText(final Throwable original) {
		StringBuilder result = new StringBuilder();
		String ls = ExchangeConstants.LINE_SEPARATOR;
		// System.getProperty("line.separator"); - не
		// работает в gwt

		for (StackTraceElement el : original.getStackTrace()) {
			result.append(el.toString() + ls);
		}
		if (original.getCause() != null) {
			result.append(CAUSE_EXC_CAPTION + ls);
			result.append(original.getCause().getStackTrace()[0].toString() + ls);
			if (original.getCause().getStackTrace().length > 1) {
				result.append(original.getCause().getStackTrace()[1].toString() + ls);
			}
		}
		return result.toString();
	}

	private static String getDetailedTextOfException(final String mes, final String className,
			final String trace, final ExceptionType aType, final DataPanelElementContext context) {
		String str = null;
		if (mes != null) {
			str =
				ORIGINAL_MESSAGE + mes + ExchangeConstants.LINE_SEPARATOR
						+ ExchangeConstants.LINE_SEPARATOR;
		}
		if (context != null) {
			str = CONTEXT_MES + ExchangeConstants.LINE_SEPARATOR + context.toString();
		}
		if (aType != ExceptionType.USER) {
			str =
				str + EXCEPTION_CLASS + className + ExchangeConstants.LINE_SEPARATOR
						+ ExchangeConstants.LINE_SEPARATOR;
			str = str + EXCEPTION_TRACE + ExchangeConstants.LINE_SEPARATOR + trace;
		}
		return str;
	}

	/**
	 * Проверка caught на то что он является экземпляром класса
	 * GeneralServerException и получение подробного текста сообщения об ошибке.
	 * 
	 * @param caught
	 *            - исключение.
	 * @return текст сообщения
	 */
	public static String checkExeptionTypeAndCreateDetailedTextOfException(final Throwable caught) {
		if (caught instanceof GeneralServerException) {
			GeneralServerException gse = (GeneralServerException) caught;
			return getDetailedTextOfException(gse.originalMessage, gse.originalExceptionClass,
					gse.originalTrace, gse.type, gse.context);
		} else {
			return getDetailedTextOfException(caught.getMessage(), caught.getClass().getName(),
					getStackText(caught), ExceptionType.JAVA, null);
		}
	}

	/**
	 * Функция говорит о том, нужно ли дать возможность просмотреть детальную
	 * информацию об ошибке на клиентской части.
	 * 
	 * @return - результат проверки.
	 * @param caught
	 *            - исключение.
	 */
	public static boolean needDetailedInfo(final Throwable caught) {
		if (caught instanceof GeneralServerException) {
			GeneralServerException gse = (GeneralServerException) caught;
			return (gse.type != ExceptionType.USER) || (gse.getOriginalMessage() != null);
		} else {
			return true;
		}
	}

	public ExceptionType getType() {
		return type;
	}

	/**
	 * Функция получения типа исключения, работающая с любыми исключениями.
	 * 
	 * @param caught
	 *            - проверяемое исключение.
	 * @return = тип.
	 */
	public static ExceptionType getType(final Throwable caught) {
		if (caught instanceof GeneralServerException) {
			return ((GeneralServerException) caught).type;
		} else {
			return ExceptionType.JAVA;
		}
	}

	public void setType(final ExceptionType aType) {
		type = aType;
	}

	public String getOriginalExceptionClass() {
		return originalExceptionClass;
	}

	public void setOriginalExceptionClass(final String aOriginalExceptionClass) {
		originalExceptionClass = aOriginalExceptionClass;
	}

	public String getOriginalTrace() {
		return originalTrace;
	}

	public void setOriginalTrace(final String aOriginalTrace) {
		originalTrace = aOriginalTrace;
	}

	public DataPanelElementContext getContext() {
		return context;
	}

	public void setContext(final DataPanelElementContext aContext) {
		context = aContext;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Функция определения типа исключения, работающая со всеми исключениями.
	 * 
	 * @param caught
	 *            - проверяемое исключение.
	 * @return - тип.
	 */
	public static MessageType getMessageType(final Throwable caught) {
		if (caught instanceof GeneralServerException) {
			return ((GeneralServerException) caught).messageType;
		} else {
			return MessageType.ERROR;
		}
	}

	public void setMessageType(final MessageType aMessageType) {
		messageType = aMessageType;
	}
}
