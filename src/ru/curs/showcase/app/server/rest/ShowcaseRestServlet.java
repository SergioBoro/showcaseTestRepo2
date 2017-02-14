package ru.curs.showcase.app.server.rest;

import java.io.*;
import java.net.*;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.*;
import org.springframework.security.authentication.AuthenticationServiceException;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.*;
import ru.curs.showcase.util.exception.*;

/**
 * @author a.lugovtsov
 *
 */
public final class ShowcaseRestServlet extends HttpServlet {

	private static final long serialVersionUID = 1311685218914828051L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseRestServlet.class);

	private class ShowcaseRESTException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		// rest.authentication.type
		String sesId = request.getSession().getId();
		String requestUrl = request.getRequestURL().toString();

		if ((requestUrl.endsWith("restlogin")) || requestUrl.endsWith("restlogin/")) {

			String userSid = null;

			String usr = request.getParameter("user");

			String pwd = request.getParameter("password");

			// взаимоействие с мелофоном

			URL server;
			String url = "";
			try {
				url = SecurityParamsFactory.getLocalAuthServerUrl();
			} catch (SettingsFileOpenException e1) {
				throw new AuthenticationServiceException(SecurityParamsFactory.APP_PROP_READ_ERROR,
						e1);
			}

			server = new URL(url + String.format("/checkcredentials?login=%s&pwd=%s",
					AuthServerAuthenticationProvider.encodeParam(usr),
					AuthServerAuthenticationProvider.encodeParam(pwd)));

			HttpURLConnection c = null;

			try {
				c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					UserInfo ud = null;
					try {
						List<UserInfo> l = UserInfoUtils.parseStream(c.getInputStream());
						ud = l.get(0);
						ud.setResponseCode(c.getResponseCode());
					} catch (TransformerException e) {
						throw new ServletException(
								AuthServerUtils.AUTH_SERVER_DATA_ERROR + e.getMessage(), e);
					}
					userSid = ud.getSid();
				} else {
					userSid = null;
				}

			} finally {
				if (c != null) {
					c.disconnect();
				}
			}

			try {
				if (userSid != null) {
					Celesta.getInstance().login(sesId, userSid);
				}
			} catch (CelestaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// String restProc =
		// UserDataUtils.getGeneralOptionalProp("rest.authentication.type");
		// localhost:8082/mellophone/checkcredentials?login=Иванов1&pwd=пасс1

		// System.out.println("aaaa: " + request.getMethod());

		if ((requestUrl.endsWith("restlogout")) || requestUrl.endsWith("restlogout/")) {

			try {
				Celesta.getInstance().logout(sesId, false);
			} catch (CelestaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		String requestType = request.getMethod();
		String userToken = request.getHeader("user-token");
		// String requestUrl = request.getRequestURL().toString();

		String acceptLanguage = request.getHeader("Accept-Language");
		if (acceptLanguage == null || acceptLanguage.isEmpty()) {
			acceptLanguage = "en";
		}

		String requestURLParams = request.getQueryString();

		// request.getReader().toString()

		String requestData = "";
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		requestData = buffer.toString();

		// System.out.println("aaaa data : " + requestData);

		String restProc = UserDataUtils.getGeneralOptionalProp("rest.entry.proc");

		if ((restProc == null) || restProc.isEmpty()) {
			// System.out.println("restProc: null");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		// request.getReader()

		// JSONObject ff = new JSONObject();

		// try {
		// ff.append("sd", "fg");
		// JSONArray a = new JSONArray();
		// a.put("1");
		// a.put("2");
		// ff.append("sd", "ff");
		// ff.putOnce("ttt", "werty");
		// ff.append("sd1", a);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// System.out.println(ff.toString());

		JythonRestResult responcseData = RESTGateway.executeRESTcommand(requestType, userToken,
				acceptLanguage, requestUrl, requestData, requestURLParams, sesId, restProc);

		response.setCharacterEncoding("UTF-8");
		response.getWriter()
				.write(StringEscapeUtils.unescapeJava(responcseData.getResponseData()));

		response.setStatus(responcseData.getResponseCode());
		response.setHeader("Content-Type", responcseData.getContentType());

		LOGGER.info("Using Rest WebService. \nCalled procedure: " + restProc + "\nRequest Type: "
				+ requestType + "\nRequest URL: " + requestUrl + "\nUser Token: " + userToken
				+ "\nAccept Language: " + acceptLanguage + "\nRequest Data: " + requestData
				+ "\nRequest URL Params: " + requestURLParams + "\nResponse Code: "
				+ responcseData.getResponseCode() + "\nResponse Data: "
				+ StringEscapeUtils.unescapeJava(responcseData.getResponseData()));

		// response.setStatus(201);
		response.getWriter().close();
	}

	private String getFullURL(final HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

}
