package ru.curs.showcase.runtime;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

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
		src = src.replace("<", "&lt;");
		src = src.replace(">", "&gt;");
		src = src.replace("\\r\\n", "&lt;br/&gt;");
		src = src.replace("\\n", "&lt;br/&gt;");
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
