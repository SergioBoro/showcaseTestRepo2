package ru.curs.showcase.app.server;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;

import javax.management.*;
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
import ru.curs.showcase.app.server.redirection.RedirectionUserdataProp;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.*;
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

	/**
	 * Количество активных сессий.
	 */
	private static Object activeSessions;

	/**
	 * Количество аутентифицированных сессий.
	 */
	private static Integer authenticatedSessions = 0;

	private ObjectName objectName;

	public static synchronized Object getActiveSessions() {
		return activeSessions;
	}

	public static synchronized Integer getAuthenticatedSessions() {
		return authenticatedSessions;
	}

	public static synchronized void increment() {
		++authenticatedSessions;
	}

	public static synchronized void decrement() {
		--authenticatedSessions;
		if (authenticatedSessions < 0)
			authenticatedSessions = 0;
	}

	@Override
	public final void contextInitialized(final ServletContextEvent arg0) {
		LOGGER.info(SHOWCASE_LOADING);

		AppInitializer.initialize();

		try {
			ProductionModeInitializer.initialize(arg0.getServletContext());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(e.getMessage());
		}

		if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {
			try {
				AppInfoSingleton.getAppInfo().getGeneralAppProperties().initialize();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(e.getMessage());
			}

			if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {

				File platformPoFile =
					new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + File.separator
							+ "common.sys" + File.separator + "resources" + File.separator
							+ "platform.po");

				if (!platformPoFile.exists()) {
					LOGGER.error("ОШИБКА: Не удалось найти дефолтный файл platform.po "
							+ "локализации клиенсткой части Showcase");
					AppInfoSingleton.getAppInfo().setShowcaseAppOnStartMessage(
							"ОШИБКА: Не удалось найти дефолтный файл platform.po "
									+ "локализации клиенсткой части Showcase");
				}

				if (AppInfoSingleton.getAppInfo().getShowcaseAppOnStartMessage().isEmpty()) {
					// Установка анонимного входа
					Properties props = UserDataUtils.getGeneralProperties();
					boolean pr =
						Boolean.parseBoolean(props.getProperty(
								"showcase.authentication.anonymous", "false").trim());

					CustomAccessProvider cap =
						ApplicationContextProvider.getApplicationContext().getBean(
								"customAccessProvider", CustomAccessProvider.class);
					if (pr) {
						cap.setAccess("permitAll");
					}

					RedirectionUserdataProp.readAndSetRedirectproperties();

					MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
					try {
						objectName =
							new ObjectName("Catalina:type=Manager,context="
									+ arg0.getServletContext().getContextPath()
									+ ",host=localhost");
					} catch (MalformedObjectNameException e1) {
						e1.printStackTrace();
					}

					Timer updateTimer = new Timer(true);
					updateTimer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							try {
								activeSessions =
									mBeanServer.getAttribute(objectName, "activeSessions");
							} catch (AttributeNotFoundException | InstanceNotFoundException
									| MBeanException | ReflectionException e) {
								e.printStackTrace();
							}
						}
					}, 0, 10 * 1000);

					WebApplicationContext ctx =
						WebApplicationContextUtils.getWebApplicationContext(arg0
								.getServletContext());
					actx = (AbstractRefreshableWebApplicationContext) ctx;
					actx.refresh();

					try {
						try {
							Properties celestaProps = UserDataUtils.getGeneralCelestaProperties();

							String javaLibPath = celestaProps.getProperty("javalib.path");
							for (String path : JythonIterpretatorFactory
									.getGeneralScriptDirFromWebInf("lib")) {
								if ("".equals(javaLibPath) || javaLibPath == null) {
									javaLibPath = path;
								} else {
									javaLibPath = javaLibPath + File.pathSeparator + path;
								}
							}
							if (javaLibPath != null) {

								javaLibPath = javaLibPath.replace("/", File.separator);

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
							pyLibPath = pyLibPath.replace("/", File.separator);
							celestaProps.setProperty("pylib.path", pyLibPath);

							if (celestaProps != null) {
								Celesta.initialize(celestaProps);
								AppInfoSingleton.getAppInfo().setIsCelestaInitialized(true);
							} else {
								if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
									LOGGER.warn("Celesta properties (in app.properties) is not set");
								}
								AppInfoSingleton
										.getAppInfo()
										.setCelestaInitializationException(
												new Exception(
														"Celesta properties (in app.properties) is not set"));
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
			}
		}
	}

	@Override
	public final void contextDestroyed(final ServletContextEvent arg0) {
		JMXBeanRegistrator.unRegister();
		// AppInfoSingleton.getAppInfo().getCacheManager().shutdown();
		AppInfoSingleton.getAppInfo().getCacheManager().close();
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

		SecurityContext context =
			(SecurityContext) destrHttpSession
					.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (context != null) {
			Authentication auth = context.getAuthentication();
			if (auth != null) {
				decrement();
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

		try {
			Celesta.getInstance().logout(destrHttpSession.getId(), false);
		} catch (CelestaException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка разлогинивания сессии в celesta", e);
			}
		}
	}
}