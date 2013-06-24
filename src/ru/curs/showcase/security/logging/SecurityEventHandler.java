package ru.curs.showcase.security.logging;

import java.util.concurrent.*;

import org.slf4j.*;

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
		private final Event event;

		public EventTask(final Event oEvent) {
			super();
			this.event = oEvent;
		}

		@Override
		public void run() {
			try {
				SecurityLoggingCommand command = new SecurityLoggingCommand(event);
				command.execute();
			} catch (Exception ex) {
				LOGGER.error("Error logging.", ex);
			}
		}

	}

	private static SecurityEventHandler instance = new SecurityEventHandler();
	private final ThreadPoolExecutor threadPoolExecutor;

	public static SecurityEventHandler getInstance() {
		return instance;
	}

	public SecurityEventHandler() {
		super();
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(corePoolSize * 2);
		this.threadPoolExecutor =
			new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue);
	}

	public void addEvent(final Event event) {
		if (this.threadPoolExecutor != null) {
			this.threadPoolExecutor.execute(new EventTask(event));
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
