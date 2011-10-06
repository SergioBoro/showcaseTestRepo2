package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ServletUtils;

/**
 * Фильтр для считывание из URL параметров сессии.
 * 
 * @author den
 * 
 */
public class SessionInfoFilter implements Filter {
	/**
	 * Префикс сервлетов, используемых в механизме аутентификации.
	 */
	// private static final String AUTH_DATA_SERVLET_PREFIX = "auth";
	/**
	 * Имя основной страницы приложения.
	 */
	public static final String INDEX_PAGE = "index.jsp";

	/**
	 * Имя страницы логина приложения.
	 */
	public static final String LOGIN_PAGE = "login.jsp";

	@Override
	public void doFilter(final ServletRequest req, final ServletResponse resp,
			final FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpreq = (HttpServletRequest) req;
			if (isMainPage(httpreq)) {
				initSession(httpreq); // TODO нужно ли
			}
			if (isDynamicDataServlet(httpreq)) {
				skipServletCaching(resp);
			}
		}
		chain.doFilter(req, resp);
		resetThread();
	}

	private void resetThread() {
		AppInfoSingleton.getAppInfo().setCurUserDataId((String) null);

	}

	private void skipServletCaching(final ServletResponse resp) {
		HttpServletResponse httpresp = (HttpServletResponse) resp;
		ServletUtils.doNoCasheResponse(httpresp);
	}

	private void initSession(final HttpServletRequest httpreq) {
		HttpSession session = httpreq.getSession(true);
		AppInfoSingleton.getAppInfo().addSession(session.getId());
	}

	private boolean isMainPage(final HttpServletRequest httpreq) {
		return httpreq.getServletPath().contains("/" + INDEX_PAGE);
	}

	private boolean isDynamicDataServlet(final HttpServletRequest httpreq) {
		// String servletPath = httpreq.getServletPath();
		// return servletPath.startsWith("/" +
		// ExchangeConstants.SECURED_SERVLET_PREFIX)
		// || servletPath.startsWith("/" + AUTH_DATA_SERVLET_PREFIX)
		// || servletPath.startsWith("/" + INDEX_PAGE)
		// || servletPath.startsWith("/" + LOGIN_PAGE);
		return true;
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
