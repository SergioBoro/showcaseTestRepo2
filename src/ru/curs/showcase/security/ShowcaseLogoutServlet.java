package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

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
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2981309424890139659L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(AuthServerUtils.APP_PROP_READ_ERROR);
		}

		String sesid;
		try {
			sesid = request.getSession().getId();
		} catch (Exception e) {
			sesid = null;
		}

		if (url != null) {
			URL server = new URL(url + String.format("/logout?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				LOGGER.info(String.format(LOGOUT_INFO, sesid));
			}
		}

	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("logout post");
	}
}
