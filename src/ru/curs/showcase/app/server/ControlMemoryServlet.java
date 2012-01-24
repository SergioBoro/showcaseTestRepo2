package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.runtime.*;

/**
 * Сервлет для контроля используемой Showcase памяти.
 */
public class ControlMemoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String pool = request.getParameter("pool");
		String gc = request.getParameter("gc");
		if ((pool == null) && (gc == null)) {
			throw new ServletException("Должен быть задан один из параметров: pool или gc");
		}
		if (pool != null) {
			switch (pool) {
			case "jdbc":
				ConnectionFactory.getInstance().clear();
				break;
			case "jython":
				JythonIterpretatorFactory.getInstance().clear();
				break;
			case "xsl":
				XSLTransformerPoolFactory.getInstance().clear();
				break;
			case "all":
				ConnectionFactory.getInstance().clear();
				JythonIterpretatorFactory.getInstance().clear();
				XSLTransformerPoolFactory.getInstance().clear();
				break;
			default:
				throw new ServletException("Неизвестное значение параметра pool");
			}
		}
		if (gc != null) {
			Runtime.getRuntime().gc();
		}
	}
}
