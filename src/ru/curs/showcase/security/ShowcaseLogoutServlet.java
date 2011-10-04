package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseLogoutServlet extends HttpServlet {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseLogoutServlet.class);
	private static final String LOGOUT_INFO = "Сессия %s закрыта";
	private static final String ERROR_LOGOUT_INFO =
		"Сессия %s не была закрыта на сервере аутентификафии. AuthServer недоступен.";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2981309424890139659L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String sesid;
		try {
			sesid = request.getSession().getId();
		} catch (Exception e) {
			sesid = null;
		}

		if (!(AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(sesid))) {
			return;
		}

		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(AuthServerUtils.APP_PROP_READ_ERROR);
		}

		if (url != null) {
			URL server = new URL(url + String.format("/logout?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			try {
				c.connect();

				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					LOGGER.info(String.format(LOGOUT_INFO, sesid));
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				LOGGER.info(String.format(ERROR_LOGOUT_INFO, sesid));
			}

		}

	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("logout post");
	}
}
