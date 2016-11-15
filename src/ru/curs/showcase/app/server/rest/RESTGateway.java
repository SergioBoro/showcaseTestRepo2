package ru.curs.showcase.app.server.rest;

import javax.servlet.http.HttpServletRequest;

import org.python.core.PyObject;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * @author a.lugovtsov
 *
 */

public class RESTGateway {

	static private class ShowcaseRESTException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}
	// public void doFilter(final ServletRequest request, final ServletResponse
	// response,
	// final FilterChain filterChain) throws IOException, ServletException {
	//
	//
	//
	// HttpServletRequest httpReq = (HttpServletRequest) request;
	// HttpServletResponse httpRes = (HttpServletResponse) response;
	//
	// String sesId = httpReq.getSession().getId();
	//
	// String initialUrl = getFullURL(httpReq);
	//
	// // ===
	// Boolean isNeedRedirect = false;
	// for (String redirrectionPath :
	// RESTUserdataProp.getPathToRedirect()) {
	//
	// if (initialUrl.contains(redirrectionPath)) {
	//
	// for (String redirrectionExt :
	// RESTUserdataProp.getExtensionToRedirect()) {
	// if (initialUrl.contains("." + redirrectionExt)) {
	// isNeedRedirect = true;
	// break;
	// }
	// }
	// break;
	// }
	//
	// }
	// // ==
	//
	// if (!isNeedRedirect) {
	// filterChain.doFilter(request, response);
	// return;
	// }
	// String redirectToUrl = getRESTUrlForLink(initialUrl, sesId,
	// RESTUserdataProp.getRESTProc());
	//
	// if (redirectToUrl != null && !redirectToUrl.isEmpty()
	// && !redirectToUrl.equals(initialUrl)) {
	// httpRes.sendRedirect(redirectToUrl);
	//
	// } else {
	// filterChain.doFilter(request, response);
	// }
	//
	// } requestType, userToken, requestUrl, requestBody, urlParams

	static public String executeRESTcommand(final String requestType, final String userToken,
			final String requestUrl, final String requestData, final String urlParams,
			final String sesId, final String restProc) {
		String correctedRESTProc = restProc.trim();
		final int tri = 3;
		final int vosem = 8;
		if (restProc.endsWith(".cl")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - tri);
		}

		if (restProc.endsWith(".celesta")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - vosem);
		}

		try {
			PyObject pObj = Celesta.getInstance().runPython(sesId, correctedRESTProc, requestType,
					userToken, requestUrl, urlParams);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(String.class)) {
				return (String) obj;
			}

		} catch (CelestaException e) {
			throw new ShowcaseRESTException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta для выполнения REST запроса произошла ошибка: "
							+ e.getMessage());

		}

		return null;

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
