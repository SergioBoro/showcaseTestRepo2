package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.util.ServletUtils;

/**
 * Обработчик экспорта в PNG.
 * 
 * @author den
 * 
 */
public class GeoMapToPNGHandler extends HttpServlet {

	private static final long serialVersionUID = -4464217023791442531L;

	public GeoMapToPNGHandler() {
		super();
	}

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		if (ServletUtils.isOldIE(request)) {
			response.setContentType("application/force-download");
		} else {
			response.setContentType("application/octet-stream");
		}
		// По агентурным данным для старых версий IE "application/octet-stream"
		// обрабатывается некорректно.
		response.setHeader("Content-Disposition",
				String.format("attachment; filename=\"%s\"", "geomap.svg"));

		response.getWriter().append(request.getParameter("svg"));
	}

}
