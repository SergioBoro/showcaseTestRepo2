package ru.curs.showcase.app.client;

import java.util.List;

import ru.curs.gwt.datagrid.*;
import ru.curs.gwt.datagrid.event.*;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.gwt.datagrid.selection.*;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с гридом.
 */
public class GridPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";
	private static final String SHOWCASE_APP_CONTAINER = "showcaseAppContainer";

	private final VerticalPanel p = new VerticalPanel();
	private final HorizontalPanel hpHeader = new HorizontalPanel();
	private final HorizontalPanel hpToolbar = new HorizontalPanel();
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	private final PushButton exportToExcelCurrentPage = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	private final PushButton exportToExcelAll = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));
	private final PushButton copyToClipboard = new PushButton(new Image(
			Constants.GRID_IMAGE_COPY_TO_CLIPBOARD));

	private final DataGridSettings settingsDataGrid = new DataGridSettings();
	private DataGrid dg = null;
	private ColumnSet cs = null;
	private Timer selectionTimer = null;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
	private Grid grid = null;

	/**
	 * Для предотвращения повторного срабатывания обработчиков.
	 */
	private boolean bListenersExit = true;

	private boolean isFirstLoading = true;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean isFirstLoading1) {
		isFirstLoading = isFirstLoading1;
	}

	private String strHeader = "";
	private String strFooter = "";

	/**
	 * Тип обновления грида.
	 */
	private enum UpdateType {
		/**
		 * Полный.
		 */
		FULL,

		/**
		 * Только записи вызовом ф-ции updateRecordSet.
		 */
		RECORDSET_BY_UPDATERECORDSET,

		/**
		 * Только записи вызовом ф-ции showData.
		 */
		RECORDSET_BY_SHOWDATA,

		/**
		 * Обновление при вызове ф-ции redrawGrid.
		 */
		UPDATE_BY_REDRAWGRID

	}

	/**
	 * Ф-ция, возвращающая панель с гридом.
	 * 
	 * @return - Панель с гридом.
	 */
	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	@Override
	public DataPanelElement getElement() {
		return grid;
	}

	/**
	 * Конструктор класса GridPanel без начального показа грида.
	 */
	public GridPanel(final DataPanelElementInfo element) {

		setElementInfo(element);
		setContext(null);
		setFirstLoading(true);

		// --------------

	}

	/**
	 * Конструктор класса GridPanel.
	 */
	public GridPanel(final CompositeContext context, final DataPanelElementInfo element,
			final Grid grid1) {

		this.setContext(context);
		this.setElementInfo(element);
		setFirstLoading(true);

		// --------------

		p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

		if (grid1 == null) {
			setDataGridPanel(UpdateType.FULL);
		} else {

			RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
			RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);

			setDataGridPanelByGrid(grid1, UpdateType.FULL);

		}

	}

	@Override
	public void reDrawPanel(final CompositeContext context) {
		reDrawPanelExt(context, null);
	}

	/**
	 * Расширенная ф-ция reDrawPanel. Используется в рабочем режиме и для тестов
	 * 
	 * @param context
	 *            CompositeContext
	 * @param grid1
	 *            Grid
	 */
	public void reDrawPanelExt(final CompositeContext context, final Grid grid1) {

		setContext(context);
		// --------------

		if (isFirstLoading()) {
			localContext = null;

			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

			if (grid1 == null) {
				setDataGridPanel(UpdateType.FULL);
			} else {
				RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
				RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);
				setDataGridPanelByGrid(grid1, UpdateType.FULL);
			}

		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				hpHeader.clear();
				hpHeader.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

				hpToolbar.setVisible(false);
				dg.setVisible(false);
				hpFooter.setVisible(false);
			}
			if (grid1 == null) {
				setDataGridPanel(UpdateType.UPDATE_BY_REDRAWGRID);
			} else {
				RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
				RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);
				setDataGridPanelByGrid(grid1, UpdateType.UPDATE_BY_REDRAWGRID);
			}
		}

	}

	private void resetSelection() {
		localContext.getSelectedRecordIds().clear();
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		localContext.getSelectedRecordIds().clear();

		List<Record> records = dg.getSelection().getSelectedRecords();
		if (records != null) {
			for (Record rec : records) {
				localContext.getSelectedRecordIds().add(rec.getId());
			}
		}
	}

	private void saveCurrentClickSelection(final DataCell cell) {
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);

		if (cell != null) {
			localContext.setCurrentRecordId(cell.getRecord().getId());
			localContext.setCurrentColumnId(cell.getColumn().getId());
		}
	}

	private void setDataGridPanel(final UpdateType ut) {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGrid(getDetailedContext(), getElementInfo(), new GWTServiceCallback<Grid>(
				"при получении данных таблицы с сервера") {

			@Override
			public void onSuccess(final Grid grid1) {
				setDataGridPanelByGrid(grid1, ut);
			}
		});

	}

	private void setDataGridPanelByGrid(final Grid grid1, final UpdateType ut) {

		grid = grid1;

		bListenersExit = true;

		beforeUpdateGrid();

		switch (ut) {
		case FULL:
			updateGridFull();
			break;
		case RECORDSET_BY_UPDATERECORDSET:
			updateGridRecordsetByUpdateRecordset();
			break;
		case RECORDSET_BY_SHOWDATA:
			updateGridRecordsetByShowData();
			break;
		case UPDATE_BY_REDRAWGRID:
			updateGridRedrawGrid();
			break;
		default:
			throw new Error("Неизвестный тип UpdateType");
		}

		afterUpdateGrid(ut);

		setupTimer();

		setFirstLoading(false);

		p.setHeight(PROC100);

		bListenersExit = false;

	}

	private void setupTimer() {
		if (getElementInfo().getRefreshByTimer()) {
			Timer timer = getTimer();
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer() {

				@Override
				public void run() {
					refreshPanel();
				}

			};
			final int n1000 = 1000;
			timer.schedule(getElementInfo().getRefreshInterval() * n1000);
		}
	}

	private void beforeUpdateGrid() {
		// Header и Footer - считаем статическими элементами, не меняющимися при
		// изменении контекста грида
		hpHeader.clear();
		HTML header = new HTML();
		if (!grid.getHeader().isEmpty()) {
			strHeader = grid.getHeader();
		}
		header.setHTML(strHeader);
		hpHeader.add(header);

		hpFooter.clear();
		HTML footer = new HTML();
		if (!grid.getFooter().isEmpty()) {
			strFooter = grid.getFooter();
		}
		footer.setHTML(strFooter);
		hpFooter.add(footer);

		settingsDataGrid.assign(grid.getUISettings());
		// все настройки - в т.ч. по умолчанию - устанавливаются сервером
	}

	private void updateGridFull() {

		// -------------------------
		dg = new DataGrid(settingsDataGrid);

		// -------------------------

		p.setSize(PROC100, PROC100);

		hpHeader.setSize(PROC100, PROC100);
		hpFooter.setSize(PROC100, PROC100);
		// dg.setSize(PROC100, PROC100);
		hpToolbar.setHeight(PROC100);
		dg.setWidth("95%");

		hpToolbar.setSpacing(1);
		if (grid.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE);
			exportToExcelCurrentPage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(GridToExcelExportType.CURRENTPAGE);
				}
			});
			hpToolbar.add(exportToExcelCurrentPage);
		}
		if (grid.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_ALL);
			exportToExcelAll.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(GridToExcelExportType.ALL);
				}
			});
			hpToolbar.add(exportToExcelAll);
		}
		if (grid.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(Constants.GRID_CAPTION_COPY_TO_CLIPBOARD);
			copyToClipboard.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					copyToClipboard();
				}
			});
			hpToolbar.add(copyToClipboard);
		}

		p.clear();
		p.add(hpHeader);
		p.add(hpToolbar);
		p.add(dg);
		p.add(hpFooter);

		cs = grid.getDataSet().getColumnSet();

		dg.addDataGridListener(new GridListener());
		dg.getSelection().addListener(new SelectionListener());
		dg.addDataClickHandler(new DataClickHandler("DataClickHandler1"));

		if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
				&& (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {
			dg.showData(grid.getDataSet());
		} else {
			hpToolbar.setVisible(false);
			dg.setVisible(false);
		}

	}

	private void updateGridRecordsetByUpdateRecordset() {
		dg.updateRecordSet(grid.getDataSet().getRecordSet());
	}

	private void updateGridRecordsetByShowData() {
		grid.getDataSet().setColumnSet(cs);
		dg.showData(grid.getDataSet());
	}

	private void updateGridRedrawGrid() {
		cs = grid.getDataSet().getColumnSet();

		if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
				&& (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {

			hpToolbar.setVisible(true);
			dg.setVisible(true);
			dg.showData(grid.getDataSet());
			hpFooter.setVisible(true);

		} else {
			hpToolbar.setVisible(false);
			dg.setVisible(false);
			hpFooter.setVisible(false);
		}

		hpFooter.setVisible(true);
	}

	/**
	 * Локальный класс для работы с ячейкой грида в Showcase.
	 * 
	 * @author den
	 * 
	 */
	class Cell {
		private String recId;
		private String colId;
	}

	/**
	 * Получает информацию о сохраненном выделении в гриде, при этом user
	 * settings имеет приоритет над данными из БД.
	 * 
	 * @return
	 */
	private Cell getStoredRecordId() {
		Cell cell = new Cell();
		if ((localContext != null) && (localContext.getCurrentRecordId() != null)) {
			cell.recId = localContext.getCurrentRecordId();
			cell.colId = localContext.getCurrentColumnId();
		} else {
			cell.recId =
				grid.getAutoSelectRecord() != null ? grid.getAutoSelectRecord().getId() : null;
			cell.colId =
				grid.getAutoSelectColumn() != null ? grid.getAutoSelectColumn().getId() : null;
		}
		return cell;
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */
	private void afterUpdateGrid(final UpdateType ut) {
		Cell selected = getStoredRecordId();
		boolean selectionSaved =
			(localContext != null) && (localContext.getCurrentRecordId() != null);

		if (grid.getDataSet().getRecordSet().getPagesTotal() > 0) {

			if (grid.getUISettings().isSelectOnlyRecords()) {
				if (selected.recId != null) {
					dg.getClickSelection().setClickedRecordById(selected.recId);
				}
			} else {
				if ((selected.recId != null) && (selected.colId != null)) {
					dg.getSelection().setSelectedCellById(selected.recId, selected.colId);
				}
			}

			if (localContext != null) {
				dg.getSelection().setSelectedRecordsById(localContext.getSelectedRecordIds());
			}
		}

		switch (ut) {
		case FULL:
			resetGridSettingsToCurrent();
			runAction(grid.getActionForDependentElements());
			break;
		case UPDATE_BY_REDRAWGRID:
			resetGridSettingsToCurrent();
			if (!selectionSaved) {
				runAction(grid.getActionForDependentElements());
			}
			break;
		default:
			processClick(selected.recId, selected.colId, InteractionType.SINGLE_CLICK);
		}
	}

	private void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, grid);
			ActionExecuter.execAction();
		}
	}

	private void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setPageNumber(grid.getDataSet().getRecordSet().getPageNumber());
		localContext.setPageSize(grid.getDataSet().getRecordSet().getPageSize());
		saveCurrentCheckBoxSelection();
		DataCell cell = dg.getSelection().getSelectedCell();
		if (cell != null) {
			saveCurrentClickSelection(cell);
		} else {
			// если выделена не ячейка, а запись целиком
			if (dg.isVisible() && (dg.getClickSelection().getClickedRecord() != null)) {
				localContext.setCurrentRecordId(dg.getClickSelection().getClickedRecord().getId());
			}
		}
	}

	/**
	 * Экспорт в Excel.
	 * 
	 * @param exportType
	 *            GridToExcelExportType
	 */
	public void exportToExcel(final GridToExcelExportType exportType) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			SerializationStreamFactory ssf = dh.getObjectSerializer();
			dh.addStdPostParamsToBody(getDetailedContext(), getElementInfo());
			dh.addParam(cs.getClass().getName(), cs.toParamForHttpPost(ssf));

			dh.submit();

		} catch (SerializationException e) {
			MessageBox
					.showSimpleMessage(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL, e.getMessage());
		}
	}

	/**
	 * Передача в буфер обмена.
	 * 
	 * @return ClipboardDialog
	 * 
	 */
	public ClipboardDialog copyToClipboard() {
		StringBuilder b = new StringBuilder();
		List<Column> columns = cs.getVisibleColumnsByIndex();

		String d = "";
		for (Column c : columns) {
			b.append(d).append(c.getCaption());
			d = "\t";
		}
		b.append("\n");

		if (dg.getSelection().hasSelectedRecords()) {
			for (Record r : dg.getSelection().getSelectedRecords()) {
				d = "";
				for (Column c : columns) {
					b.append(d).append(r.getValues().get(c.getId()));
					d = "\t";
				}
				b.append("\n");
			}
		} else {
			for (Record r : grid.getDataSet().getRecordSet().getRecords()) {
				d = "";
				for (Column c : columns) {
					b.append(d).append(r.getValues().get(c.getId()));
					d = "\t";
				}
				b.append("\n");
			}
		}

		ClipboardDialog cd = new ClipboardDialog(b.toString());
		cd.center();
		return cd;
	}

	// -------------------------------------------------------

	/**
	 * DataClickHandler.
	 */
	private final class DataClickHandler implements GridClickHandler<DataCell> {

		/**
		 * label.
		 */
		@SuppressWarnings("unused")
		private final String label;

		private DataClickHandler(final String label1) {
			this.label = label1;
		}

		@Override
		public void onClick(final GridClickEvent<DataCell> event) {
			// log("data click (" + label + "): " + event.getClickType() +
			// ", record="
			// + event.getTarget().getRecord().getId() + ", column="
			// + event.getTarget().getColumn().getId());

			// event.preventDefault();

			if (bListenersExit) {
				return;
			}

			saveCurrentClickSelection(event.getTarget());

			InteractionType interactionType;
			switch (event.getClickType()) {
			case SINGLE:
				interactionType = InteractionType.SINGLE_CLICK;
				break;
			case DOUBLE:
				interactionType = InteractionType.DOUBLE_CLICK;
				break;
			case RIGHT:
				interactionType = InteractionType.RIGHT_CLICK;
				break;
			case MIDDLE:
				interactionType = InteractionType.MIDDLE_CLICK;
				break;
			default:
				interactionType = InteractionType.SINGLE_CLICK;
				break;
			}

			if (event.isClickFromAdditionalButton()) {
				event.preventDefault();
				if (event.getTarget().getColumn().getValueType() == GridValueType.DOWNLOAD) {
					processFileDownload(event.getTarget().getRecord(), event.getTarget()
							.getColumn());
				}
			} else {
				processClick(event.getTarget().getRecord().getId(), event.getTarget().getColumn()
						.getId(), interactionType);
			}
		}
	}

	private void processClick(final String rowId, final String colId,
			final InteractionType interactionType) {
		Action ac = null;

		List<GridEvent> events =
			grid.getEventManager().getEventForCell(rowId, colId, interactionType);

		for (GridEvent ev : events) {
			ac = ev.getAction();
			runAction(ac);
		}
	}

	private void processFileDownload(final Record rec, final Column col) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

		try {
			dh.addParam("linkId", col.getLinkId());
			dh.addStdPostParamsToBody(getContext(), getElementInfo());
			dh.addParam("recordId", rec.getId());

			dh.submit();
		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD,
					e.getMessage());
		}
	}

	/**
	 * SelectionListener.
	 */
	private class SelectionListener implements DataSelectionListener {
		@Override
		public void selectedRecordsChanged(final DataSelection selection) {

			// log("selectedRecordsChanged: " +
			// selection.hasSelectedRecords() + ", "
			// + recordsToString(selection.getSelectedRecords()));

			if (bListenersExit) {
				return;
			}

			if (selectionTimer != null) {
				selectionTimer.cancel();
			}

			selectionTimer = new Timer() {
				@Override
				public void run() {
					processSelectionRecords();
				}
			};
			selectionTimer.schedule(Constants.GRID_SELECTION_DELAY);

			saveCurrentCheckBoxSelection();
		}

		@Override
		public void selectedCellChanged(final DataSelection selection) {
			// log("selectedCellChanged: " + (selection.isCellSelected()
			// ? "record=" + selection.getSelectedCell().getRecord().getId() +
			// ", column=" + selection.getSelectedCell().getColumn().getId()
			// : "not selected"));
		}
	}

	private void processSelectionRecords() {

		if (bListenersExit) {
			return;
		}

		Action ac =
			grid.getEventManager().getSelectionActionForDependentElements(dg.getSelection());

		runAction(ac);

	}

	/**
	 * GridListener.
	 */
	private class GridListener implements DataGridListener {
		@Override
		public void columnWidthChanged(final Column column) {
			// log("columnWidthChanged, column=" + column.getId() + ", width=" +
			// column.getWidth());

			if (bListenersExit) {
				return;
			}

			cs.getColumns().get(column.getIndex()).setWidth(column.getWidth());

			// log("columnsLayoutChanged");
			// StringBuilder b = new StringBuilder();
			// for (Column c : cs.getColumns()) {
			// b.append(c.getId()).append(": ").append(c.getWidth()).append(" ");
			// }
			// log("(columns width: " + b.toString() + ")");

		}

		@Override
		public void sortingChanged(final List<Column> columns) {

			// StringBuilder b = new StringBuilder();
			// for (Column c : columns) {
			// b.append(c.getId()).append("[").append(c.getSorting()).append("] ");
			// }
			// log("sortingChanged, columns: " + b.toString());

			if (bListenersExit) {
				return;
			}

			localContext.setPageNumber(1);
			localContext.setSortedColumns(columns);
			resetSelection();
			setDataGridPanel(UpdateType.RECORDSET_BY_UPDATERECORDSET);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void pageNumberChanged(final int newPageNumber) {
			// log("pageNumberChanged: " + newPageNumber);
			// rs.setPageNumber(newPageNumber);
			// r1.setValue("aa", "page no " + newPageNumber);
			// dg.updateRecordSet(rs);

			if (bListenersExit) {
				return;
			}

			localContext.setPageNumber(newPageNumber);
			resetSelection();
			setDataGridPanel(UpdateType.RECORDSET_BY_UPDATERECORDSET);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void pageSizeChanged(final int newItemsPerPage) {
			// log("pageSizeChanged: " + newItemsPerPage);
			// rs.setPageSize(newItemsPerPage);
			// r1.setValue("cc", "pagesize is " + newItemsPerPage);
			// dg.updateRecordSet(rs);

			if (bListenersExit) {
				return;
			}

			localContext.setPageNumber(1);
			localContext.setPageSize(newItemsPerPage);
			resetSelection();
			setDataGridPanel(UpdateType.RECORDSET_BY_SHOWDATA);
			// dg.updateRecordSet(grid.getDataSet().getRecordSet());
		}

		@Override
		public void columnsLayoutChanged() {
			// log("columnsLayoutChanged");
			// StringBuilder b = new StringBuilder();
			// for (Column c : cs.getColumns()) {
			// b.append(c.getId()).append(": ").append(c.getWidth()).append(" ");
			// }
			// log("(columns width: " + b.toString() + ")");
			// dg.updateColumnSet(cs);

			if (bListenersExit) {
				return;
			}

			bListenersExit = true;
			dg.updateColumnSet(cs);
			bListenersExit = false;
		}

	}

	@Override
	public void prepareSettings(final boolean keepElementSettings) {
		if (!keepElementSettings) {
			localContext = null;
		}
	}

	@Override
	public final void refreshPanel() {
		p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			hpHeader.clear();
			hpHeader.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

			hpToolbar.setVisible(false);
			dg.setVisible(false);
			hpFooter.setVisible(false);
		}
		setDataGridPanel(UpdateType.UPDATE_BY_REDRAWGRID);

	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
		}
		result.apply(getContext());
		return result;
	}

}
