package ru.curs.showcase.app.server;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.plugin.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.server.internatiolization.CourseLocalization;
import ru.curs.showcase.core.chart.ChartGetCommand;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.event.ExecServerActivityCommand;
import ru.curs.showcase.core.frame.MainPageGetCommand;
import ru.curs.showcase.core.geomap.GeoMapGetCommand;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.core.grid.toolbar.GridToolBarCommand;
import ru.curs.showcase.core.html.plugin.PluginCommand;
import ru.curs.showcase.core.html.webtext.WebTextGetCommand;
import ru.curs.showcase.core.html.xform.*;
import ru.curs.showcase.core.plugin.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.core.primelements.navigator.NavigatorGetCommand;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.logging.Event.TypeEvent;
import ru.curs.showcase.security.logging.*;
import ru.curs.showcase.util.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service. Является декоратором для
 * сервисного слоя приложения.
 */
@SuppressWarnings("serial")
// CHECKSTYLE:OFF
public class DataServiceImpl extends RemoteServiceServlet implements DataService {

	@Override
	public Navigator getNavigator(final CompositeContext context) throws GeneralException {
		Date dt1 = new Date();
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator navigator = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog("Navigator. Общее время загрузки.", dt1, dt2, "NAVIGATOR", "");

		return navigator;
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralException {
		Date dt1 = new Date();
		DataPanelGetCommand command = new DataPanelGetCommand(action);
		DataPanel dp = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(dp.getId().getString(), dt1, dt2, "DATAPANEL", "");

		return dp;
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(), "");

		return wt;
	}

	@Override
	public GridMetadata getGridMetadata(GridContext context, DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		GridMetadataGetCommand command = new GridMetadataGetCommand(context, element);
		GridMetadata gm = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(),
				element.getSubtype().toString());

