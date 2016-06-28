package ru.curs.showcase.app.server.redirection;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.python.core.PyObject;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

public class RedirectionFilter implements Filter {

	private class MyException extends BaseException {
		private static final long serialVersionUID = 6725288887082284411L;

		MyException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {

		if (RedirectionUserdataProp.getPathToRedirect().isEmpty()
				|| RedirectionUserdataProp.getExtensionToRedirect().isEmpty()
				|| RedirectionUserdataProp.getRedirectionProc().isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;

		String sesId = httpReq.getSession().getId();

		String initialUrl = getFullURL(httpReq);

		// ===
		Boolean isNeedRedirect = false;
		for (String redirrectionPath : RedirectionUserdataProp.getPathToRedirect()) {

			if (initialUrl.contains(redirrectionPath)) {

				for (String redirrectionExt : RedirectionUserdataProp.getExtensionToRedirect()) {
					if (initialUrl.contains("." + redirrectionExt)) {
						isNeedRedirect = true;
						break;
					}
				}
				break;
			}

		}
		// ==

		if (!isNeedRedirect) {
			filterChain.doFilter(request, response);
			return;
		}
		String redirectToUrl =
			getRedirectionUrlForLink(initialUrl, sesId,
					RedirectionUserdataProp.getRedirectionProc());

		if (redirectToUrl != null && !redirectToUrl.isEmpty() && !redirectToUrl.equals(initialUrl)) {
			httpRes.sendRedirect(redirectToUrl);

		} else {
			filterChain.doFilter(request, response);
		}

	}

	private String getRedirectionUrlForLink(final String initialUrl, final String sesId,
			final String redirectionProc) {
		String correctedRedirectionProc = redirectionProc.trim();
		final int tri = 3;
		final int vosem = 8;
		if (redirectionProc.endsWith(".cl")) {
			correctedRedirectionProc =
				redirectionProc.substring(0, redirectionProc.length() - tri);
		}

		if (redirectionProc.endsWith(".celesta")) {
			correctedRedirectionProc =
				redirectionProc.substring(0, redirectionProc.length() - vosem);
		}

		try {
			PyObject pObj =
				Celesta.getInstance().runPython(sesId, correctedRedirectionProc, initialUrl);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(String.class)) {
				return (String) obj;
			}

		} catch (CelestaException e) {
			throw new MyException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta произошла ошибка: " + e.getMessage());

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

	@Override
	public void destroy() {
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}
}
