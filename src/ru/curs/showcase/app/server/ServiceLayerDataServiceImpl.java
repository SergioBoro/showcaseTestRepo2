package ru.curs.showcase.app.server;

import java.io.ByteArrayOutputStream;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.model.chart.ChartGetCommand;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.model.datapanel.DataPanelGetCommand;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.model.geomap.GeoMapGetCommand;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.navigator.NavigatorGetCommand;
import ru.curs.showcase.model.webtext.WebTextGetCommand;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.util.*;

/**
 * Реализация функций сервисного слоя приложения не зависимая от GWT Servlet.
 * Позволяет вызывать функции сервисного слоя не из GWT кода. Нельзя создавать
 * экземпляр данного класса, не указав при этом aSessionId.
 * 
 * @author den
 * 
 */
public final class ServiceLayerDataServiceImpl implements DataService, DataServiceExt {

	/**
	 * Идентификатор текущей HTTP сессии.
	 */
	private final String sessionId;

	public ServiceLayerDataServiceImpl(final String aSessionId) {
		super();
		sessionId = aSessionId;
	}

	@Override
	public Navigator getNavigator(final CompositeContext context) throws GeneralException {
		NavigatorGetCommand command = new NavigatorGetCommand(sessionId, context);
		return command.execute();
	}

	@Override
	public DataPanel getDataPanel(final Action action) throws GeneralException {
		DataPanelGetCommand command = new DataPanelGetCommand(sessionId, action);
		return command.execute();
	}

	@Override
	public WebText getWebText(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		WebTextGetCommand command = new WebTextGetCommand(sessionId, context, element);
		return command.execute();
	}

	@Override
	public Grid getGrid(final GridContext context, final DataPanelElementInfo elementInfo)
			throws GeneralException {
		GridGetCommand command = new GridGetCommand(sessionId, context, elementInfo, true);
		return command.execute();
	}

	@Override
	public Chart getChart(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		ChartGetCommand command = new ChartGetCommand(sessionId, context, element);
		return command.execute();
	}

	@Override
	public ExcelFile generateExcelFromGrid(final GridToExcelExportType exportType,
			final GridContext context, final DataPanelElementInfo element, final ColumnSet cs)
			throws GeneralException {
		GridExcelExportCommand command =
			new GridExcelExportCommand(sessionId, context, element, exportType, cs);
		return command.execute();
	}

	@Override
	public void saveColumnSet(final ColumnSet aCs) throws GeneralException {
		// fake метод для корректной сериализации
	}

	@Override
	public GeoMap getGeoMap(final CompositeContext context, final DataPanelElementInfo element)
			throws GeneralException {
		GeoMapGetCommand command = new GeoMapGetCommand(sessionId, context, element);
		return command.execute();
	}

	@Override
	public XForm getXForms(final XFormContext context, final DataPanelElementInfo element)
			throws GeneralException {
		XFormGetCommand command = new XFormGetCommand(sessionId, context, element);
		return command.execute();
	}

	@Override
	public void saveXForms(final XFormContext context, final DataPanelElementInfo elementInfo)
			throws GeneralException {
		XFormSaveCommand command = new XFormSaveCommand(sessionId, context, elementInfo);
		command.execute();
	}

	@Override
	public String
			handleSQLSubmission(final XFormContext context, final DataPanelElementInfo elInfo)
					throws GeneralException {
		XFormSQLTransformCommand command =
			new XFormSQLTransformCommand(sessionId, context, elInfo);
		return command.execute();
	}

	@Override
	public String handleXSLTSubmission(final XFormContext context,
			final DataPanelElementInfo elInfo) throws GeneralException {
		XFormXSLTransformCommand command =
			new XFormXSLTransformCommand(sessionId, context, elInfo);
		return command.execute();
	}

	@Override
	public ServerState getServerCurrentState(final CompositeContext context)
			throws GeneralException {
		ServerStateGetCommand command = new ServerStateGetCommand(sessionId, context);
		return command.execute();
	}

	@Override
	public DataFile<ByteArrayOutputStream> getDownloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final String linkId) throws GeneralException {
		XFormDownloadCommand command =
			new XFormDownloadCommand(sessionId, context, elementInfo, linkId);
		return command.execute();
	}

	@Override
	public void uploadFile(final XFormContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final DataFile<ByteArrayOutputStream> file)
			throws GeneralException {
		XFormUploadCommand command =
			new XFormUploadCommand(sessionId, context, elementInfo, linkId, file);
		command.execute();
	}

	@Override
	public void execServerAction(final Action action) throws GeneralException {
		ExecServerActionCommand command = new ExecServerActionCommand(sessionId, action);
		command.execute();
	}

	@Override
	public MainPage getMainPage(final CompositeContext context) throws GeneralException {
		MainPageGetCommand command = new MainPageGetCommand(sessionId, context);
		return command.execute();
	}

	@Override
	public String getMainPageFrame(final CompositeContext context, final MainPageFrameType type)
			throws GeneralException {
		MainPageFrameGetCommand command = new MainPageFrameGetCommand(sessionId, context, type);
		return command.execute();
	}

}