		return gm;
	}

	@Override
	public GridData getGridData(GridContext context, DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		GridDataGetCommand command = new GridDataGetCommand(context, element, true);
		GridData gd = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(),
				element.getSubtype().toString());

		return gd;
	}

	@Override
	public LyraGridMetadata getLyraGridMetadata(LyraGridContext context,
			DataPanelElementInfo element) throws GeneralException {
		Date dt1 = new Date();
		LyraGridMetadataGetCommand command = new LyraGridMetadataGetCommand(context, element);
		LyraGridMetadata gm = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(),
				element.getSubtype().toString());

		return gm;
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		ChartGetCommand command = new ChartGetCommand(context, element);
		Chart chart = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(), "");

		return chart;
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		GeoMapGetCommand command = new GeoMapGetCommand(context, element);
		GeoMap gm = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(), "");

		return gm;
	}

	@Override
	public XForm getXForms(final XFormContext context, final DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		XFormGetCommand command = new XFormGetCommand(context, element);
		XForm xform = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId() + ". Общее время загрузки.", dt1, dt2,
				element.getType().toString(), "");

		return xform;
	}

	@Override
	public List<String> getMainXForms() throws GeneralException {
		MainXFormGetCommand command = new MainXFormGetCommand();
		return command.execute();
	}

	@Override
	public VoidElement saveXForms(final XFormContext context, final DataPanelElementInfo element)
			throws GeneralException {
		Date dt1 = new Date();
		XFormSaveCommand command = new XFormSaveCommand(context, element);
		VoidElement ve = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(element.getFullId(), dt1, dt2, element.getType().toString(),
				"SAVEXFORMS");

		return ve;

	}

	@Override
	public ServerState getServerCurrentState(final CompositeContext context)
			throws GeneralException {
		Date dt1 = new Date();
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		ServerState state = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog("ServerCurrentState", dt1, dt2, "SERVERCURRENTSTATE", "");

		return state;
	}

	@Override
	public VoidElement execServerAction(final Action action) throws GeneralException {
		Date dt1 = new Date();
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		Date dt2 = new Date();
		VoidElement ve = command.execute();

		LoggerHelper.profileToLog("execServerAction", dt1, dt2, "EXECSERVERACTION", "");

		return ve;
	}

	@Override
	public MainPage getMainPage(final CompositeContext context) throws GeneralException {
		Date dt1 = new Date();

		HttpServletRequest request = getThreadLocalRequest();
		SecurityLoggingCommand logCommand =
			new SecurityLoggingCommand(context, request, request.getSession(), TypeEvent.LOGIN);
		logCommand.execute();

		MainPageGetCommand command = new MainPageGetCommand(context);
		MainPage mainPage = command.execute();

		Date dt2 = new Date();

		LoggerHelper.profileToLog("MainPage", dt1, dt2, "MAINPAGE", "");

		return mainPage;
	}

	@Override
	public Plugin getPlugin(final RequestData requestData) throws GeneralException {
		Date dt1 = new Date();
		PluginCommand command = new PluginCommand(requestData);
		Plugin plugin = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(requestData.getElInfo().getFullId(), dt1, dt2, "PLUGIN", "");

		return plugin;
	}

	@Override
	public ResponceData getPluginData(final RequestData requestData) {
		Date dt1 = new Date();
		ResponceData responceData = new ResponceData();
		GetDataPluginCommand command = new GetDataPluginCommand(requestData);
		ResultPluginData result = command.execute();
		responceData.setJsonData(result.getData());
		Date dt2 = new Date();

		LoggerHelper.profileToLog(requestData.getElInfo().getFullId(), dt1, dt2, "PLUGINDATA", "");

		return responceData;
	}

	@Override
	public GridToolBar getGridToolBar(final CompositeContext context,
			final DataPanelElementInfo elInfo) throws GeneralException {
		Date dt1 = new Date();
		GridToolBarCommand command = new GridToolBarCommand(context, elInfo);
		GridToolBar gtb = command.execute();
		Date dt2 = new Date();

		LoggerHelper.profileToLog(elInfo.getFullId(), dt1, dt2, elInfo.getType().toString(), "");

		return gtb;
	}

	@Override
	public void writeToLog(final CompositeContext aContext, final String aMessage,
			final MessageType aMessageType) throws GeneralException {
		WriteToLogFromClientCommand command =
			new WriteToLogFromClientCommand(aContext, aMessage, aMessageType);
		command.execute();
	}

	@Override
	public Map<String, String> getBundle(CompositeContext context) throws GeneralException {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		Map<String, String> map = new HashMap<String, String>();
		ResourceBundle bundle = null;
		String lang = "";
		lang = UserDataUtils.getLocaleForCurrentUserdata();

		if (!("en".equals(lang))) {
			bundle =
				ResourceBundle
						.getBundle("ru.curs.showcase.app.server.internatiolization.constantsShowcase");
		} else {
			Locale loc = new Locale("en");
			bundle =
				ResourceBundle.getBundle(
						"ru.curs.showcase.app.server.internatiolization.constantsShowcase", loc);
		}

		if (bundle != null) {
			for (String key : bundle.keySet()) {
				map.put(key, bundle.getString(key));
			}
		}
		return map;
	}

	@Override
	public String getLocalizationBundleDomainName(CompositeContext context) {
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sesid = ((UserAndSessionDetails) auth.getDetails()).getSessionId();
			AppInfoSingleton.getAppInfo().getLocalizedBundleCache()
					.put(sesid, CourseLocalization.getLocalizedResourseBundle());
		}
		// String lang = UserDataUtils.getLocaleForCurrentUserdata();
		// String platform = lang.equals("") ? "platform" : "platform" + "_" +
		// lang;
		//
		// ProcessBuilder pb = new ProcessBuilder();
		// String classpath = pb.environment().get("CLASSPATH");
		// String localizePath = classpath.substring(classpath.lastIndexOf(";")
		// + 1);
		//
		// File localizeDir = new File(localizePath);
		//
		// String domainFile = "";
		// for (String file : localizeDir.list()) {
		// if (file.equals(platform + ".po")) {
		// domainFile = file;
		// break;
		// }
		// }

		String domainFile =
			UserDataUtils.getPlatformPoFile(AppInfoSingleton.getAppInfo().getCurUserDataId());

		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sesid = ((UserAndSessionDetails) auth.getDetails()).getSessionId();

			String lang = AppInfoSingleton.getAppInfo().getLocalizationCache().get(sesid);

			if (lang != null && !"".equals(lang))
				domainFile =
					UserDataUtils.getPlatformPoFile(AppInfoSingleton.getAppInfo()
							.getCurUserDataId(), lang);

		}

		if ("".equals(domainFile))
			domainFile = UserDataUtils.getDefaultPlatformPoFile();

		String result = "";

		if (!"".equals(domainFile) && domainFile.contains("."))
			result = domainFile.substring(0, domainFile.lastIndexOf("."));

		return result;
	}
}
