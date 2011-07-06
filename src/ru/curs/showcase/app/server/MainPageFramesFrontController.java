package ru.curs.showcase.app.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.frame.MainPageFrameType;
import ru.curs.showcase.util.TextUtils;

/**
 * Front controller для получения "статических" фреймов, которые будут включены
 * в главную страницу приложения.
 */
public final class MainPageFramesFrontController extends HttpServlet {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 7991801050316249555L;

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String servlet = request.getServletPath();
			servlet = servlet.replace("/secured/", "").toUpperCase();
			MainPageFrameType type = MainPageFrameType.valueOf(servlet);
			Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
			CompositeContext context = new CompositeContext(params);
			ServiceLayerDataServiceImpl sl =
				new ServiceLayerDataServiceImpl(request.getSession().getId());
			String html = sl.getMainPageFrame(context, type);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			response.getWriter().append(html);
			response.getWriter().close();
		} catch (Throwable e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}
	}

}
