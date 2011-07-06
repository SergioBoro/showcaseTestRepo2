package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.model.RequestResult;

/**
 * Сервлет, обрабатывающий submission из XForms.
 */
public class XFormsSubmissionServlet extends HttpServlet {

	static final String PROC_PARAM_ERROR =
		"В XFormsSubmissionServlet не передан обязательный параметр proc";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1387485389229827545L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse response)
			throws ServletException, IOException {
		String procName = req.getParameter("proc");
		if (procName == null) {
			throw new ServletException(PROC_PARAM_ERROR);
		}
		String userDataId = req.getParameter(ExchangeConstants.URL_PARAM_USERDATA);
		String content = ServletUtils.getRequestAsString(req);

		try {
			ServiceLayerDataServiceImpl sl =
				new ServiceLayerDataServiceImpl(req.getSession().getId());
			RequestResult res = sl.handleSQLSubmission(procName, content, userDataId);
			if (res.getSuccess()) {
				response.setStatus(HttpServletResponse.SC_OK);
				ServletUtils.makeResponseFromString(response, res.getData());
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtils.makeResponseFromString(response, res.generateStandartErrorMessage());
			}
		} catch (Exception e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}
	}
}
