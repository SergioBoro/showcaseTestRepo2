package ru.curs.showcase.runtime;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.*;

/**
 * Оболочка для класса LoggingEvent.
 * 
 * @author den
 * 
 */
public class LoggingEventDecorator {
	public LoggingEvent getOriginal() {
		return original;
	}

	private final LoggingEvent original;

	private String userName;

	private String userdata;

	private final CommandContext commandContext = new CommandContext();

	public String getUserdata() {
		return userdata;
	}

	public void setUserdata(final String aUserData) {
		userdata = aUserData;
	}

	public void setUserName(final String aUserName) {
		userName = aUserName;
	}

	public LoggingEventDecorator(final LoggingEvent event) {
		super();
		original = event;
	}

	public String getMessage() {
		String src = (String) original.getMessage();
		if (original.getThrowableInformation() != null) {
			src =
				src
						+ ExchangeConstants.LINE_SEPARATOR
						+ TextUtils.arrayToString(original.getThrowableStrRep(),
								ExchangeConstants.LINE_SEPARATOR);
		}
		src = src.replace("<", "&lt;");
		src = src.replace(">", "&gt;");

		src = src.replace("\\r\\n", ExchangeConstants.LINE_SEPARATOR);
		src = src.replace("\\n", "\n");
		src = src.replace("\\\"", "&quot;");
		src = src.replace("\\t", "\t");
		src = src.replace("&amp;", "&");
		src = src.replace("&quot;", "\"");
		src = src.replace("&apos;", "'");
		return src;
	}

	public Level getLevel() {
		return original.getLevel();
	}

	public String getTime() {
		Date time = new Date(original.getTimeStamp());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		return sdf.format(time);
	}

	public String getUserName() {
		return userName;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}

	public String getRequestId() {
		return commandContext.getRequestId();
	}

	public String getCommandName() {
		return commandContext.getCommandName();
	}

	public boolean isSatisfied(final String fieldName, final String fieldValue) {
		String realValue = null;
		try {
			realValue = (String) ReflectionUtils.getPropValueForFieldName(this, fieldName);
		} catch (Exception e) {
			return true;
		}
		if ((realValue == null) && (fieldValue == null)) {
			return true;
		}
		return (realValue != null) && (fieldValue != null) ? realValue
				.equalsIgnoreCase(fieldValue) : false;
	}

}
