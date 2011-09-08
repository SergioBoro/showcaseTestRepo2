package ru.curs.showcase.model.grid;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.DataPanelElementCommand;
import ru.curs.showcase.util.ExcelFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда экспорта в грид.
 * 
 * @author den
 * 
 */
public final class GridExcelExportCommand extends DataPanelElementCommand<ExcelFile> {

	private final GridToExcelExportType exportType;
	private Grid grid;
	private final ColumnSet columnSet;

	public GridExcelExportCommand(final String aSessionId, final CompositeContext aContext,
			final DataPanelElementInfo aElInfo, final GridToExcelExportType aExportType,
			final ColumnSet cs) {
		super(aSessionId, aContext, aElInfo);
		exportType = aExportType;
		columnSet = cs;
	}

	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected void preProcess() throws GeneralException {
		super.preProcess();

		if (exportType == GridToExcelExportType.ALL) {
			getContext().resetForReturnAllRecords();
		}

		GridGetCommand command =
			new GridGetCommand(getSessionId(), getContext(), getElementInfo(), false);
		grid = command.execute();
	}

	@Override
	protected void mainProc() throws Exception {
		GridXMLBuilder builder = new GridXMLBuilder(grid);
		Document xml = builder.build(columnSet);
		ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
		setResult(new ExcelFile(stream));
	}
}
