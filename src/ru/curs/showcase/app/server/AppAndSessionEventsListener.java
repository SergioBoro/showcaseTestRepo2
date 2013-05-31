package ru.curs.showcase.app.server;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.logging.*;

/**
 * Перехватчик старта приложения и сессии. Служит для инициализации приложения.
 * 
 * @author den
 * 
 */
public class AppAndSessionEventsListener implements ServletContextListener, HttpSessionListener {
	private static final String SHOWCASE_LOADING = "Showcase загружается...";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	@Override
	public final void contextInitialized(final ServletContextEvent arg0) {
		LOGGER.info(SHOWCASE_LOADING);
		AppInitializer.initialize();
		ProductionModeInitializer.initialize(arg0.getServletContext());
	}

	@Override
	public final void contextDestroyed(final ServletContextEvent arg0) {
		JMXBeanRegistrator.unRegister();
		AppInfoSingleton.getAppInfo().getCacheManager().shutdown();
		ConnectionFactory.unregisterDrivers();
		SecurityEventHandler.getInstance().shutdown();
	}

	@Override
	public final void sessionCreated(final HttpSessionEvent arg0) {
		LOGGER.info("сессия Showcase создается... " + arg0.getSession().getId());
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent arg0) {
		HttpSession destrHttpSession = arg0.getSession();
		LOGGER.info("сессия Showcase удаляется..." + destrHttpSession.getId());
		AppInfoSingleton.getAppInfo().removeSessionInfo(destrHttpSession.getId());

		String userName = null; // SessionUtils.getCurrentSessionUserName();
		SecurityContext context =
			(SecurityContext) destrHttpSession
					.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				userName = auth.getName();
				Event event = new Event(Event.TypeEvent.LOGOUT);
				event.setUsername(userName);
				event.setSessionid(destrHttpSession.getId());
				SecurityEventHandler.getInstance().addEvent(event);
			}
		}
	}
}