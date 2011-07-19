package ru.curs.showcase.exception;

import java.io.*;
import java.sql.SQLException;

import javax.xml.parsers.SAXParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.services.ExceptionType;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Исключение, возникающее при проверке введенных пользователем данных в БД.
 * Исключение можно вызвать, если в тексте сообщения об ошибке из БД будут
 * определенные комбинации символов. Описание выдаваемых пользователю сообщений
 * при таких исключений хранятся в файле в папке userdata. Формат этих файлов
 * следующий: <messages> <messages id="id1" type="ERROR"> Это текст сообщения об
 * ошибке в хранимой процедуре.
 * 
 * @author den
 * 
 */
public final class ValidateInDBException extends BaseException {

	/**
	 * Префикс для исключения решения.
	 */
	public static final String SOL_MES_PREFIX = "__user_mes_";
	/**
	 * Суффикс для исключения решения.
	 */
	public static final String SOL_MES_SUFFIX = "_src__";

	/**
	 * Имя файла с описанием сообщений.
	 */
	public static final String SOL_MESSAGES_FILE = "user.messages.xml";

	/**
	 * Временная переменная для хранения идентификатора сообщения.
	 */
	private String mesId;

	/**
	 * Сообщение, выдаваемое пользователю.
	 */
	private UserMessage userMessage;

	public UserMessage getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(final UserMessage aMessage) {
		userMessage = aMessage;
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 870894006633410366L;
	static final String MESSAGE_TAG = "message";

	/**
	 * Признак того, что нужное сообщение найдено в файле.
	 */
	private boolean mesFound = false;

	public ValidateInDBException(final Throwable cause) {
		super(ExceptionType.USER);
		parse(cause);
		loadMessage();
	}

	public ValidateInDBException(final Integer errorCode, final String errorMes) {
		super(ExceptionType.USER);
		userMessage =
			new UserMessage(String.format("%s (%d)", errorMes, errorCode), MessageType.ERROR);
		userMessage.setId(errorCode.toString());
	}

	private void loadMessage() {
		InputStream stream;
		try {
			stream = AppProps.loadUserDataToStream(SOL_MESSAGES_FILE);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, SOL_MESSAGES_FILE,
					SettingsFileType.SOLUTION_MESSAGES);
		}

		DefaultHandler saxHandler = new DefaultHandler() {

			@Override
			public void startElement(final String namespaceURI, final String lname,
					final String qname, final Attributes attrs) {
				if (qname.equalsIgnoreCase(MESSAGE_TAG)) {
					if (attrs.getValue(GeneralXMLHelper.ID_TAG).equals(mesId)) {
						mesFound = true;
						setUserMessage(new UserMessage());
						getUserMessage().setId(mesId);
						getUserMessage().setText("");
						if (attrs.getIndex(GeneralXMLHelper.TYPE_TAG) > -1) {
							getUserMessage()
									.setType(
											MessageType.valueOf(attrs
													.getValue(GeneralXMLHelper.TYPE_TAG)));
						}
					}
				}
			}

			@Override
			public void characters(final char[] aCh, final int aStart, final int aLength)
					throws SAXException {
				if (mesFound) {
					getUserMessage().setText(
							getUserMessage().getText() + String.copyValueOf(aCh, aStart, aLength));
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(MESSAGE_TAG)) {
					if (mesFound) {
						mesFound = false;
						getUserMessage().setText(getUserMessage().getText().trim());
					}
				}
			}
		};

		SAXParser parser = XMLUtils.createSAXParser();
		try {
			parser.parse(stream, saxHandler);
		} catch (Throwable e) {
			XMLUtils.stdSAXErrorHandler(e, SOL_MESSAGES_FILE);
		}

		if (getUserMessage() == null) {
			throw new SettingsFileRequiredPropException(SOL_MESSAGES_FILE, mesId,
					SettingsFileType.SOLUTION_MESSAGES);
		}

	}

	private void parse(final Throwable cause) {
		String mes = cause.getMessage();
		mesId =
			(String) mes.subSequence(mes.indexOf(SOL_MES_PREFIX) + SOL_MES_PREFIX.length(),
					mes.indexOf(SOL_MES_SUFFIX));
	}

	/**
	 * Функция проверки на то, что полученное из БД исключение является
	 * "предопределенным" создателями решения.
	 * 
	 * @param exc
	 *            - исключение.
	 * @return - результат проверки.
	 */
	public static boolean isExplicitRaised(final SQLException exc) {
		return exc.getMessage().contains(SOL_MES_PREFIX)
				&& exc.getMessage().contains(SOL_MES_SUFFIX);
	}
}
