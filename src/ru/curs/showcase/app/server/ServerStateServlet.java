package ru.curs.showcase.app.server;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import ru.curs.showcase.app.api.ServerState;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.command.ServerStateGetCommand;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLObjectSerializer;

/**
 * Сервлет для возврата состояния сервера.
 * 
 * @author den
 * 
 */
public final class ServerStateServlet extends HttpServlet {

	private static final long serialVersionUID = -3101461389195836031L;

	public ServerStateServlet() {
		super();
	}

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, List<String>> params = ServletUtils.prepareURLParamsMap(request);
		CompositeContext context = new CompositeContext(params);
		try {
			ServerStateGetCommand command = new ServerStateGetCommand(context);
			ServerState state = command.execute();

			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/html");
			response.setCharacterEncoding(TextUtils.DEF_ENCODING);
			ObjectSerializer serializer = new XMLObjectSerializer();
			String message = serializer.serialize(state);
			message = message.replace("<", "&lt;");
			message = message.replace(">", "&gt;");
			response.getWriter().append(message);
			response.getWriter().close();
		} catch (Exception e) {
			ServletUtils.fillErrorResponce(response, e.getLocalizedMessage());
		}
	}

}
