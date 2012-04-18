package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
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
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.event.GridEvent;
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

	private final Button exportToExcelCurrentPage = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	private final Button exportToExcelAll = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));
	private final Button copyToClipboard = new Button("",
			IconHelper.create(Constants.GRID_IMAGE_COPY_TO_CLIPBOARD));

	private final DataGridSettings settingsDataGrid = new DataGridSettings();
	private ContentPanel cpGrid = null;
	private EditorGrid<ExtGridData> grid = null;
	private GridSelectionModel<ExtGridData> selectionModel = null;
	private ColumnSet cs = null;
	private Timer selectionTimer = null;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
	private ExtGridMetadata gridMetadata = null;
	private ExtGridExtradata gridExtradata = null;
	private boolean isFirstLoading = true;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean aIsFirstLoading) {
		isFirstLoading = aIsFirstLoading;
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
		return gridMetadata;
	}

	/**
	 * Конструктор класса GridPanel без начального показа грида.
	 */
	public ExtGridPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
	}

	/**
	 * Конструктор класса GridPanel.
	 */
	public ExtGridPanel(final CompositeContext context, final DataPanelElementInfo element,
			final Grid grid1) {
		setContext(context);
		setElementInfo(element);
		setFirstLoading(true);

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

		if (isFirstLoading()) {
			localContext = null;

			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

			setDataGridPanel();
		} else {
			refreshPanel();
		}
	}

	@Override
	public final void refreshPanel() {
		p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			p.clear();
			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		}
		setDataGridPanel();
	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getExtGridMetadata(getDetailedContext(), getElementInfo(),
				new GWTServiceCallback<ExtGridMetadata>("при получении данных таблицы с сервера") {

					@Override
					public void onSuccess(final ExtGridMetadata aGridMetadata) {
						setDataGridPanelByGrid(aGridMetadata);
					}
				});
	}

	private void setDataGridPanelByGrid(final ExtGridMetadata aGridMetadata) {
		gridMetadata = aGridMetadata;

		beforeUpdateGrid();

		updateGridFull();

		setupTimer();

		p.setHeight(PROC100);
	}

	@Override
	protected void internalResetLocalContext() {
		localContext = null;
	}

	private void beforeUpdateGrid() {
		resetLocalContext();
		// все настройки - в т.ч. по умолчанию - устанавливаются сервером
		settingsDataGrid.assign(gridMetadata.getUISettings());
	}

	// CHECKSTYLE:OFF
	private void updateGridFull() {

		RpcProxy<PagingLoadResult<ExtGridData>> proxy =
			new RpcProxy<PagingLoadResult<ExtGridData>>() {
				@Override
				public void load(final Object loadConfig,
						final AsyncCallback<PagingLoadResult<ExtGridData>> callback) {

					PagingLoadConfig plc = (PagingLoadConfig) loadConfig;

					// --------------

					GridContext gridContext = getDetailedContext();
					gridContext.getLiveInfo().setOffset(plc.getOffset());
					gridContext.getLiveInfo().setLimit(plc.getLimit());

					// --------------

					ColumnConfig colConfig =
						grid.getColumnModel().getColumnById(plc.getSortField());
					if (colConfig != null) {
						Column colOriginal = null;
						for (Column c : gridMetadata.getOriginalColumnSet().getColumns()) {
							if (colConfig.getHeader().equals(c.getId())) {
								colOriginal = c;
								break;
							}
						}
						if (colOriginal != null) {
							List<Column> sortOriginalCols = new ArrayList<Column>();

							colOriginal.setSorting(Sorting.valueOf(plc.getSortDir().name()));

							sortOriginalCols.add(colOriginal);

							gridContext.setSortedColumns(sortOriginalCols);
						}
					}

					// --------------

					dataService.getExtGridData(gridContext, getElementInfo(), callback);

				}
			};

		final PagingLoader<PagingLoadResult<ModelData>> loader =
			new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
		loader.setRemoteSort(true);
		loader.addListener(Loader.Load, new Listener<LoadEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(LoadEvent be) {
				gridExtradata =
					((ExtGridPagingLoadResult<ExtGridData>) be.getData()).getExtGridExtradata();

				resetSelection();
			}
		});

		final ListStore<ExtGridData> store = new ListStore<ExtGridData>(loader);
		// store.setMonitorChanges(true);

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectionModel = new CheckBoxSelectionModel<ExtGridData>();
			columns.add(((CheckBoxSelectionModel<ExtGridData>) selectionModel).getColumn());
		} else {
			selectionModel = new CellSelectionModel<ExtGridData>();
		}

		for (final ExtGridColumnConfig egcc : gridMetadata.getColumns()) {
			ColumnConfig column =
				new ColumnConfig(egcc.getId(), egcc.getCaption(), egcc.getWidth());

			column.setToolTip(column.getHeader());

			if (egcc.getDateTimeFormat() != null) {
				column.setDateTimeFormat(DateTimeFormat.getFormat(egcc.getDateTimeFormat()));
			}

			column.setAlignment(egcc.getHorizontalAlignment());

			if (egcc.getValueType() == GridValueType.DOWNLOAD) {
				column.setRenderer(new GridCellRenderer<ExtGridData>() {
					@Override
					public Object render(final ExtGridData model, String property,
							ColumnData config, int rowIndex, int colIndex,
							ListStore<ExtGridData> store,
							com.extjs.gxt.ui.client.widget.grid.Grid<ExtGridData> grid) {
						com.google.gwt.user.client.ui.Grid g =
							new com.google.gwt.user.client.ui.Grid(1, 2);
						g.setWidth("100%");

						g.setWidget(0, 0, new HTML("<nowrap>" + (String) model.get(property)
								+ "</nowrap>"));

						Button bt =
							new Button("", IconHelper.create(settingsDataGrid
									.getUrlImageFileDownload()));
						bt.setTitle("Загрузить файл с сервера");

						bt.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
							@Override
							public void componentSelected(ButtonEvent ce) {
								processFileDownload(model.getId(), egcc.getLinkId());

							}
						});

						g.setWidget(0, 1, bt);

						g.getCellFormatter().setWidth(0, 1, "30px");
						g.getCellFormatter().setHorizontalAlignment(0, 1,
								HasHorizontalAlignment.ALIGN_CENTER);
						g.getCellFormatter().setVerticalAlignment(0, 1,
								HasVerticalAlignment.ALIGN_MIDDLE);

						return g;
					}
				});
			}

			columns.add(column);
		}

		ColumnModel cm = new ColumnModel(columns);

		grid = new EditorGrid<ExtGridData>(store, cm);

		grid.setSelectionModel(selectionModel);
		// selectionModel.bindGrid(grid);

		grid.setColumnReordering(true);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.getAriaSupport().setLabelledBy(grid.getId() + "-label");
		// grid.setStateId("pagingGridExample");
		// grid.setStateful(true);

		// ---------------------------

		grid.addListener(Events.CellClick, new Listener<GridEvent<ExtGridData>>() {
			@Override
			public void handleEvent(GridEvent<ExtGridData> be) {
				handleClick(be, InteractionType.SINGLE_CLICK);
			}
		});

		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<ExtGridData>>() {
			@Override
			public void handleEvent(GridEvent<ExtGridData> be) {
				handleClick(be, InteractionType.DOUBLE_CLICK);
			}
		});

		// ---------------------------

		LiveGridView liveView = new LiveGridView();

		liveView.addListener(Events.LiveGridViewUpdate, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				afterUpdateGrid();
			}
		});

		liveView.setRowHeight(gridMetadata.getUISettings().getRowHeight());
		// liveView.setPrefetchFactor(0);
		liveView.setCacheSize(gridMetadata.getLiveInfo().getLimit());
		grid.setView(liveView);

		// ---------------------------

		// Стили для записей
		GridViewConfig gvc = new GridViewConfig() {
			@Override
			public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
				ExtGridData egd = (ExtGridData) model;
				String rowstyle = egd.getRowStyle();
				return rowstyle;
			}
		};
		grid.getView().setViewConfig(gvc);

		// ---------------------------

		ToolBar buttonBar = new ToolBar();
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE);
			exportToExcelCurrentPage
					.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							exportToExcel(GridToExcelExportType.CURRENTPAGE);
						}
					});
			buttonBar.add(exportToExcelCurrentPage);
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
			buttonBar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(Constants.GRID_CAPTION_COPY_TO_CLIPBOARD);
			copyToClipboard
					.addSelectionListener(new com.extjs.gxt.ui.client.event.SelectionListener<ButtonEvent>() {
						@Override
						public void componentSelected(ButtonEvent ce) {
							// cpGrid.setAutoWidth(true);
							copyToClipboard();
						}
					});
			buttonBar.add(copyToClipboard);
		}

		// ------------
		ToolBar liveBar = new ToolBar();
		LabelToolItem footer = new LabelToolItem(gridMetadata.getFooter());
		liveBar.add(footer);
		liveBar.add(new FillToolItem());
		LiveToolItem item = new LiveToolItem();
		item.bindGrid(grid);
		liveBar.add(item);
		// ------------

		// ------------------------------------------------------------------------------

		cpGrid = new ContentPanel();
		// ------------
		Draggable d = new Draggable(cpGrid, cpGrid.getHeader());
		d.setUseProxy(true);
		Resizable r = new Resizable(cpGrid);
		r.setDynamic(false);
		// ------------
		cpGrid.setFrame(true);
		cpGrid.setCollapsible(true);
		cpGrid.setAnimCollapse(false);
		cpGrid.setLayout(new FitLayout());
		// ------------

		cpGrid.setHeading(gridMetadata.getHeader());

		cpGrid.setTopComponent(buttonBar);

		grid.setWidth(PROC100);
		grid.setHeight(gridMetadata.getUISettings().getGridHeight());

		// grid.setAutoWidth(true);
		// grid.setAutoHeight(true);
		cpGrid.add(grid);
		cpGrid.add(liveBar);

		// ------------------------------------------------------------------------------

		p.setSize(PROC100, PROC100);
		p.clear();
		p.add(cpGrid);

		// ------------------------------------------------------------------------------

		cs = gridMetadata.getOriginalColumnSet();

	}

	// CHECKSTYLE:ON

	private void
			handleClick(final GridEvent<ExtGridData> be, final InteractionType interactionType) {

		saveCurrentClickSelection(be);

		if (!(selectionModel instanceof CellSelectionModel)) {
			selectedRecordsChanged();
		}

		processClick(be.getModel().getId(), grid.getColumnModel().getColumn(be.getColIndex())
				.getHeader(), interactionType);
	}

	private void processClick(final String rowId, final String colId,
			final InteractionType interactionType) {
		Action ac = null;

		List<ru.curs.showcase.app.api.grid.GridEvent> events =
			gridExtradata.getEventManager().getEventForCell(rowId, colId, interactionType);

		for (ru.curs.showcase.app.api.grid.GridEvent ev : events) {
			ac = ev.getAction();
			runAction(ac);
		}
	}

	private void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, gridMetadata);
			ActionExecuter.execAction();
		}
	}

	private void selectedRecordsChanged() {
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

	private void processSelectionRecords() {
		List<String> selectedRecordIds = new ArrayList<String>();
		for (ExtGridData egd : selectionModel.getSelectedItems()) {
			selectedRecordIds.add(egd.getId());
		}

		Action ac =
			gridExtradata.getEventManager().getSelectionActionForDependentElements(
					selectedRecordIds);

		runAction(ac);
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */
	private void afterUpdateGrid() {
		if (!isFirstLoading) {
			return;
		}

		if (grid.getStore().getModels().size() == 0) {
			return;
		}

		Cell selected = getStoredRecordId();

		if (selectionModel instanceof CellSelectionModel) {
			final int row = 1;
			final int col = 2;

			// for (ExtGridData egd : grid.getStore().getModels()) {
			// if (egd.getId().equals(selected.recId)) {
			//
			// // grid.getr . getStore().getRecord(egd).
			//
			// grid.getView().getRow(egd).get.getStore().getRecord(egd).getModel().
			//
			// selectionModel.select(egd, false);
			// break;
			// }
			// }

			((CellSelectionModel<ExtGridData>) selectionModel).selectCell(row, col);
		} else {
			for (ExtGridData egd : grid.getStore().getModels()) {
				if (egd.getId().equals(selected.recId)) {
					selectionModel.select(egd, false);
					break;
				}
			}
		}

		// if (localContext != null) {
		// dg.getSelection().setSelectedRecordsById(localContext.getSelectedRecordIds());
		// }

		resetGridSettingsToCurrent(); // Это вместо нижнего switch

		runAction(gridExtradata.getActionForDependentElements());

		setFirstLoading(false);
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

	private void resetSelection() {
		if (localContext == null) {
			return;

		}
		localContext.getSelectedRecordIds().clear();
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		localContext.getSelectedRecordIds().clear();

		for (ExtGridData egd : selectionModel.getSelectedItems()) {
			localContext.getSelectedRecordIds().add(egd.getId());
		}
	}

	private void saveCurrentClickSelection(final GridEvent<ExtGridData> be) {
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);

		localContext.setCurrentRecordId(be.getModel().getId());
		localContext.setCurrentColumnId(grid.getColumnModel().getColumn(be.getColIndex())
				.getHeader());
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
				gridMetadata.getAutoSelectRecord() != null ? gridMetadata.getAutoSelectRecord()
						.getId() : null;
			cell.colId =
				gridMetadata.getAutoSelectColumn() != null ? gridMetadata.getAutoSelectColumn()
						.getCaption() : null;
		}
		return cell;
	}

	private void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);

		saveCurrentCheckBoxSelection();
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

			SerializationStreamFactory ssf = dh.getAddObjectSerializer();
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

		List<ColumnConfig> columns = grid.getColumnModel().getColumns();

		String d = "";
		for (ColumnConfig c : columns) {
			b.append(d).append(c.getHeader());
			d = "\t";
		}
		b.append("\n");

		List<ExtGridData> models;
		if (selectionModel.getSelectedItems().size() > 0) {
			models = selectionModel.getSelectedItems();
		} else {
			models = grid.getStore().getModels();
		}

		for (ExtGridData egd : models) {
			d = "";
			for (ColumnConfig c : columns) {
				b.append(d).append(egd.get(c.getId()));
				d = "\t";
			}
			b.append("\n");
		}

		ClipboardDialog cd = new ClipboardDialog(b.toString());
		cd.center();
		return cd;
	}

	private void processFileDownload(final String recId, final String colLinkId) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

		try {
			dh.addParam("linkId", colLinkId);
			dh.addStdPostParamsToBody(getContext(), getElementInfo());
			dh.addParam("recordId", recId);

			dh.submit();
		} catch (SerializationException e) {
			ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
					Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD, e.getMessage());
		}
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		result.assignNullValues(getContext());
		return result;
	}

}
