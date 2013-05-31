package ru.curs.showcase.security.logging;

import java.util.concurrent.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.runtime.*;

/**
 * Обработчик событий.
 * 
 * @author bogatov
 * 
 */
public class SecurityEventHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityEventHandler.class);

	private final int corePoolSize = 2;
	private final int maximumPoolSize = 20;
	private final int keepAliveTime = 15;
	private final TimeUnit unit = TimeUnit.SECONDS;

	/**
	 * Поток обработки события.
	 * 
	 */
	private class EventTask implements Runnable {
		private SecurityLoggingGateway gateway;
		private Event event;

		public EventTask(final SecurityLoggingGateway oGateway, final Event oEvent) {
			super();
			this.gateway = oGateway;
			this.event = oEvent;
		}

		@Override
		public void run() {
			try {
				AppInfoSingleton.getAppInfo().setCurUserDataId(ExchangeConstants.DEFAULT_USERDATA);
				if (this.gateway != null) {
					this.gateway.doLogging(event);
				} else {
					LOGGER.warn("SecurityLoggingGateway is null.");
				}
			} catch (Exception ex) {
				LOGGER.error("Error logging.", ex);
			}
		}

	}

	private static SecurityEventHandler instance = new SecurityEventHandler();
	private ThreadPoolExecutor threadPoolExecutor;
	private SecurityLoggingGateway gateway;

	public static SecurityEventHandler getInstance() {
		return instance;
	}

	public SecurityEventHandler() {
		super();
		String procName = UserDataUtils.getGeneralOptionalProp("security.logging.proc");
		if (procName != null && !procName.isEmpty()) {
			SecurityLoggingSelector selector = new SecurityLoggingSelector(procName);
			this.gateway = selector.getGateway();
			BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(corePoolSize * 2);
			this.threadPoolExecutor =
				new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue);
		}
	}

	public void addEvent(final Event event) {
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.execute(new EventTask(this.gateway, event));
		} else {
			LOGGER.warn("SecurityEventHandler is not init.");
		}
	}
	
	public void shutdown() {
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.shutdown();
		}
	}
}
