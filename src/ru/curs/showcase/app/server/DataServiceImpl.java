package ru.curs.showcase.app.server;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.model.chart.ChartGetCommand;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.model.event.ExecServerActivityCommand;
import ru.curs.showcase.model.frame.MainPageGetCommand;
import ru.curs.showcase.model.geomap.GeoMapGetCommand;
import ru.curs.showcase.model.grid.GridGetCommand;
import ru.curs.showcase.model.html.webtext.WebTextGetCommand;
import ru.curs.showcase.model.html.xform.*;
import ru.curs.showcase.model.primelements.datapanel.DataPanelGetCommand;
import ru.curs.showcase.model.primelements.navigator.NavigatorGetCommand;

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
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		ChartGetCommand command = new ChartGetCommand(context, element);
		return command.execute();
	}

	@Override
	public void saveColumnSet(final ColumnSet aCs, final GeoMapExportSettings settings)
			throws GeneralException {
		// fake метод для корректной сериализации
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
}
