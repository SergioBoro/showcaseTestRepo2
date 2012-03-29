package ru.curs.showcase.core;

import java.io.*;
import java.sql.SQLException;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.runtime.UserDataUtils;
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

	private String messageFile = SOL_MESSAGES_FILE;

	/**
	 * Имя файла с описанием сообщений.
	 */
	public static final String SOL_MESSAGES_FILE = "user.messages.xml";

	public UserMessage build(final Throwable cause) {
		String mesId = parse(cause);
		loadMessage(mesId);
		if (userMessage == null) {
			throw new SettingsFileRequiredPropException(messageFile, mesId,
					SettingsFileType.SOLUTION_MESSAGES);
		}
		return userMessage;
	}

	public UserMessage build(final UserMessage initial) {
		return internalBuild(initial);
	}

	public UserMessage build(final Integer errorCode, final String errorMes) {
		return internalBuild(new UserMessage(errorCode.toString(), errorMes));
	}

	private UserMessage internalBuild(final UserMessage initial) {
		loadMessage(initial.getId());
		if (userMessage != null) {
			if (userMessage.getText().indexOf("%s") > -1) {
				userMessage.setText(String.format(userMessage.getText(), initial.getText()));
			} else {
				userMessage.setText(userMessage.getText() + " " + initial.getText());
			}
		} else {
			if (initial.getId() != null) {
				userMessage =
					new UserMessage(String.format("%s (%s)", initial.getText(), initial.getId()),
							initial.getType());
			} else {
				userMessage =
					new UserMessage(initial.getText(), initial.getId(), initial.getType());
			}
			userMessage.setId(initial.getId());
		}
		return userMessage;
	}

	private void loadMessage(final String mesId) {
		InputStream stream;
		try {
			stream = UserDataUtils.loadUserDataToStream(messageFile);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, messageFile, SettingsFileType.SOLUTION_MESSAGES);
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

		SimpleSAX sax = new SimpleSAX(stream, saxHandler, messageFile);
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

	public void setMessageFile(final String aMessageFile) {
		messageFile = aMessageFile;
	}
}
