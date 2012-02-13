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
 * Сервлет, обрабатывающий xslt-преобразование из XForms.
 */
public class XFormXSLTransformServlet extends HttpServlet {

	public static final String XSLTFILE_PARAM = "xsltfile";

	private static final long serialVersionUID = 382470453045525219L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String xsltFile = request.getParameter(XSLTFILE_PARAM);
		if (xsltFile == null) {
			throw new HTTPRequestRequiredParamAbsentException(XSLTFILE_PARAM);
		}

		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
		params.remove(XSLTFILE_PARAM);
		String content = ServletUtils.getRequestAsString(request);
		XFormContext context = new XFormContext(params, content);
		DataPanelElementInfo elInfo = XFormInfoFactory.generateXFormsTransformationInfo(xsltFile);

		XFormXSLTransformCommand command = new XFormXSLTransformCommand(context, elInfo);
		String res = command.execute();

		response.setStatus(HttpServletResponse.SC_OK);
		ServletUtils.makeResponseFromString(response, res);
	}

}
