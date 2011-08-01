package ru.curs.showcase.app.server;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Перехватчик старта приложения и сессии. Служит для инициализации приложения.
 * 
 * @author den
 * 
 */
public class AppAndSessionEventsListener implements ServletContextListener, HttpSessionListener {
	private static final String SHOWCASE_LOADING = "Showcase загружается...";

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	@Override
	public final void contextInitialized(final ServletContextEvent arg0) {
		LOGGER.info(SHOWCASE_LOADING);
		AppInitializer.initialize();
		ProductionModeInitializer.initialize(arg0);
	}

	@Override
	public final void contextDestroyed(final ServletContextEvent arg0) {

	}

	@Override
	public final void sessionCreated(final HttpSessionEvent arg0) {
		LOGGER.info("сессия Showcase создается... " + arg0.getSession().getId());

	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent arg0) {
		LOGGER.info("сессия Showcase удаляется..." + arg0.getSession().getId());
		AppInfoSingleton.getAppInfo().removeSessionInfo(arg0.getSession().getId());
	}
}