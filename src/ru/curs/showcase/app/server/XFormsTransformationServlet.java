package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.TextUtils;

/**
 * Сервлет, обрабатывающий xslt-преобразование из XForms.
 */
public class XFormsTransformationServlet extends HttpServlet {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 382470453045525219L;

	static final String XSLTFILE_PARAM_ERROR =
		"В XFormsTransformationServlet не передан обязательный параметр xsltfile";

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		String xsltFile = request.getParameter("xsltfile");
		if (xsltFile == null) {
			throw new ServletException(XSLTFILE_PARAM_ERROR);
		}
		String userDataId = request.getParameter(ExchangeConstants.URL_PARAM_USERDATA);
		String content = ServletUtils.getRequestAsString(request);

		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);

		try {
			ServiceLayerDataServiceImpl sl =
				new ServiceLayerDataServiceImpl(request.getSession().getId());
			String res = sl.handleXSLTSubmission(xsltFile, content, userDataId);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().append(res);
			response.getWriter().close();
		} catch (Exception e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}

	}

}
