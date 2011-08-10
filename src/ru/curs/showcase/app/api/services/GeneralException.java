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
public class GeneralException extends Exception {
	private static final String EXCEPTION_TRACE = "Основной стек ошибки:";

	private static final String EXCEPTION_CLASS = "Класс ошибки: ";

	private static final String ORIGINAL_MESSAGE = "Исходное сообщение: ";

	private static final String CONTEXT_MES = "Контекст выполнения: ";

	private static final String CAUSE_EXC_CAPTION = "Источник ошибки: ";

	private static final String CAUSE_EXC_TRACE_CAPTION = "Стек источника ошибки:";

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

	/**
	 * Признак того, что для данного исключения требуется показывать
	 * пользователю дополнительную информацию.
	 */
	private Boolean needDatailedInfo;

	public final String getOriginalMessage() {
		return originalMessage;
	}

	public final void setOriginalMessage(final String aOriginalMessage) {
		originalMessage = aOriginalMessage;
	}

	public GeneralException() {
		super();
	}

	public GeneralException(final Throwable original, final String aUserMessage) {
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

		if (original.getCause() != null) {
			result.append(CAUSE_EXC_CAPTION);
			result.append(ls);
			result.append(original.getCause().getLocalizedMessage());
			result.append(ls);
			result.append(ls);
			result.append(CAUSE_EXC_TRACE_CAPTION);
			result.append(ls);
			result.append(ls);
			result.append(original.getCause().getStackTrace()[0].toString());
			result.append(ls);
			if (original.getCause().getStackTrace().length > 1) {
				result.append(original.getCause().getStackTrace()[1].toString());
				result.append(ls);
			}
		}

		result.append(ls);

		result.append(EXCEPTION_TRACE);
		result.append(ls);
		result.append(ls);

		for (StackTraceElement el : original.getStackTrace()) {
			result.append(el.toString());
			result.append(ls);
		}

		return result.toString();
	}

	private static String getDetailedTextOfException(final String mes, final String className,
			final String trace, final ExceptionType aType, final DataPanelElementContext context) {
		String str = "";
		String ls = ExchangeConstants.LINE_SEPARATOR;
		if (mes != null) {
			str = ORIGINAL_MESSAGE + ls + mes + ls + ls;
		}

		if (context != null) {
			str = CONTEXT_MES + ls + ls + context.toString();
		}
		str = str + ls;
		if (aType != ExceptionType.USER) {
			str = str + EXCEPTION_CLASS + ls + className + ls + ls;
			str = str + trace;
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
	public static String generateDetailedInfo(final Throwable caught) {
		if (caught instanceof GeneralException) {
			GeneralException gse = (GeneralException) caught;
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
		if (caught instanceof GeneralException) {
			GeneralException gse = (GeneralException) caught;
			return gse.needDatailedInfo;
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
		if (caught instanceof GeneralException) {
			return ((GeneralException) caught).type;
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
		if (caught instanceof GeneralException) {
			return ((GeneralException) caught).messageType;
		} else {
			return MessageType.ERROR;
		}
	}

	public void setMessageType(final MessageType aMessageType) {
		messageType = aMessageType;
	}

	public Boolean getNeedDatailedInfo() {
		return needDatailedInfo;
	}

	public void setNeedDatailedInfo(final Boolean aNeedDatailedInfo) {
		needDatailedInfo = aNeedDatailedInfo;
	}
}
