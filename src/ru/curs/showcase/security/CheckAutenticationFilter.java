package ru.curs.showcase.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.*;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Фильтр, контролирующий доступ к данным системы.
 * 
 */
public class CheckAutenticationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckAutenticationFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		if (httpReq.getSession(false) == null) {
			httpReq.getSession();
			response.reset();
			response.setContentType("text/html");
			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
			response.getWriter().close();

		} else {

			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.debug(httpReq.getSession().getId());
			}

			String url = null;
			try {
				url = SecurityParamsFactory.getLocalAuthServerUrl();
			} catch (SettingsFileOpenException e) {
				throw new ServletException(SecurityParamsFactory.APP_PROP_READ_ERROR, e);
			}
			AuthServerUtils.init(url);

			if (AppInfoSingleton.getAppInfo().getAuthViaAuthServerForSession(
					httpReq.getSession().getId())) {
				UserInfo ud =
					AuthServerUtils.getTheAuthServerAlias().isAuthenticated(
							httpReq.getSession().getId());
				// if (SecurityContextHolder.getContext().getAuthentication() !=
				// null) {
				// LOGGER.debug("RequestContextHolder = "
				// + ((WebAuthenticationDetails)
				// SecurityContextHolder.getContext()
				// .getAuthentication().getDetails()).getSessionId());
				// }

				if (ud == null) {
					response.reset();
					response.setContentType("text/html");
					response.setCharacterEncoding(TextUtils.DEF_ENCODING);
					response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
					response.getWriter().close();
				} else {
					filterChain.doFilter(request, response);
				}
			} else {

				String username = (String) (httpReq.getSession(false).getAttribute("username"));
				// String password = (String)
				// (httpReq.getSession(false).getAttribute("password"));

				if ("master".equals(username)
				// && "master".equals(password)
				) {
					filterChain.doFilter(request, response);
				} else {
					try {
						if (SecurityContextHolder.getContext().getAuthentication() != null) {
							filterChain.doFilter(request, response);
						} else {

							response.reset();
							response.setContentType("text/html");
							response.setCharacterEncoding(TextUtils.DEF_ENCODING);
							response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
							response.getWriter().close();
						}
					} catch (Exception e) {
						response.reset();
						response.setContentType("text/html");
						response.setCharacterEncoding(TextUtils.DEF_ENCODING);
						response.getWriter().append(ExchangeConstants.SESSION_NOT_AUTH_SIGN);
						response.getWriter().close();
					}
				}
			}
		}
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}

}
