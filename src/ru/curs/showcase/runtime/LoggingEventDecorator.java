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
public class LoggingEventDecorator implements AbstractCommandContext {

	private final LoggingEvent original;

	private CommandContext commandContext = new CommandContext();

	public LoggingEvent getOriginal() {
		return original;
	}

	@Override
	public String getUserdata() {
		return commandContext.getUserdata();
	}

	public LoggingEventDecorator(final LoggingEvent event) {
		super();
		original = event;
	}

	public LoggingEventDecorator(final LoggingEvent aEvent, final CommandContext aCommandContext) {
		super();
		original = aEvent;
		commandContext = aCommandContext;
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

	@Override
	public String getUserName() {
		return commandContext.getUserName();
	}

	public AbstractCommandContext getCommandContext() {
		return commandContext;
	}

	@Override
	public String getRequestId() {
		return commandContext.getRequestId();
	}

	@Override
	public String getCommandName() {
		return commandContext.getCommandName();
	}

	public boolean isSatisfied(final String fieldName, final String fieldValue) {
		Object tempValue = null;
		try {
			tempValue = ReflectionUtils.getPropValueByFieldName(this, fieldName);
		} catch (Exception e) {
			return true;
		}
		String realValue = tempValue != null ? tempValue.toString() : null;
		if ((realValue == null) && (fieldValue == null)) {
			return true;
		}
		return (realValue != null) && (fieldValue != null) ? realValue
				.equalsIgnoreCase(fieldValue) : false;
	}

	public void setUserdata(final String aUserdata) {
		commandContext.setUserdata(aUserdata);

	}

	public void setUserName(final String aUserName) {
		commandContext.setUserName(aUserName);
	}

	public void setRequestId(final String aRequestId) {
		commandContext.setRequestId(aRequestId);
	}

	public void setCommandName(final String aCommandName) {
		commandContext.setCommandName(aCommandName);
	}
}
