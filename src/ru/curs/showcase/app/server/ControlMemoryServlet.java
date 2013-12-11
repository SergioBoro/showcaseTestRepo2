package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import ru.curs.celesta.*;
import ru.curs.showcase.runtime.*;

/**
 * Сервлет для контроля используемой Showcase памяти.
 */
public class ControlMemoryServlet extends HttpServlet {
	public static final String UNKNOWN_PARAM_ERROR = "Неизвестное значение параметра pool";
	public static final String NO_PARAMS_ERROR =
		"Должен быть задан один из параметров: pool, gc или userdata";
	public static final String USERDATA_PARAM = "userdata";
	public static final String GC_PARAM = "gc";
	public static final String POOL_PARAM = "pool";
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ControlMemoryServlet.class);

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String pool = request.getParameter(POOL_PARAM);
		String gc = request.getParameter(GC_PARAM);
		String userdata = request.getParameter(USERDATA_PARAM);
		if ((pool == null) && (gc == null) && (userdata == null)) {
			throw new ServletException(NO_PARAMS_ERROR);
		}
		if (pool != null) {
			switch (pool) {
			case "jdbc":
				ConnectionFactory.getInstance().clear();
				break;
			case "jython":
				JythonIterpretatorFactory.getInstance().clear();
				break;
			case "jythonCelesta":
				try {
					Celesta.getInstance().clearInterpretersPool();
				} catch (CelestaException e) {
					LOGGER.error("Ошибка очистки пула интерпретаторов Сelesta ", e);
				}
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
				throw new ServletException(UNKNOWN_PARAM_ERROR);
			}
		}
		if (gc != null) {
			Runtime.getRuntime().gc();
		}
		if (userdata != null) {
			ProductionModeInitializer.initUserDatas(request.getSession().getServletContext());
		}
	}
}
