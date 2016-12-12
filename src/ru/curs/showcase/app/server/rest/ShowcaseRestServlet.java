package ru.curs.showcase.app.server.rest;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.util.exception.BaseException;

/**
 * @author a.lugovtsov
 *
 */
public final class ShowcaseRestServlet extends HttpServlet {

	private static final long serialVersionUID = 1311685218914828051L;

	private class ShowcaseRESTException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		// System.out.println("aaaa: " + request.getMethod());

		String requestType = request.getMethod();
		String userToken = request.getHeader("user-token");
		String requestUrl = request.getRequestURL().toString();

		String acceptLanguage = request.getHeader("Accept-Language");
		if (acceptLanguage == null || acceptLanguage.isEmpty()) {
			acceptLanguage = "en";
		}

		String sesId = request.getSession().getId();

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
		response.getWriter().write(responcseData.getResponseData());

		response.setStatus(responcseData.getResponseCode());

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
