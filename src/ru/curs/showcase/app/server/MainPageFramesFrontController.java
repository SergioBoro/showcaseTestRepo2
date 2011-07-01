package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.util.TextUtils;

/**
 * Front controller для получения "статических" фреймов, которые будут включены
 * в главную страницу приложения.
 */
public final class MainPageFramesFrontController extends HttpServlet {

	static final String UNKNOWN_COMMAND_ERROR =
		"Неизвестная команда для MainPageFramesFrontController";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String servlet = request.getServletPath();
			servlet = servlet.replace("/secured/", "").toUpperCase();
			MainPageFramesType action = MainPageFramesType.valueOf(servlet);

			switch (action) {
			case WELCOME:

				break;
			case HEADER:

				break;
			case FOOTER:

				break;
			default:
				fillErrorResponce(response, UNKNOWN_COMMAND_ERROR);
			}
			// handler.handle(request, response);

		} catch (Throwable e) {
			fillErrorResponce(response, e.getMessage());
		}

	}

	private void fillErrorResponce(final HttpServletResponse response, final String message)
			throws IOException {
		response.reset();
		ServletUtils.doNoCasheResponse(response);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);
		response.getWriter().append(message);
		response.getWriter().close();
	}
}
