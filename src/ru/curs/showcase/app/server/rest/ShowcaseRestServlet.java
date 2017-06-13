package ru.curs.showcase.app.server.rest;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.slf4j.*;
import org.springframework.security.authentication.AuthenticationServiceException;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.UserInfo;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.*;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * @author a.lugovtsov
 *
 */
public final class ShowcaseRestServlet extends HttpServlet {

	private static final long serialVersionUID = 1311685218914828051L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowcaseRestServlet.class);

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		// rest.authentication.type
		String sesId = request.getSession().getId();
		String requestUrl = request.getRequestURL().toString();

		if ((requestUrl.endsWith("restlogin")) || requestUrl.endsWith("restlogin/")) {
			addAccessControlAllowOriginPropertyToResponceHeader(response);

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

			if (userSid != null) {
				try {

					Celesta.getInstance().login(sesId, userSid);
				} catch (CelestaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				response.setCharacterEncoding("UTF-8");
				response.getWriter()
						.write("ОШИБКА выполнения REST запроса restlogin: Логин пользователя ''"
								+ usr + "'' неуспешен. Неверная пара логин-пароль.");

				response.setStatus(403);
				response.getWriter().close();
				// response.setHeader("Content-Type",
				// responcseData.getContentType());
			}

			// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// String restProc =
		// UserDataUtils.getGeneralOptionalProp("rest.authentication.type");
		// localhost:8082/mellophone/checkcredentials?login=Иванов1&pwd=пасс1

		// System.out.println("aaaa: " + request.getMethod());

		if ((requestUrl.endsWith("restlogout")) || requestUrl.endsWith("restlogout/")) {
			addAccessControlAllowOriginPropertyToResponceHeader(response);
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

		// final String requestType,
		// final String requestUrl, final String requestData, final String
		// requestHeaders,
		// final String urlParams, final String sesId, final String restProc

		JythonRestResult responcseData = null;
		if (restProc.endsWith(".cl") || restProc.endsWith(".celesta"))

			try {
				responcseData = RESTGateway.executeRESTcommand(requestType,
						truncateRequestUrl(requestUrl), requestData, getHeadersJson(request),
						getUrlParamsJson(request), sesId, restProc);
			} catch (RESTGateway.ShowcaseRESTUnauthorizedException e) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;

			}
		if (restProc.endsWith(".py"))
			responcseData = RESTGateway.executeRESTcommandFromJythonProc(requestType,
					truncateRequestUrl(requestUrl), requestData, getHeadersJson(request),
					getUrlParamsJson(request), restProc);

		response.setCharacterEncoding("UTF-8");
		response.getWriter()
				.write(StringEscapeUtils.unescapeJava(responcseData.getResponseData()));

		response.setStatus(responcseData.getResponseCode());
		response.setHeader("Content-Type", responcseData.getContentType());
		addAccessControlAllowOriginPropertyToResponceHeader(response);
		for (Iterator<Map.Entry<String, String>> iter =
			responcseData.getResponseHttpParametersMap().entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, String> entry = iter.next();
			if ("Access-Control-Allow-Origin".equals(entry.getKey())) {
				continue;
			}

			response.setHeader(entry.getKey(), entry.getValue());

		}

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

	private String getUrlParamsJson(final HttpServletRequest request) {
		SortedMap<String, List<String>> urlParamsMap = null;
		try {
			urlParamsMap = ServletUtils.prepareURLParamsMap(request);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (urlParamsMap == null)
			return "{}";

		org.json.JSONObject jsonObj = new JSONObject(urlParamsMap);
		return jsonObj.toString();
	}

	private String getHeadersJson(final HttpServletRequest request) {
		Map<String, String> headersMap = new HashMap<>();
		@SuppressWarnings("unchecked")
		Enumeration<String> iterator = request.getHeaderNames();
		while (iterator.hasMoreElements()) {
			String headerName = iterator.nextElement();
			String headerValue = request.getHeader(headerName);
			headersMap.put(headerName, headerValue);
		}
		org.json.JSONObject jsonObj = new JSONObject(headersMap);
		return jsonObj.toString();
	}

	private String truncateRequestUrl(final String url) {
		// String[] parts = url.split("api");
		// return parts[1];
		return url.substring(url.indexOf("api") + 3);
	}

	private void addAccessControlAllowOriginPropertyToResponceHeader(
			final HttpServletResponse aresponse) {
		String rach = UserDataUtils.getGeneralOptionalProp("rest.allow.crossdomain.hosts");
		if (rach != null && "true".equalsIgnoreCase(rach.trim()))
			aresponse.setHeader("Access-Control-Allow-Origin", "*");
	}
}
