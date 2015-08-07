package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.simple.JSONArray;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.FakeService;
import ru.curs.showcase.core.command.GeneralExceptionFactory;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.util.ServletUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;

/**
 * Сервлет работы с данными для JSGrid'ов.
 * 
 */
public class JSGridService extends HttpServlet {
	private static final long serialVersionUID = 350171574189068907L;

	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	@Override
	protected void doPost(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws ServletException, IOException {

		String editor = hreq.getParameter("editor");
		if (editor == null) {
			getData(hreq, hresp);
		} else {
			if ("addRecord".equals(editor)) {
				addRecord(hreq, hresp);
			} else {
				saveData(hreq, hresp);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void getData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

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

		try {
			GridTransformer.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		LiveGridDataGetCommand command = new LiveGridDataGetCommand(context, element);
		LiveGridData<LiveGridModel> lgd = command.execute();

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
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

	private void saveData(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

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

		try {
			GridTransformer.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		String success = "1";
		UserMessage um = null;
		boolean refreshAfterSave = false;

		try {
			GridSaveDataCommand command = new GridSaveDataCommand(context, element);
			GridSaveResult gridSaveResult = command.execute();

			success = "1";
			if (gridSaveResult != null) {
				um = gridSaveResult.getOkMessage();
				if ((um != null) && (um.getType() == MessageType.ERROR)) {
					success = "0";
				}
				refreshAfterSave = gridSaveResult.isRefreshAfterSave();
			}

		} catch (Exception e) {
			success = "0";
			um = new UserMessage(e.getMessage(), MessageType.ERROR);
		}

		String message = null;
		try {
			message =
				RPC.encodeResponseForSuccess(FakeService.class.getMethod("serializeUserMessage"),
						um);
		} catch (SerializationException | NoSuchMethodException | SecurityException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try (PrintWriter writer = hresp.getWriter()) {
			message = message.replace("\"", "'");
			writer.print("{\"success\":\"" + success + "\", \"message\":\"" + message
					+ "\", \"refreshAfterSave\":\"" + refreshAfterSave + "\"}");
		}

	}

	private void addRecord(final HttpServletRequest hreq, final HttpServletResponse hresp)
			throws IOException {

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

		try {
			GridTransformer.fillFilterContextByFilterInfo(context);
		} catch (Exception e) {
			throw GeneralExceptionFactory.build(e);
		}

		// ---------------------------------------------

		hresp.setStatus(HttpServletResponse.SC_OK);
		hresp.setContentType(CONTENT_TYPE_APPLICATION_JSON);
		hresp.setCharacterEncoding("UTF-8");

		// ---------------------------------------------

		String success = "1";
		UserMessage um = null;

		try {
			GridAddRecordCommand command = new GridAddRecordCommand(context, element);
			GridAddRecordResult gridAddRecordResult = command.execute();

			success = "1";
			if (gridAddRecordResult != null) {
				um = gridAddRecordResult.getOkMessage();
				if ((um != null) && (um.getType() == MessageType.ERROR)) {
					success = "0";
				}
			}

		} catch (Exception e) {
			success = "0";
			um = new UserMessage(e.getMessage(), MessageType.ERROR);
		}

		String message = null;
		try {
			message =
				RPC.encodeResponseForSuccess(FakeService.class.getMethod("serializeUserMessage"),
						um);
		} catch (SerializationException | NoSuchMethodException | SecurityException e) {
			throw GeneralExceptionFactory.build(e);
		}

		try (PrintWriter writer = hresp.getWriter()) {
			message = message.replace("\"", "'");
			writer.print("{\"success\":\"" + success + "\", \"message\":\"" + message + "\"}");
		}

	}

}
