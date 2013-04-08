package ru.curs.showcase.app.server;

import java.util.List;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.GridToolBar;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;
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
import ru.curs.showcase.core.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.core.primelements.navigator.NavigatorGetCommand;

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
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		return command.execute();
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralException {
		DataPanelGetCommand command = new DataPanelGetCommand(action);
		return command.execute();
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		WebTextGetCommand command = new WebTextGetCommand(context, element);
		return command.execute();
	}

	@Override
	public Grid getGrid(final GridContext context, final DataPanelElementInfo element)
			throws GeneralException {
		GridGetCommand command = new GridGetCommand(context, element, true);
		return command.execute();
	}

	@Override
	public LiveGridMetadata getLiveGridMetadata(GridContext context, DataPanelElementInfo element)
			throws GeneralException {
		LiveGridMetadataGetCommand command = new LiveGridMetadataGetCommand(context, element);
		return command.execute();
	}

	@Override
	public LiveGridData<LiveGridModel> getLiveGridData(GridContext context,
			DataPanelElementInfo element) throws GeneralException {
		LiveGridDataGetCommand command = new LiveGridDataGetCommand(context, element);
		return command.execute();
	}

	@Override
	public List<TreeGridModel> getTreeGridData(GridContext context, DataPanelElementInfo element)
			throws GeneralException {
		TreeGridDataGetCommand command = new TreeGridDataGetCommand(context, element);
		return command.execute();
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		ChartGetCommand command = new ChartGetCommand(context, element);
		return command.execute();
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		GeoMapGetCommand command = new GeoMapGetCommand(context, element);
		return command.execute();
	}

	@Override
	public XForm getXForms(final XFormContext context, final DataPanelElementInfo element)
			throws GeneralException {
		XFormGetCommand command = new XFormGetCommand(context, element);
		return command.execute();
	}

	@Override
	public List<String> getMainXForms() throws GeneralException {
		MainXFormGetCommand command = new MainXFormGetCommand();
		return command.execute();
	}

	@Override
	public void saveXForms(final XFormContext context, final DataPanelElementInfo element)
			throws GeneralException {
		XFormSaveCommand command = new XFormSaveCommand(context, element);
		command.execute();
	}

	@Override
	public ServerState getServerCurrentState(final CompositeContext context)
			throws GeneralException {
		ServerStateGetCommand command = new ServerStateGetCommand(context);
		return command.execute();
	}

	@Override
	public void execServerAction(final Action action) throws GeneralException {
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();
	}

	@Override
	public MainPage getMainPage(final CompositeContext context) throws GeneralException {
		MainPageGetCommand command = new MainPageGetCommand(context);
		return command.execute();
	}

	@Override
	public Plugin getPlugin(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) throws GeneralException {
		PluginCommand command = new PluginCommand(aContext, aElementInfo);
		return command.execute();
	}

	@Override
	public void writeToLog(final CompositeContext aContext, final String aMessage,
			final MessageType aMessageType) throws GeneralException {
		WriteToLogFromClientCommand command =
			new WriteToLogFromClientCommand(aContext, aMessage, aMessageType);
		command.execute();
	}

	@Override
	public GridToolBar getGridToolBar(final CompositeContext context, final DataPanelElementInfo elInfo) throws GeneralException {
		GridToolBarCommand command = new GridToolBarCommand(context, elInfo);
		return command.execute();
	}
}
