package ru.curs.showcase.app.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.util.ServletUtils;

/**
 * Сервлет, обрабатывающий submission из XForms.
 */
public class XFormSQLTransformServlet extends HttpServlet {

	private static final String PROC_PARAM = "proc";
	private static final String PROC_PARAM_ERROR =
		"В XFormsSQLTransformServlet не передан обязательный параметр proc";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1387485389229827545L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse response)
			throws ServletException, IOException {
		String procName = req.getParameter(PROC_PARAM);
		if (procName == null) {
			throw new ServletException(PROC_PARAM_ERROR);
		}

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(req);
		params.remove(PROC_PARAM);
		String content = ServletUtils.getRequestAsString(req);
		XFormContext context = new XFormContext(params, content);
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsSQLSubmissionInfo(procName);

		XFormSQLTransformCommand command = new XFormSQLTransformCommand(context, elInfo);
		String res = command.execute();

		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(response, res);
	}
}
