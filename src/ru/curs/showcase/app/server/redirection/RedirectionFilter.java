package ru.curs.app.server;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class RedirectionFilter implements Filter {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		// if (request instanceof HttpServletRequest) {
		// (new Server()).

		if (httpReq.getRequestURI().toString().contains("mp4")) {

			// httpRes.SC_m
			// if (!httpReq.getRequestURI().toString().contains("video1.mp4"))
			// httpRes.send
			httpRes.sendRedirect("http://172.16.1.132/anafilaxy/data/video1.mp4");
			// httpRes.sendRedirect(httpReq.getContextPath() + "/video1.mp4");
			// else
			// filterChain.doFilter(request, response);
		} else

			filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {
	}
}
