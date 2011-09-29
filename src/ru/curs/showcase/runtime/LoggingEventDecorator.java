package ru.curs.showcase.runtime;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Marker;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.ReflectionUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.*;

/**
 * Оболочка для класса LoggingEvent.
 * 
 * @author den
 * 
 */
public class LoggingEventDecorator implements AbstractCommandContext {

	private final ILoggingEvent original;

	private CommandContext commandContext = new CommandContext();

	public ILoggingEvent getOriginal() {
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

	public LoggingEventDecorator(final ILoggingEvent aEvent, final CommandContext aCommandContext) {
		super();
		original = aEvent;
		commandContext = aCommandContext;
	}

	public String getMessage() {
		String src = original.getMessage();
		if (original.getThrowableProxy() != null) {
			src =
				src + ExchangeConstants.LINE_SEPARATOR + original.getThrowableProxy().getMessage()
						+ ExchangeConstants.LINE_SEPARATOR
						+ logExceptionStack(original.getThrowableProxy());
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

	private String logExceptionStack(final IThrowableProxy throwableProxy) {
		String result = "";
		StackTraceElementProxy[] stackElements = throwableProxy.getStackTraceElementProxyArray();
		for (StackTraceElementProxy element : stackElements) {
			result = result + ExchangeConstants.LINE_SEPARATOR + element.getSTEAsString();
		}
		if (throwableProxy.getCause() != null) {
			result =
				result + ExchangeConstants.LINE_SEPARATOR + ExchangeConstants.LINE_SEPARATOR
						+ "Источник ошибки:" + ExchangeConstants.LINE_SEPARATOR
						+ throwableProxy.getCause().getMessage()
						+ ExchangeConstants.LINE_SEPARATOR
						+ logExceptionStack(throwableProxy.getCause());
		}
		return result;
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

	public String getDirection() {
		Marker marker = original.getMarker();
		if ((marker != null) && marker.hasReferences()) {
			return ((Marker) marker.iterator().next()).getName();
		}
		return "";
	}

	public String getProcess() {
		Marker marker = original.getMarker();
		if (marker != null) {
			return marker.getName();
		}
		return "";
	}

	public String getParams() {
		Marker marker = original.getMarker();
		if ((marker != null) && marker.hasReferences()) {
			@SuppressWarnings("unchecked")
			Iterator<Marker> iterator = marker.iterator();
			iterator.next();
			if (iterator.hasNext()) {
				return iterator.next().getName();
			} else {
				return "";
			}
		}
		return "";
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
