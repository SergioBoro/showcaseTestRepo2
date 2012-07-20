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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.*;
import com.sencha.gxt.core.client.resources.*;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.*;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.loader.*;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.*;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.*;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.*;
import com.sencha.gxt.widget.core.client.toolbar.*;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * Класс панели с tree-гридом.
 */
public class TreeGridPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";

	private final VerticalPanel p = new VerticalPanel();
	private final TextButton exportToExcelCurrentPage = new TextButton("",
			IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE),
					16, 16));
	private final TextButton exportToExcelAll = new TextButton("", IconHelper.getImageResource(
			UriUtils.fromSafeConstant(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL), 16, 16));
	private final TextButton copyToClipboard = new TextButton("", IconHelper.getImageResource(
			UriUtils.fromSafeConstant(Constants.GRID_IMAGE_COPY_TO_CLIPBOARD), 16, 16));
	private final DataGridSettings settingsDataGrid = new DataGridSettings();
	private TreeGrid<TreeGridModel> grid = null;
	private GridSelectionModel<TreeGridModel> selectionModel = null;
	private ColumnSet cs = null;
	private com.google.gwt.user.client.Timer selectionTimer = null;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
	private LiveGridMetadata gridMetadata = null;
	private LiveGridExtradata gridExtradataLevel0 = null;
	private boolean isFirstLoading = true;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean aIsFirstLoading) {
		isFirstLoading = aIsFirstLoading;
	}

	private final List<String> expandedIds = new ArrayList<String>();

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
	public TreeGridPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
	}

	/**
	 * Конструктор класса GridPanel.
	 */
	public TreeGridPanel(final CompositeContext context, final DataPanelElementInfo element,
			final Grid grid1) {
		setContext(context);
		setElementInfo(element);
		setFirstLoading(true);

		refreshPanel();
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

		refreshPanel();
	}

	@Override
	public final void refreshPanel() {

		if (isFirstLoading()) {
			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				p.clear();
				p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
			}
		}

		if (isFirstLoading() || isNeedResetLocalContext()) {
			localContext = null;
			setFirstLoading(true);
			setDataGridPanel();
		} else {
			expandedIds.clear();
			List<TreeGridModel> models = grid.getStore().getAll();
			for (TreeGridModel old : models) {
				if (grid.isExpanded(old)) {
					expandedIds.add(old.getId());
				}
			}

			setFirstLoading(false);
			grid.getTreeLoader().load();

		}

	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService
				.getLiveGridMetadata(getDetailedContext(), getElementInfo(),
						new GWTServiceCallback<LiveGridMetadata>(
								"при получении данных таблицы с сервера") {

							@Override
							public void onSuccess(final LiveGridMetadata aGridMetadata) {
								setDataGridPanelByGrid(aGridMetadata);
							}
						});
	}

	private void setDataGridPanelByGrid(final LiveGridMetadata aGridMetadata) {
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

		cs = gridMetadata.getOriginalColumnSet();

		RpcProxy<TreeGridModel, List<TreeGridModel>> proxy =
			new RpcProxy<TreeGridModel, List<TreeGridModel>>() {
				@Override
				public void load(final TreeGridModel loadConfig,
						final AsyncCallback<List<TreeGridModel>> callback) {

					final GridContext gridContext = getDetailedContext();
					gridContext.resetForReturnAllRecords();
					gridContext.setParentId(null);
					if (loadConfig != null) {
						gridContext.setParentId(loadConfig.getId());
					}

					if (!grid.getTreeStore().getSortInfo().isEmpty()) {
						ColumnConfig<TreeGridModel, ?> colConfig =
							grid.getColumnModel().getColumn(
									getColumnIndexByLiveId(grid.getTreeStore().getSortInfo()
											.get(0).getPath()));
						if (colConfig != null) {
							Column colOriginal = null;
							for (Column c : gridMetadata.getOriginalColumnSet().getColumns()) {
								if (colConfig.getHeader().asString().equals(c.getId())) {
									colOriginal = c;
									break;
								}
							}
							if (colOriginal != null) {
								List<Column> sortOriginalCols = new ArrayList<Column>();

								colOriginal.setSorting(Sorting.valueOf(grid.getTreeStore()
										.getSortInfo().get(0).getDirection().name()));

								sortOriginalCols.add(colOriginal);

								gridContext.setSortedColumns(sortOriginalCols);
							}
						}
					}

					dataService.getTreeGridData(gridContext, getElementInfo(),
							new AsyncCallback<List<TreeGridModel>>() {
								@Override
								public void onFailure(Throwable caught) {
									callback.onFailure(caught);
								}

								@Override
								public void onSuccess(List<TreeGridModel> result) {

									if (gridContext.getParentId() != null) {
										String id;
										List<TreeGridModel> models = grid.getStore().getAll();
										for (TreeGridModel res : result) {
											id = res.getId();
											if (id == null) {
												continue;
											}

											for (TreeGridModel old : models) {
												if (id.equals(old.getId())) {
													MessageBox
															.showSimpleMessage(
																	"Загрузка данных",
																	"Загружаемая запись с идентификатором "
																			+ res.getId()
																			+ " уже присутствует в гриде. Записи загружены не будут.");
													return;
												}
											}
										}
									}

									callback.onSuccess(result);

									TreeGridData<TreeGridModel> tgd =
										(TreeGridData<TreeGridModel>) result;

									if (loadConfig == null) {
										gridExtradataLevel0 = tgd.getLiveGridExtradata();
									} else {
										boolean needAdd;
										for (ru.curs.showcase.app.api.grid.GridEvent ev : tgd
												.getLiveGridExtradata().getEventManager()
												.getEvents()) {
											needAdd = true;
											for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridExtradataLevel0
													.getEventManager().getEvents()) {
												if (ev.getId1().equals(evOld.getId1())
														&& ev.getId2().equals(evOld.getId2())
														&& (ev.getInteractionType() == evOld
																.getInteractionType())) {
													needAdd = false;
													break;
												}
											}
											if (needAdd) {
												gridExtradataLevel0.getEventManager().getEvents()
														.add(ev);
											}
										}
									}

									afterUpdateGrid(loadConfig);
								}
							});
				}
			};

		final TreeLoader<TreeGridModel> loader = new TreeLoader<TreeGridModel>(proxy) {
			@Override
			public boolean hasChildren(TreeGridModel parent) {
				return parent.isHasChildren();
			}
		};

		final TreeStore<TreeGridModel> store =
			new TreeStore<TreeGridModel>(new ModelKeyProvider<TreeGridModel>() {
				@Override
				public String getKey(TreeGridModel model) {
					return model.getId();
				}
			}) {
				@Override
				public void applySort(boolean suppressEvent) {
					if (!suppressEvent) {
						fireEvent(new StoreSortEvent<TreeGridModel>());
						loader.load();
					}
				}

				@Override
				protected boolean isSorted() {
					return false;
				}
			};
		loader.addLoadHandler(new ChildTreeStoreBinding<TreeGridModel>(store));

		List<ColumnConfig<TreeGridModel, ?>> columns =
			new ArrayList<ColumnConfig<TreeGridModel, ?>>();

		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			if (gridMetadata.getUISettings().isVisibleRecordsSelector()) {
				IdentityValueProvider<TreeGridModel> identity =
					new IdentityValueProvider<TreeGridModel>();
				selectionModel = new CheckBoxSelectionModel<TreeGridModel>(identity);
				// columns.add(((CheckBoxSelectionModel<TreeGridModel>)
				// selectionModel).getColumn());
			} else {
				selectionModel = new GridSelectionModel<TreeGridModel>();
			}
		} else {
			selectionModel = new CellSelectionModel<TreeGridModel>();
		}

		String styleColumn = getColumnStyle();
		for (final LiveGridColumnConfig egcc : gridMetadata.getColumns()) {
			ColumnConfig<TreeGridModel, String> column =
				new ColumnConfig<TreeGridModel, String>(new TreeGridModelProvider(egcc.getId()),
						egcc.getWidth(), egcc.getCaption());

			column.setToolTip(column.getHeader());

			if (egcc.getHorizontalAlignment() != null) {
				column.setAlignment(HorizontalAlignment.getHorizontalAlignmentConstant(egcc
						.getHorizontalAlignment()));
			}

			column.setMenuDisabled(!gridMetadata.getUISettings().isVisibleColumnsCustomizer());

			if (!styleColumn.isEmpty()) {
				column.setColumnTextStyle(SafeStylesUtils.fromTrustedString(styleColumn));
			}

			if (egcc.getValueType() == GridValueType.DOWNLOAD) {
				column.setColumnTextClassName(CommonStyles.get().inlineBlock());
				column.setColumnTextStyle(SafeStylesUtils.fromTrustedString("padding: 1px 3px;"));

				com.sencha.gxt.cell.core.client.TextButtonCell button =
					new com.sencha.gxt.cell.core.client.TextButtonCell();
				button.setIcon(IconHelper.getImageResource(
						UriUtils.fromSafeConstant(settingsDataGrid.getUrlImageFileDownload()), 16,
						16));
				button.setIconAlign(IconAlign.RIGHT);
				button.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						processFileDownload(grid.getStore().get(event.getContext().getIndex())
								.getId(), egcc.getLinkId());
					}
				});
				column.setCell(button);
			} else {
				column.setCell(new AbstractCell<String>() {
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context,
							String value, SafeHtmlBuilder sb) {
						sb.appendHtmlConstant(value);
					}
				});
			}

			columns.add(column);
		}

		ColumnModel<TreeGridModel> cm = new ColumnModel<TreeGridModel>(columns);

		// ---------------------------
		grid = new TreeGrid<TreeGridModel>(store, cm, columns.get(0));
		grid.setSelectionModel(selectionModel);

		grid.setTreeLoader(loader);
		grid.getView().setTrackMouseOver(false);
		grid.setColumnReordering(true);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setHideHeaders(!gridMetadata.getUISettings().isVisibleColumnsHeader());
		grid.getStyle().setLeafIcon(
				IconHelper.getImageResource(
						UriUtils.fromSafeConstant(Constants.TREE_GRID_IMAGE_LEAF_NODE), 16, 16));

		// ---------------------------

		grid.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				handleClick(grid.getStore().get(event.getRowIndex()).getId(), grid
						.getColumnModel().getColumn(event.getCellIndex()).getHeader().asString(),
						InteractionType.SINGLE_CLICK);
			}
		});
		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellClick(CellDoubleClickEvent event) {
				handleClick(grid.getStore().get(event.getRowIndex()).getId(), grid
						.getColumnModel().getColumn(event.getCellIndex()).getHeader().asString(),
						InteractionType.DOUBLE_CLICK);
			}
		});

		// ---------------------------
		// Стили для записей
		GridViewConfig<TreeGridModel> gvc = new GridViewConfig<TreeGridModel>() {
			@Override
			public String getColStyle(TreeGridModel model,
					ValueProvider<? super TreeGridModel, ?> valueProvider, int rowIndex,
					int colIndex) {
				return "";
			}

			@Override
			public String getRowStyle(TreeGridModel model, int rowIndex) {
				String rowStyle = model.getRowStyle();
				if (rowStyle == null) {
					rowStyle = "";
				}
				return rowStyle;
			}
		};
		grid.getView().setViewConfig(gvc);
		// ---------------------------

		ToolBar buttonBar = new ToolBar();
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE);
			exportToExcelCurrentPage.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					exportToExcel(GridToExcelExportType.CURRENTPAGE);
				}
			});
			buttonBar.add(exportToExcelCurrentPage);
		}

		gridMetadata.getUISettings().setVisibleExportToExcelAll(false);
		if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_ALL);
			exportToExcelAll.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					exportToExcel(GridToExcelExportType.ALL);
				}
			});
			buttonBar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(Constants.GRID_CAPTION_COPY_TO_CLIPBOARD);
			copyToClipboard.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					copyToClipboard();
				}
			});
			buttonBar.add(copyToClipboard);
		}

		// ------------------------------------------------------------------------------

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);

		if (buttonBar.getWidgetCount() > 0) {
			con.add(buttonBar, new VerticalLayoutData(1, -1));
		}
		con.add(grid, new VerticalLayoutData(1, 1));
		if ((gridMetadata.getFooter() != null) && (!gridMetadata.getFooter().isEmpty())) {
			ToolBar footerBar = new ToolBar();
			footerBar.addStyleName(ThemeStyles.getStyle().borderTop());
			footerBar.getElement().getStyle().setProperty("borderBottom", "none");

			LabelToolItem footer = new LabelToolItem(gridMetadata.getFooter());
			footerBar.add(footer);

			con.add(footerBar, new VerticalLayoutData(1, -1));
		}

		FramedPanel cpGrid = new FramedPanel();
		cpGrid.setCollapsible(true);
		cpGrid.setHeadingText(gridMetadata.getHeader());
		grid.setWidth("10%");
		grid.setHeight(gridMetadata.getUISettings().getGridHeight());
		cpGrid.setWidget(con);

		// ------------------------------------------------------------------------------

		p.clear();
		p.setSize(PROC100, PROC100);
		p.add(cpGrid);

		// ------------------------------------------------------------------------------

	}

	// CHECKSTYLE:ON

	private void handleClick(final String recId, final String colId,
			final InteractionType interactionType) {

		saveCurrentClickSelection(recId, colId);

		if (!(selectionModel instanceof CellSelectionModel)) {
			selectedRecordsChanged();
		}

		processClick(recId, colId, interactionType);

	}

	private void processClick(final String rowId, final String colId,
			final InteractionType interactionType) {
		Action ac = null;

		List<ru.curs.showcase.app.api.grid.GridEvent> events =
			gridExtradataLevel0.getEventManager().getEventForCell(rowId, colId, interactionType);

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

		selectionTimer = new com.google.gwt.user.client.Timer() {
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
		for (TreeGridModel lgm : selectionModel.getSelectedItems()) {
			selectedRecordIds.add(lgm.getId());
		}

		Action ac =
			gridExtradataLevel0.getEventManager().getSelectionActionForDependentElements(
					selectedRecordIds);

		runAction(ac);
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */

	private void afterUpdateGrid(final TreeGridModel loadConfig) {

		if (isFirstLoading) {
			resetSelection();
		} else {
			for (String id : expandedIds) {
				TreeGridModel tgm = grid.getStore().findModelWithKey(id);
				if (tgm != null) {
					grid.setExpanded(tgm, true);
				}
			}

			if ((loadConfig != null) && (expandedIds.indexOf(loadConfig) == -1)) {
				expandedIds.clear();
			}

		}

		Cell selected = getStoredRecordId();
		if (selectionModel instanceof CellSelectionModel) {
			int row = getRecordIndexById(selected.recId);
			int col = getColumnIndexById(selected.colId);
			if ((row >= 0) && (col >= 0)) {
				((CellSelectionModel<TreeGridModel>) selectionModel).selectCell(row, col);
			}
		} else {
			for (TreeGridModel lgm : grid.getStore().getAll()) {
				if (lgm.getId().equals(selected.recId)) {
					selectionModel.select(lgm, false);
					break;
				}
			}
		}

		if (isFirstLoading) {
			resetGridSettingsToCurrent();

			runAction(gridExtradataLevel0.getActionForDependentElements());
		} else {
			processClick(selected.recId, selected.colId, InteractionType.SINGLE_CLICK);
		}

		setFirstLoading(false);
	}

	private int getRecordIndexById(final String recId) {
		int index = -1;
		if (recId != null) {
			int i = 0;
			for (TreeGridModel lgm : grid.getStore().getAll()) {
				if (lgm.getId().equals(recId)) {
					index = i;
					break;
				}
				i++;
			}
		}
		return index;
	}

	private int getColumnIndexById(final String colId) {
		int index = -1;
		if (colId != null) {
			int i = 0;
			for (ColumnConfig<TreeGridModel, ?> col : grid.getColumnModel().getColumns()) {
				if (col.getHeader().asString().equals(colId)) {
					index = i;
					break;
				}
				i++;
			}
		}
		return index;
	}

	private int getColumnIndexByLiveId(final String liveId) {
		int index = -1;
		if (liveId != null) {
			int i = 0;
			for (ColumnConfig<TreeGridModel, ?> col : grid.getColumnModel().getColumns()) {
				if (col.getValueProvider().getPath().equals(liveId)) {
					index = i;
					break;
				}
				i++;
			}
		}
		return index;
	}

	private String getColumnStyle() {
		String style = "";

		if (gridMetadata.getTextColor() != null) {
			style = style + "color:" + gridMetadata.getTextColor() + ";";
		}

		if (gridMetadata.getBackgroundColor() != null) {
			style = style + "background-color:" + gridMetadata.getBackgroundColor() + ";";
		}

		if (gridMetadata.getFontSize() != null) {
			style = style + "font-size:" + gridMetadata.getFontSize() + ";";
		}

		if (gridMetadata.getFontSize() != null) {
			for (FontModifier fm : gridMetadata.getFontModifiers()) {
				switch (fm) {
				case ITALIC:
					style = style + "font-style:italic;";
					continue;
				case BOLD:
					style = style + "font-weight:bold;";
					continue;
				case STRIKETHROUGH:
					style = style + "text-decoration:line-through;";
					continue;
				case UNDERLINE:
					style = style + "text-decoration:underline;";
					continue;
				default:
					continue;
				}
			}
		}

		if ((cs.getColumns().get(0) != null)
				&& (cs.getColumns().get(0).getDisplayMode() != ColumnValueDisplayMode.SINGLELINE)) {
			style = style + "white-space:normal;";
		} else {
			style = style + "white-space:nowrap;";
		}

		return style;
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

	private void resetSelection() {
		selectionModel.deselectAll();
		if (localContext == null) {
			return;
		}
		localContext.getSelectedRecordIds().clear();
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		localContext.getSelectedRecordIds().clear();

		for (TreeGridModel lgm : selectionModel.getSelectedItems()) {
			localContext.getSelectedRecordIds().add(lgm.getId());
		}
	}

	private void saveCurrentClickSelection(final String recId, final String colId) {
		localContext.setCurrentRecordId(recId);
		localContext.setCurrentColumnId(colId);
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
			cell.recId = gridExtradataLevel0.getAutoSelectRecordId();
			cell.colId = gridExtradataLevel0.getAutoSelectColumnId();
		}
		return cell;
	}

	private void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
		localContext.setParentId(null);
		localContext.resetForReturnAllRecords();

		saveCurrentCheckBoxSelection();

		Cell selected = getStoredRecordId();
		saveCurrentClickSelection(selected.recId, selected.colId);
	}

	/**
	 * Экспорт в Excel.
	 * 
	 * @param exportType
	 *            GridToExcelExportType
	 */
	public void exportToExcel(final GridToExcelExportType exportType) {

		if (grid.getStore().getAll().size() == 0) {
			MessageBox.showSimpleMessage("Экспорт в Excel",
					"Таблица пуста. Экспорт в Excel выполнен не будет.");
			return;
		}

		GridContext context = getDetailedContext();
		context.setParentId(null);
		TreeGridModel child = selectionModel.getSelectedItem();
		if (child != null) {
			TreeGridModel parent = grid.getTreeStore().getParent(child);
			if (parent != null) {
				context.setParentId(parent.getId());
			}
		}

		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			SerializationStreamFactory ssf = dh.getAddObjectSerializer();
			dh.addStdPostParamsToBody(context, getElementInfo());
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

		List<ColumnConfig<TreeGridModel, ?>> columns = grid.getColumnModel().getColumns();

		String d = "";
		for (ColumnConfig<TreeGridModel, ?> c : columns) {
			b.append(d).append(c.getHeader().asString());
			d = "\t";
		}
		b.append("\n");

		List<TreeGridModel> models;
		if (selectionModel.getSelectedItems().size() > 0) {
			models = selectionModel.getSelectedItems();
		} else {
			models = grid.getStore().getAll();
		}

		for (TreeGridModel lgm : models) {
			d = "";
			for (ColumnConfig<TreeGridModel, ?> c : columns) {
				b.append(d).append(lgm.get(c.getValueProvider().getPath()));
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
			result.setSubtype(DataPanelElementSubType.EXT_TREE_GRID);
			result.setParentId(null);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		result.resetForReturnAllRecords();
		result.assignNullValues(getContext());
		return result;
	}

}
