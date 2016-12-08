package ru.curs.showcase.app.server.rest;

import org.python.core.PyObject;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * @author a.lugovtsov
 *
 */

public class RESTGateway {

	static private class ShowcaseRESTException extends BaseException {

		private static final long serialVersionUID = 6725288887092284411L;

		ShowcaseRESTException(final ExceptionType aType, final String aMessage) {
			super(aType, aMessage);
		}
	}

	static public JythonRestResult executeRESTcommand(final String requestType,
			final String userToken, final String acceptLanguage, final String requestUrl,
			final String requestData, final String urlParams, final String sesId,
			final String restProc) {
		String correctedRESTProc = restProc.trim();
		final int tri = 3;
		final int vosem = 8;
		if (restProc.endsWith(".cl")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - tri);
		}

		if (restProc.endsWith(".celesta")) {
			correctedRESTProc = restProc.substring(0, restProc.length() - vosem);
		}

		String tempSesId = "RESTful" + sesId;
		try {
			Celesta.getInstance().login(tempSesId, "userCelestaSid");
			PyObject pObj = Celesta.getInstance().runPython(tempSesId, correctedRESTProc,
					requestType, userToken, acceptLanguage, requestUrl, requestData, urlParams);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(JythonRestResult.class)) {
				return (JythonRestResult) obj;
			}

		} catch (CelestaException e) {
			throw new ShowcaseRESTException(ExceptionType.SOLUTION,
					"При запуске процедуры Celesta для выполнения REST запроса произошла ошибка: "
							+ e.getMessage());

		} finally {
			try {
				Celesta.getInstance().logout(tempSesId, false);
			} catch (CelestaException e) {
				throw new ShowcaseRESTException(ExceptionType.SOLUTION,
						"Пля выполнении REST запроса произошла ошибка при попытке выйти из сессии в celesta: "
								+ e.getMessage());
			}
		}

		return null;

	}

}
