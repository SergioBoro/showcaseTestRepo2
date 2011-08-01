package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;

import ru.curs.showcase.util.*;

/**
 * Servlet implementation class ShowcaseIsAuthenticatedServlet.
 */
public class ShowcaseIsAuthenticatedServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 9152046062107176349L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String url = null;
		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e) {
			throw new ServletException(AuthServerUtils.APP_PROP_READ_ERROR, e);
		}

		String sesid = request.getParameter("sesid");

		if (url != null) {
			UserData ud = connectToAuthServer(url, sesid);
			if (ud != null) {
				prepareGoodResponce(response, ud);
			}
		}
	}

	private UserData connectToAuthServer(final String url, final String sesid) throws IOException,
			ServletException {
		URL server = new URL(url + String.format("/isauthenticated?sesid=%s", sesid));
		HttpURLConnection c = (HttpURLConnection) server.openConnection();
		c.setRequestMethod("GET");
		c.setDoInput(true);
		c.connect();
		UserData ud = null;
		if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
			try {
				List<UserData> l = UserInfo.parseStream(c.getInputStream());
				ud = l.get(0);
				ud.setResponseCode(c.getResponseCode());
			} catch (TransformerException e) {
				throw new ServletException(
						AuthServerUtils.AUTH_SERVER_DATA_ERROR + e.getMessage(), e);
			}
		}
		return ud;
	}

	private void prepareGoodResponce(final HttpServletResponse response, final UserData ud)
			throws IOException {
		response.reset();
		response.setStatus(ud.getResponseCode());
		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);
		response.getWriter().append(
				String.format("{login:'%s', pwd:'%s'}", ud.getCaption(),
						"9152046062107176349L_default_value"));

		response.getWriter().close();
	}

}
