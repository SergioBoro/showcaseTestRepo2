package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONArray;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.LiveGridDataGetCommand;
import ru.curs.showcase.util.ServletUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

/**
 * Сервлет получения данных для JSGrid'ов.
 * 
 */
public class JSGridService extends HttpServlet {
	private static final long serialVersionUID = 350171574189068907L;

	@SuppressWarnings("unchecked")
	@Override
	protected void service(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String stringGridContext = hreq.getParameter(GridContext.class.getName());
		if (stringGridContext == null) {
			throw new HTTPRequestRequiredParamAbsentException(GridContext.class.getName());
		}
		String stringElementInfo = hreq.getParameter(PluginInfo.class.getName());
		if (stringElementInfo == null) {
			throw new HTTPRequestRequiredParamAbsentException(PluginInfo.class.getName());
		}

		GridContext context = null;
		PluginInfo element = null;
		try {
			context = (GridContext) ServletUtils.deserializeObject(stringGridContext);
			element = (PluginInfo) ServletUtils.deserializeObject(stringElementInfo);
		} catch (SerializationException e) {
			throw GeneralExceptionFactory.build(e);
		}

		LiveGridDataGetCommand command = new LiveGridDataGetCommand(context, element);
		LiveGridData<LiveGridModel> lgd = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType("application/json");
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------
		int firstIndex = 0;
		int lastIndex = 0;
		int totalCount = 0;
		if (context.getSubtype() == DataPanelElementSubType.EXT_TREE_GRID) {
			totalCount = lgd.getData().size();
			firstIndex = 0;
			lastIndex = totalCount - 1;
		} else {
			totalCount = context.getLiveInfo().getTotalCount();
			firstIndex = context.getLiveInfo().getOffset();
			lastIndex = context.getLiveInfo().getOffset() + context.getLiveInfo().getLimit() - 1;
		}

		hresp.setHeader("Content-Range",
				"items " + String.valueOf(firstIndex) + "-" + String.valueOf(lastIndex) + "/"
						+ String.valueOf(totalCount));

		// -------------------------------

		if (lgd.getData().size() > 0) {

			String stringLiveGridExtradata = null;
			try {
				stringLiveGridExtradata =
					RPC.encodeResponseForSuccess(
							FakeService.class.getMethod("serializeLiveGridExtradata"),
							lgd.getLiveGridExtradata());

			} catch (SerializationException | NoSuchMethodException | SecurityException e) {
				throw GeneralExceptionFactory.build(e);
			}

			lgd.getData().get(0).set("liveGridExtradata", stringLiveGridExtradata);

		}

		// -------------------------------

		JSONArray data = new JSONArray();
		for (LiveGridModel lgm : lgd.getData()) {
			data.add(lgm.getMap());
		}

		try (PrintWriter writer = hresp.getWriter()) {
			writer.print(data);
		}

		// ---------------------------------------------

	}

}
