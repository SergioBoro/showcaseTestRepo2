package ru.curs.showcase.model;

import java.io.*;
import java.sql.SQLException;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания UserMessage для ValidateInDBException.
 * 
 * @author den
 * 
 */
public final class UserMessageFactory {

	/**
	 * Префикс для исключения решения.
	 */
	public static final String SOL_MES_PREFIX = "__user_mes_";
	/**
	 * Суффикс для исключения решения.
	 */
	public static final String SOL_MES_SUFFIX = "_src__";

	private static final String MESSAGE_TAG = "message";

	/**
	 * Сообщение, выдаваемое пользователю.
	 */
	private UserMessage userMessage;

	/**
	 * Признак того, что нужное сообщение найдено в файле.
	 */
	private boolean mesFound = false;

	/**
	 * Имя файла с описанием сообщений.
	 */
	public static final String SOL_MESSAGES_FILE = "user.messages.xml";

	public UserMessage build(final Throwable cause) {
		String mesId = parse(cause);
		loadMessage(mesId);
		if (userMessage == null) {
			throw new SettingsFileRequiredPropException(SOL_MESSAGES_FILE, mesId,
					SettingsFileType.SOLUTION_MESSAGES);
		}
		return userMessage;
	}

	public UserMessage build(final UserMessage initial) {
		return internalBuild(initial.getId(), initial.getText());
	}

	public UserMessage build(final Integer errorCode, final String errorMes) {
		return internalBuild(errorCode.toString(), errorMes);
	}

	private UserMessage internalBuild(final String errorCode, final String errorMes) {
		loadMessage(errorCode);
		if (userMessage != null) {
			if (userMessage.getText().indexOf("%s") > -1) {
				userMessage.setText(String.format(userMessage.getText(), errorMes));
			} else {
				userMessage.setText(userMessage.getText() + " " + errorMes);
			}
		} else {
			userMessage =
				new UserMessage(String.format("%s (%s)", errorMes, errorCode), MessageType.ERROR);
			userMessage.setId(errorCode);
		}
		return userMessage;
	}

	private void loadMessage(final String mesId) {
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
						userMessage = new UserMessage();
						userMessage.setId(mesId);
						userMessage.setText("");
						if (attrs.getIndex(GeneralXMLHelper.TYPE_TAG) > -1) {
							userMessage.setType(MessageType.valueOf(attrs
									.getValue(GeneralXMLHelper.TYPE_TAG)));
						}
					}
				}
			}

			@Override
			public void characters(final char[] aCh, final int aStart, final int aLength)
					throws SAXException {
				if (mesFound) {
					userMessage.setText(userMessage.getText()
							+ String.copyValueOf(aCh, aStart, aLength));
				}
			}

			@Override
			public void endElement(final String namespaceURI, final String lname,
					final String qname) {
				if (qname.equalsIgnoreCase(MESSAGE_TAG)) {
					if (mesFound) {
						mesFound = false;
						userMessage.setText(userMessage.getText().trim());
					}
				}
			}
		};

		SimpleSAX sax = new SimpleSAX(stream, saxHandler, SOL_MESSAGES_FILE);
		sax.parse();
	}

	private static String parse(final Throwable cause) {
		String mes = cause.getMessage();
		return (String) mes.subSequence(mes.indexOf(SOL_MES_PREFIX) + SOL_MES_PREFIX.length(),
				mes.indexOf(SOL_MES_SUFFIX));
	}

	/**
	 * Функция проверки на то, что полученное из БД исключение предусмотрено
	 * создателями решения.
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
