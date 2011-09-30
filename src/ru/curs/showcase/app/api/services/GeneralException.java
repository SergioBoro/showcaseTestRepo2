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

	private static final String EXCEPTION_CLASS = "Класс ошибки: ";

	private static final String ORIGINAL_MESSAGE = "Исходное сообщение: ";

	private static final String CONTEXT_MES = "Контекст выполнения: ";

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

	private static String getDetailedTextOfException(final String mes, final String className,
			final ExceptionType aType, final DataPanelElementContext context) {
		String str = "";
		String ls = ExchangeConstants.LINE_SEPARATOR;
		if (mes != null) {
			str = ORIGINAL_MESSAGE + ls + ls + mes + ls + ls;
		}

		if (context != null) {
			str = str + CONTEXT_MES + ls + ls + context.toString();
		}
		str = str + ls;
		if (aType != ExceptionType.USER) {
			str = str + EXCEPTION_CLASS + ls + ls + className;
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
					gse.type, gse.context);
		} else {
			return getDetailedTextOfException(caught.getMessage(), caught.getClass().getName(),
					ExceptionType.JAVA, null);
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
