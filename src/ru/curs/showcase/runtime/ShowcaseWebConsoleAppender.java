package ru.curs.showcase.runtime;

import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.MDC;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

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
			LoggingEventDecorator eventDecorator = new LoggingEventDecorator(event);
			retriveMDC(eventDecorator);
			AppInfoSingleton.getAppInfo().addLogEvent(eventDecorator);
		}
	}

	private void retriveMDC(final LoggingEventDecorator eventDecorator) {
		@SuppressWarnings("unchecked")
		Map<String, String> params = MDC.getCopyOfContextMap();
		if (params != null) {
			eventDecorator.setUserName(params.get(GeneralXMLHelper.USERNAME_TAG));
			eventDecorator.setUserdata(params.get(ExchangeConstants.URL_PARAM_USERDATA));
			eventDecorator.getCommandContext().setRequestId(
					params.get(GeneralXMLHelper.REQUEST_ID_TAG));
			eventDecorator.getCommandContext().setCommandName(
					params.get(GeneralXMLHelper.COMMAND_NAME_TAG));
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