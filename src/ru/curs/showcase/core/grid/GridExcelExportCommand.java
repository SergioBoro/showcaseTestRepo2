package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.ExcelFile;

/**
 * Команда экспорта в грид.
 * 
 * @author den
 * 
 */
public final class GridExcelExportCommand extends DataPanelElementCommand<ExcelFile> {

	// private Grid grid;

	private final GridToExcelExportType exportType;

	@InputParam
	public GridToExcelExportType getExportType() {
		return exportType;
	}

	// private final ColumnSet columnSet;

	// @InputParam
	// public ColumnSet getColumnSet() {
	// return columnSet;
	// }

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridExcelExportCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final GridToExcelExportType aExportType, /*
													 * Refactoring final
													 * ColumnSet cs
													 */final int dummy) {
		super(aContext, aElInfo);
		exportType = aExportType;
		// columnSet = cs;
	}

	public GridExcelExportCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final GridToExcelExportType aExportType) {
		super(aContext, aElInfo);
		exportType = aExportType;
		// columnSet = null;
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
			GridUtils.includeDataPanelWidthAndHeightInSessionContext(getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// -----------------------------------Рефакторинг
		// GridGetCommand command = new GridGetCommand(getContext(),
		// getElementInfo(), false);
		// grid = command.execute();
		// -----------------------------------Рефакторинг
		initCommandContext();
	}

	@Override
	protected void mainProc() throws Exception {
		// -----------------------------------Рефакторинг
		// GridToExcelXMLFactory factory = new GridToExcelXMLFactory(grid);
		// Document xml = factory.build(columnSet);
		// ByteArrayOutputStream stream = XMLUtils.xsltTransformForGrid(xml);
		// setResult(new ExcelFile(stream));
		// -----------------------------------Рефакторинг
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
