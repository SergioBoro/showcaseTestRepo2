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
 * Сервлет, обрабатывающий SQL и Jython submission из XForms. Является
 * универсальным обработчиком для не XSL преобразований XForm.
 */
public class XFormScriptTransformServlet extends HttpServlet {

	public static final String PROC_PARAM = "proc";

	private static final long serialVersionUID = -1387485389229827545L;

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse response)
			throws ServletException, IOException {
		String procName = req.getParameter(PROC_PARAM);
		if (procName == null) {
			throw new HTTPRequestRequiredParamAbsentException(PROC_PARAM);
		}

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(req);
		params.remove(PROC_PARAM);
		String content = ServletUtils.getRequestAsString(req);
		XFormContext context = new XFormContext(params, content);
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsSQLSubmissionInfo(procName);

		XFormScriptTransformCommand command = new XFormScriptTransformCommand(context, elInfo);
		String res = command.execute();

		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(response, res);
	}
}
