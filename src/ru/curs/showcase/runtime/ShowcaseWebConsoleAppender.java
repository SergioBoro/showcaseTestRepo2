package ru.curs.showcase.runtime;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Наш обработчик событий для работы веб-консоли.
 * 
 * @author den
 * 
 */
public class ShowcaseWebConsoleAppender extends AppenderSkeleton {

	@Override
	protected void append(final LoggingEvent event) {
		if (event.getLoggerName().startsWith("ru.curs")
				|| event.getLoggerName().startsWith("jdbc.sqlonly")) {
			CommandContext commandContext = new CommandContext();
			commandContext.fromMDC();
			LoggingEventDecorator eventDecorator =
				new LoggingEventDecorator(event, commandContext);
			AppInfoSingleton.getAppInfo().addLogEvent(eventDecorator);
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