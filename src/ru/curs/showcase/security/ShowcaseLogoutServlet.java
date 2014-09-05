package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseLogoutServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseLogoutServlet.class);
	private static final String LOGOUT_INFO = "Сессия %s закрыта";
	private static final String ERROR_LOGOUT_INFO =
		"Сессия %s не была закрыта на сервере аутентификафии. AuthServer недоступен.";

	private static final long serialVersionUID = -2981309424890139659L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String sesid = null;

		sesid = request.getSession().getId();

		if (!((UserAndSessionDetails) SecurityContextHolder.getContext().getAuthentication()
				.getDetails()).isAuthViaAuthServer()) {
			// if
			// (!(AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(sesid)))
			// {
			return;
		}

		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR);
		}

		if (url != null) {
			URL server = new URL(url + String.format("/logout?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			try {
				c.connect();

				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
						LOGGER.info(String.format(LOGOUT_INFO, sesid));
					}
				}

			} catch (IOException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info(String.format(ERROR_LOGOUT_INFO, sesid));
				}
			}

		}

	}
}
