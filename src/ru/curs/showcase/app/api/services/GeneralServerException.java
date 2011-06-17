package ru.curs.showcase.app.api.services;

import ru.curs.showcase.app.api.UserMessage;

/**
 * Класс общего серверного исключения. Данное исключение передается на GWT
 * клиент вместо любого конкретного исключения. Сообщение данного исключения
 * копирует
 * 
 * @author den
 * 
 */
public class GeneralServerException extends Exception {
	/**
	 * Разделитель строк для сообщений, показываемых пользователю (используется
	 * разделитель Windows).
	 */
	private static final String LINE_SEPARATOR = "\r\n";

	static final String EXCEPTION_TRACE = "Стек ошибки:" + LINE_SEPARATOR;

	static final String EXCEPTION_CLASS = "Класс ошибки: ";

	static final String ORIGINAL_MESSAGE = "Исходное сообщение: ";

	/**
	 * Заголовок в полном стеке исключения перед описанием исключения-источника.
	 */
	private static final String CAUSE_EXC_CAPTION = "Источник ошибки";

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
	 * Информация для вывода пользователю (сообщение, его тип).
	 */
	private UserMessage userMessage;

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

	public final String getOriginalMessage() {
		return originalMessage;
	}

	public final void setOriginalMessage(final String aOriginalMessage) {
		originalMessage = aOriginalMessage;
	}

	public GeneralServerException() {
		super();
	}

	public GeneralServerException(final Throwable original, final UserMessage aUserMessage) {
		super(aUserMessage.getText(), original);
		userMessage = aUserMessage;
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
		String ls = LINE_SEPARATOR;
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
			final String trace, final ExceptionType aType) {
		String str = null;
		if (mes != null) {
			str = ORIGINAL_MESSAGE + mes + LINE_SEPARATOR + LINE_SEPARATOR;
		}
		if (aType != ExceptionType.USER) {
			str = str + EXCEPTION_CLASS + className + LINE_SEPARATOR + LINE_SEPARATOR;
			str = str + EXCEPTION_TRACE + trace;
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
					gse.originalTrace, gse.type);
		} else {
			return getDetailedTextOfException(caught.getMessage(), caught.getClass().getName(),
					getStackText(caught), ExceptionType.JAVA);
		}
	}

	/**
	 * Функция говорит о том, нужно ли дать возможность просмотреть детальную
	 * информацию об ошибке на клиентской части.
	 * 
	 * @return - результат проверки.
	 */
	public boolean needDetailedInfo() {
		return (type != ExceptionType.USER) || (getOriginalMessage() != null);
	}

	public UserMessage getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(final UserMessage aUserMessage) {
		userMessage = aUserMessage;
	}

	public ExceptionType getType() {
		return type;
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
}
