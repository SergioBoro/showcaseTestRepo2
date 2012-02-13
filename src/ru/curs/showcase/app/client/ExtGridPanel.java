package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.DataGridListener;
import ru.curs.gwt.datagrid.event.*;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.gwt.datagrid.selection.*;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.fx.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с гридом.
 */
public class ExtGridPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";

	private final VerticalPanel p = new VerticalPanel();
	private final HorizontalPanel hpHeader = new HorizontalPanel();
	private final ContentPanel cp = new ContentPanel();
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	private final Button exportToExcelCurrentPage = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	private final Button exportToExcelAll = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));
	private final Button copyToClipboard = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_COPY_TO_CLIPBOARD));

	private final DataGridSettings settingsDataGrid = new DataGridSettings();
	// private DataGrid dg = null;
	private final ColumnSet cs = null;
	private Timer selectionTimer = null;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
	private ExtGridMetadata gridMetadata = null;

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
		return gridMetadata;
	}

	/**
	 * Конструктор класса GridPanel без начального показа грида.
	 */
	public ExtGridPanel(final DataPanelElementInfo element) {

		setElementInfo(element);
		setContext(null);
		setFirstLoading(true);

		// --------------

	}

	/**
	 * Конструктор класса GridPanel.
	 */
	public ExtGridPanel(final CompositeContext context, final DataPanelElementInfo element,
			final Grid grid1) {

		this.setContext(context);
		this.setElementInfo(element);
		setFirstLoading(true);

		// --------------

		p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

		setDataGridPanel();

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

			setDataGridPanel();

		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				hpHeader.clear();
				hpHeader.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

				// dg.setVisible(false);
				hpFooter.setVisible(false);
			}

			setDataGridPanel();
		}

	}

	private void resetSelection() {
		// localContext.getSelectedRecordIds().clear();
		// localContext.setCurrentColumnId(null);
		// localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		// localContext.getSelectedRecordIds().clear();
		//
		// List<Record> records = dg.getSelection().getSelectedRecords();
		// if (records != null) {
		// for (Record rec : records) {
		// localContext.getSelectedRecordIds().add(rec.getId());
		// }
		// }
	}

	private void saveCurrentClickSelection(final DataCell cell) {
		// localContext.setCurrentColumnId(null);
		// localContext.setCurrentRecordId(null);
		//
		// if (cell != null) {
		// localContext.setCurrentRecordId(cell.getRecord().getId());
		// localContext.setCurrentColumnId(cell.getColumn().getId());
		// }
	}

	private void setDataGridPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getExtGridMetadata(getDetailedContext(), getElementInfo(),
				new GWTServiceCallback<ExtGridMetadata>("при получении данных таблицы с сервера") {

					@Override
					public void onSuccess(final ExtGridMetadata grid1) {
						// MessageBox.showSimpleMessage("Debug", "");

						setDataGridPanelByGrid(grid1);
					}
				});

	}

	private void setDataGridPanelByGrid(final ExtGridMetadata aGridMetadata) {

		gridMetadata = aGridMetadata;

		beforeUpdateGrid();

		updateGridFull(); // вместо нижнего switch
		//
		// switch (ut) {
		// case FULL:
		// updateGridFull();
		// break;
		// case RECORDSET_BY_UPDATERECORDSET:
		// updateGridRecordsetByUpdateRecordset();
		// break;
		// case RECORDSET_BY_SHOWDATA:
		// updateGridRecordsetByShowData();
		// break;
		// case UPDATE_BY_REDRAWGRID:
		// updateGridRedrawGrid();
		// break;
		// default:
		// throw new Error("Неизвестный тип UpdateType");
		// }
		//
		afterUpdateGrid();

		setupTimer();

		setFirstLoading(false);

		p.setHeight(PROC100);

	}

	private void setupTimer() {
		// if (getElementInfo().getRefreshByTimer()) {
		// Timer timer = getTimer();
		// if (timer != null) {
		// timer.cancel();
		// }
		// timer = new Timer() {
		//
		// @Override
		// public void run() {
		// refreshPanel();
		// }
		//
		// };
		// final int n1000 = 1000;
		// timer.schedule(getElementInfo().getRefreshInterval() * n1000);
		// }
	}

	private void beforeUpdateGrid() {
		// Header и Footer - считаем статическими элементами, не меняющимися при
		// изменении контекста грида
		hpHeader.clear();
		HTML header = new HTML();
		if (!gridMetadata.getHeader().isEmpty()) {
			strHeader = gridMetadata.getHeader();
		}
		header.setHTML(strHeader);
		hpHeader.add(header);

		hpFooter.clear();
		HTML footer = new HTML();
		if (!gridMetadata.getFooter().isEmpty()) {
			strFooter = gridMetadata.getFooter();
		}
		footer.setHTML(strFooter);
		hpFooter.add(footer);

		settingsDataGrid.assign(gridMetadata.getUISettings());
		// все настройки - в т.ч. по умолчанию - устанавливаются сервером
	}

	// CHECKSTYLE:OFF
	private void updateGridFull() {

		// // -------------------------
		// dg = new DataGrid(settingsDataGrid);
		//
		// // -------------------------
		//
		p.setSize(PROC100, PROC100);

		hpHeader.setSize(PROC100, PROC100);
		hpFooter.setSize(PROC100, PROC100);
		// dg.setSize(PROC100, PROC100);
		// dg.setWidth("95%");
		//

		p.clear();
		p.add(hpHeader);
		// p.add(dg);

		// ------------------------------------------------------------------------------

		RpcProxy<PagingLoadResult<ExtGridData>> proxy =
			new RpcProxy<PagingLoadResult<ExtGridData>>() {
				@Override
				public void load(final Object loadConfig,
						final AsyncCallback<PagingLoadResult<ExtGridData>> callback) {

					GridContext gridContext = getDetailedContext();
					gridContext.getLiveInfo().setOffset(
							((PagingLoadConfig) loadConfig).getOffset());
					gridContext.getLiveInfo().setLimit(((PagingLoadConfig) loadConfig).getLimit());

					dataService.getExtGridData(gridContext, getElementInfo(), callback);
				}
			};

		// loader
		final PagingLoader<PagingLoadResult<ModelData>> loader =
			new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(true);

		final ListStore<ExtGridData> store = new ListStore<ExtGridData>(loader);
		// store.setMonitorChanges(true);

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		for (ExtGridColumnConfig egcc : gridMetadata.getColumns()) {
			ColumnConfig column =
				new ColumnConfig(egcc.getId(), egcc.getCaption(), egcc.getWidth());

			column.setToolTip(column.getHeader());

			if (egcc.getDateTimeFormat() != null) {
				column.setDateTimeFormat(DateTimeFormat.getFormat(egcc.getDateTimeFormat()));
			}

			columns.add(column);
		}

		ColumnModel cm = new ColumnModel(columns);

		final EditorGrid<ExtGridData> grid = new EditorGrid<ExtGridData>(store, cm);
		grid.setColumnReordering(true);
		// grid.setStateId("pagingGridExample");
		// grid.setStateful(true);

		// ---------------------------

		grid.setLoadMask(true);
		grid.setBorders(true);
		// grid.setAutoExpandColumn("forum");

		LiveGridView liveView = new LiveGridView();
		liveView.setEmptyText("No rows available on the server.");
		// liveView.setRowHeight(32);
		liveView.setCacheSize(gridMetadata.getLiveInfo().getLimit());
		grid.setView(liveView);

		// ------------
		final GridSelectionModel<ExtGridData> selectionModel =
			new GridSelectionModel<ExtGridData>();
		selectionModel.bindGrid(grid);
		// ------------

		// ------------
		Draggable d = new Draggable(cp, cp.getHeader());
		d.setUseProxy(true);
		Resizable r = new Resizable(cp);
		r.setDynamic(false);
		// ------------

		cp.setFrame(true);
		cp.setCollapsible(true);
		cp.setAnimCollapse(false);
		cp.setHeading("Live Grid");
		// cp.setHeaderVisible(false);
		cp.setLayout(new FitLayout());
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");
		cp.add(grid);
		cp.setSize(1200, 500);
		// cp.setHeight(500);

		// ------------
		ToolBar toolBar = new ToolBar();
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE);
			exportToExcelCurrentPage
					.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							exportToExcel(GridToExcelExportType.CURRENTPAGE);
						}
					});
			toolBar.add(exportToExcelCurrentPage);
		}
		if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_ALL);
			exportToExcelAll
					.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							exportToExcel(GridToExcelExportType.ALL);
						}
					});
			toolBar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(Constants.GRID_CAPTION_COPY_TO_CLIPBOARD);
			copyToClipboard
					.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							copyToClipboard();
						}
					});
			toolBar.add(copyToClipboard);
		}
		cp.setTopComponent(toolBar);

		// ------------
		ToolBar toolBar2 = new ToolBar();
		toolBar2.add(new FillToolItem());
		LiveToolItem item = new LiveToolItem();
		item.bindGrid(grid);
		toolBar2.add(item);
		cp.setBottomComponent(toolBar2);
		// ------------

		// ------------------------------------------------------------------------------

		p.add(cp);
		p.add(hpFooter);
		//
		// cs = grid.getDataSet().getColumnSet();
		//
		// dg.addDataGridListener(new GridListener());
		// dg.getSelection().addListener(new SelectionListener());
		// dg.addDataClickHandler(new DataClickHandler("DataClickHandler1"));
		//
		// if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
		// && (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {
		// dg.showData(grid.getDataSet());
		// } else {
		// hpToolbar.setVisible(false);
		// dg.setVisible(false);
		// }

	}

	// CHECKSTYLE:ON

	@SuppressWarnings("unused")
	private void updateGridRecordsetByUpdateRecordset() {
		// dg.updateRecordSet(grid.getDataSet().getRecordSet());
	}

	@SuppressWarnings("unused")
	private void updateGridRecordsetByShowData() {
		// grid.getDataSet().setColumnSet(cs);
		// dg.showData(grid.getDataSet());
	}

	@SuppressWarnings("unused")
	private void updateGridRedrawGrid() {
		// cs = grid.getDataSet().getColumnSet();
		//
		// if ((grid.getDataSet().getColumnSet().getColumns().size() > 0)
		// && (grid.getDataSet().getRecordSet().getRecordsCount() > 0)) {
		//
		// hpToolbar.setVisible(true);
		// dg.setVisible(true);
		// dg.showData(grid.getDataSet());
		// hpFooter.setVisible(true);
		//
		// } else {
		// hpToolbar.setVisible(false);
		// dg.setVisible(false);
		// hpFooter.setVisible(false);
		// }
		//
		// hpFooter.setVisible(true);
	}

	/**
	 * Локальный класс для работы с ячейкой грида в Showcase.
	 * 
	 * @author den
	 * 
	 */
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	private Cell getStoredRecordId() {
		Cell cell = new Cell();
		if ((localContext != null) && (localContext.getCurrentRecordId() != null)) {
			cell.recId = localContext.getCurrentRecordId();
			cell.colId = localContext.getCurrentColumnId();
		} else {
			cell.recId =
				gridMetadata.getAutoSelectRecord() != null ? gridMetadata.getAutoSelectRecord()
						.getId() : null;
			cell.colId =
				gridMetadata.getAutoSelectColumn() != null ? gridMetadata.getAutoSelectColumn()
						.getId() : null;
		}
		return cell;
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */
	private void afterUpdateGrid() {
		// Cell selected = getStoredRecordId();
		// boolean selectionSaved =
		// (localContext != null) && (localContext.getCurrentRecordId() !=
		// null);
		//
		// if (grid.getDataSet().getRecordSet().getPagesTotal() > 0) {
		//
		// if (grid.getUISettings().isSelectOnlyRecords()) {
		// if (selected.recId != null) {
		// dg.getClickSelection().setClickedRecordById(selected.recId);
		// }
		// } else {
		// if ((selected.recId != null) && (selected.colId != null)) {
		// dg.getSelection().setSelectedCellById(selected.recId,
		// selected.colId);
		// }
		// }
		//
		// if (localContext != null) {
		// dg.getSelection().setSelectedRecordsById(localContext.getSelectedRecordIds());
		// }
		// }
		//
		// switch (ut) {
		// case FULL:
		// resetGridSettingsToCurrent();
		// runAction(grid.getActionForDependentElements());
		// break;
		// case UPDATE_BY_REDRAWGRID:
		// resetGridSettingsToCurrent();
		// if (!selectionSaved) {
		// runAction(grid.getActionForDependentElements());
		// }
		// break;
		// default:
		// processClick(selected.recId, selected.colId,
		// InteractionType.SINGLE_CLICK);
		// }
	}

	@SuppressWarnings("unused")
	private void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, gridMetadata);
			ActionExecuter.execAction();
		}
	}

	@SuppressWarnings("unused")
	private void resetGridSettingsToCurrent() {
		// localContext = new GridContext();
		// localContext.setPageNumber(grid.getDataSet().getRecordSet().getPageNumber());
		// localContext.setPageSize(grid.getDataSet().getRecordSet().getPageSize());
		// saveCurrentCheckBoxSelection();
		// DataCell cell = dg.getSelection().getSelectedCell();
		// if (cell != null) {
		// saveCurrentClickSelection(cell);
		// } else {
		// // если выделена не ячейка, а запись целиком
		// if (dg.getClickSelection().getClickedRecord() != null) {
		// localContext.setCurrentRecordId(dg.getClickSelection().getClickedRecord().getId());
		// }
		// }
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

			ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
					Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL, e.getMessage());
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
		// List<Column> columns = cs.getVisibleColumnsByIndex();
		//
		// String d = "";
		// for (Column c : columns) {
		// b.append(d).append(c.getCaption());
		// d = "\t";
		// }
		// b.append("\n");
		//
		// if (dg.getSelection().hasSelectedRecords()) {
		// for (Record r : dg.getSelection().getSelectedRecords()) {
		// d = "";
		// for (Column c : columns) {
		// b.append(d).append(r.getValues().get(c.getId()));
		// d = "\t";
		// }
		// b.append("\n");
		// }
		// } else {
		// for (Record r : grid.getDataSet().getRecordSet().getRecords()) {
		// d = "";
		// for (Column c : columns) {
		// b.append(d).append(r.getValues().get(c.getId()));
		// d = "\t";
		// }
		// b.append("\n");
		// }
		// }

		ClipboardDialog cd = new ClipboardDialog(b.toString());
		cd.center();
		return cd;
	}

	// -------------------------------------------------------

	/**
	 * DataClickHandler.
	 */
	@SuppressWarnings("unused")
	private final class DataClickHandler implements GridClickHandler<DataCell> {

		/**
		 * label.
		 */
		private final String label;

		private DataClickHandler(final String label1) {
			this.label = label1;
		}

		@Override
		public void onClick(final GridClickEvent<DataCell> event) {

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
		// Action ac = null;
		//
		// List<GridEvent> events =
		// grid.getEventManager().getEventForCell(rowId, colId,
		// interactionType);
		//
		// for (GridEvent ev : events) {
		// ac = ev.getAction();
		// runAction(ac);
		// }
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
			ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
					Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD, e.getMessage());
		}
	}

	/**
	 * SelectionListener.
	 */
	@SuppressWarnings("unused")
	private class SelectionListener implements DataSelectionListener {
		@Override
		public void selectedRecordsChanged(final DataSelection selection) {

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
		}
	}

	private void processSelectionRecords() {

		// Action ac =
		// grid.getEventManager().getSelectionActionForDependentElements(dg.getSelection());
		//
		// runAction(ac);

	}

	/**
	 * GridListener.
	 */
	@SuppressWarnings("unused")
	private class GridListener implements DataGridListener {
		@Override
		public void columnWidthChanged(final Column column) {

			cs.getColumns().get(column.getIndex()).setWidth(column.getWidth());

		}

		@Override
		public void sortingChanged(final List<Column> columns) {

			localContext.setPageNumber(1);
			localContext.setSortedColumns(columns);
			resetSelection();
			setDataGridPanel();

		}

		@Override
		public void pageNumberChanged(final int newPageNumber) {

			localContext.setPageNumber(newPageNumber);
			resetSelection();
			setDataGridPanel();

		}

		@Override
		public void pageSizeChanged(final int newItemsPerPage) {
			localContext.setPageNumber(1);
			localContext.setPageSize(newItemsPerPage);
			resetSelection();
			setDataGridPanel();

		}

		@Override
		public void columnsLayoutChanged() {

			// dg.updateColumnSet(cs);

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

			// dg.setVisible(false);
			hpFooter.setVisible(false);
		}
		setDataGridPanel();

	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		}
		result.apply(getContext());
		return result;
	}

}
