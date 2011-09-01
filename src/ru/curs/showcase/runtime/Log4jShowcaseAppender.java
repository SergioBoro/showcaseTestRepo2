package ru.curs.showcase.runtime;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Наш обработчик события для Log4j.
 * 
 * @author den
 * 
 */
public class Log4jShowcaseAppender extends AppenderSkeleton {

	@Override
	protected void append(final LoggingEvent event) {
		if (event.getLoggerName().startsWith("ru.curs")) {
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