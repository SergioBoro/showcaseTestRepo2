package ru.curs.showcase.runtime;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.TextUtils;

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

}
