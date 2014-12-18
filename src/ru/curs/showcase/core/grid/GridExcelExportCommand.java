package ru.curs.showcase.core.grid;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
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

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
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

		try {
			GridTransformer.includeDataPanelWidthAndHeightInSessionContext(getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		GridGetCommand command = new GridGetCommand(getContext(), getElementInfo(), false);
		grid = command.execute();
		initCommandContext();
	}

	@Override
	protected void mainProc() throws Exception {
		GridToExcelXMLFactory factory = new GridToExcelXMLFactory(grid);
		Document xml = factory.build(columnSet);
		ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
		setResult(new ExcelFile(stream));
	}

	@Override
	protected void postProcess() {
		super.postProcess();
		if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
			LOGGER.info(String.format("Размер возвращаемого файла: %d байт", getResult().getData()
					.size()));
		}
	}
}
