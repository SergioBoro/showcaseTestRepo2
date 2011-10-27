package ru.curs.showcase.model.grid;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.util.ExcelFile;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда экспорта в грид.
 * 
 * @author den
 * 
 */
public final class GridExcelExportCommand extends DataPanelElementCommand<ExcelFile> {

	private Grid grid;

	private final GridToExcelExportType exportType;

	@InputParam
	public GridToExcelExportType getExportType() {
		return exportType;
	}

	private final ColumnSet columnSet;

	@InputParam
	public ColumnSet getColumnSet() {
		return columnSet;
	}

	public GridExcelExportCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final GridToExcelExportType aExportType, final ColumnSet cs) {
		super(aContext, aElInfo);
		exportType = aExportType;
		columnSet = cs;
	}

	public GridExcelExportCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final GridToExcelExportType aExportType) {
		super(aContext, aElInfo);
		exportType = aExportType;
		columnSet = null;
	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected void preProcess() {
		super.preProcess();

		if (exportType == GridToExcelExportType.ALL) {
			getContext().resetForReturnAllRecords();
		}

		GridGetCommand command = new GridGetCommand(getContext(), getElementInfo(), false);
		grid = command.execute();
	}

	@Override
	protected void mainProc() throws Exception {
		GridToExcelXMLFactory builder = new GridToExcelXMLFactory(grid);
		Document xml = builder.build(columnSet);
		ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
		setResult(new ExcelFile(stream));
	}
}
