package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.model.command.ExternalCommand;
import ru.curs.showcase.util.ServletUtils;

/**
 * Сервлет - аналог веб-сервисов.
 * 
 * @author den
 * 
 */
public class ExternalServlet extends HttpServlet {

	private static final String REQUEST_STRING = "requestString";

	private static final long serialVersionUID = -4937856990909960895L;

	private static final String PROC_PARAM = "proc";
	private static final String PROC_PARAM_ERROR =
		"В ExternalServlet не передан обязательный параметр proc";

	@Override
	protected void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {
		String procName = hreq.getParameter(PROC_PARAM);
		if (procName == null) {
			throw new ServletException(PROC_PARAM_ERROR);
		}
		String request = hreq.getParameter(REQUEST_STRING);
		if (request == null) {
			throw new ServletException(REQUEST_STRING);
		}

		ExternalCommand command = new ExternalCommand(request, procName);
		String response = command.execute();

		hresp.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(hresp, response);
	}
}
