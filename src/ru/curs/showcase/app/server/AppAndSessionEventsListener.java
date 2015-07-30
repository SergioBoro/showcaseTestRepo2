package ru.curs.showcase.app.server;

import java.io.File;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.*;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.logging.Event.TypeEvent;
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

	private AbstractRefreshableWebApplicationContext actx;

	@Override
	public final void contextInitialized(final ServletContextEvent arg0) {
		LOGGER.info(SHOWCASE_LOADING);

		AppInitializer.initialize();

		ProductionModeInitializer.initialize(arg0.getServletContext());

		AppInfoSingleton.getAppInfo().getGeneralAppProperties().initialize();

		AppInfoSingleton.getAppInfo().setDebugSolutionModeEnabled(
				Boolean.parseBoolean(UserDataUtils
						.getGeneralOptionalProp("debug.solution.mode.enabled")));

		WebApplicationContext ctx =
			WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
		actx = (AbstractRefreshableWebApplicationContext) ctx;
		actx.refresh();

		try {
			try {
				Properties celestaProps = UserDataUtils.getGeneralCelestaProperties();
				
				String javaLibPath = celestaProps.getProperty("javalib.path");
				for (String path : JythonIterpretatorFactory.getGeneralScriptDirFromWebInf("lib")) {
					if ("".equals(javaLibPath) || javaLibPath == null) {
						javaLibPath = path;
					} else {
						javaLibPath = javaLibPath + File.pathSeparator + path;
					}
				}
				if (javaLibPath != null) {

					javaLibPath.replace("/", File.separator);

					celestaProps.setProperty("javalib.path", javaLibPath);
				} else {
					celestaProps.setProperty("javalib.path", "");
				}
				
				String pyLibPath = celestaProps.getProperty("pylib.path");
				for (String path : JythonIterpretatorFactory
						.getGeneralScriptDirFromWebInf("libJython")) {
					if ("".equals(pyLibPath) || pyLibPath == null) {
						pyLibPath = path;
					} else {
						pyLibPath = pyLibPath + File.pathSeparator + path;
					}
				}
				celestaProps.setProperty("pylib.path", pyLibPath);
				
				if (celestaProps != null) {
					Celesta.initialize(celestaProps);
					AppInfoSingleton.getAppInfo().setIsCelestaInitialized(true);
				} else {
					if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
						LOGGER.warn("Celesta properties (in app.properties) is not set");
					}
					AppInfoSingleton.getAppInfo().setCelestaInitializationException(
							new Exception("Celesta properties (in app.properties) is not set"));
				}
			} catch (Exception ex) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка инициализации celesta", ex);
				}
				AppInfoSingleton.getAppInfo().setCelestaInitializationException(ex);
			}
		} finally {
			ProductionModeInitializer.initActiviti();
		}
	}

	@Override
	public final void contextDestroyed(final ServletContextEvent arg0) {
		JMXBeanRegistrator.unRegister();
		AppInfoSingleton.getAppInfo().getCacheManager().shutdown();
		ConnectionFactory.unregisterDrivers();
		if (actx != null) {
			actx.close();
		}
	}

	@Override
	public final void sessionCreated(final HttpSessionEvent arg0) {
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("сессия Showcase создается... " + arg0.getSession().getId());
		}
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent arg0) {
		HttpSession destrHttpSession = arg0.getSession();
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info("сессия Showcase удаляется..." + destrHttpSession.getId());
		}

		try {
			Celesta.getInstance().logout(destrHttpSession.getId(), false);
		} catch (CelestaException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка разлогинивания сессии в celesta", e);
			}
		}

		SecurityContext context =
			(SecurityContext) destrHttpSession
					.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				TypeEvent typeEvent = TypeEvent.SESSSIONTIMEOUT;
				if (destrHttpSession.getAttribute(SecurityLoggingCommand.IS_CLICK_LOGOUT) != null) {
					typeEvent = TypeEvent.LOGOUT;
				}
				SecurityLoggingCommand logCommand =
					new SecurityLoggingCommand(new CompositeContext(), null, destrHttpSession,
							typeEvent);
				logCommand.execute();
			}
		}
	}
}