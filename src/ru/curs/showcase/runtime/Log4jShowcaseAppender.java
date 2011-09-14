package ru.curs.showcase.runtime;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Наш обработчик событий для Log4J.
 * 
 * @author den
 * 
 */
public class Log4jShowcaseAppender extends AppenderSkeleton {

	@Override
	protected void append(final LoggingEvent event) {
		if (event.getLoggerName().startsWith("ru.curs")
				|| event.getLoggerName().startsWith("jdbc.sqlonly")) {
			AppInfoSingleton.getAppInfo().addLogEvent(new LoggingEventDecorator(event));
		}
	}

	/**
	 * ресурсы не выделялись - закрывать ничего не надо.
	 */
	@Override
	public void close() {
	}

	/**
	 * Layout не используется.
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}
}