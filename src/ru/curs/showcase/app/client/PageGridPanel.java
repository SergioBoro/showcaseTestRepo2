package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.*;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.*;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.resources.*;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.*;
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
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.toolbar.*;

/**
 * Класс панели с гридом.
 */
public class PageGridPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";

	private final VerticalPanel p = new VerticalPanel();
	private final MessagePopup mp = new MessagePopup(AppCurrContext.getInstance().getBundleMap()
			.get("grid_message_popup_export_to_excel"));
	private final DataGridSettings settingsDataGrid = new DataGridSettings();
	private PagingToolBar toolBarTop = null;
	private PagingToolBar toolBarBottom = null;
	private TextBox pageSizeTop = null;
	private TextBox pageSizeBottom = null;
	private final FramedPanel cpGrid = new FramedPanel();
	private com.sencha.gxt.widget.core.client.grid.Grid<LiveGridModel> grid = null;
	private GridSelectionModel<LiveGridModel> selectionModel = null;
	private ColumnSet cs = null;
	private Timer selectionTimer = null;
	private Timer clickTimer = null;
	private Timer firstSelectionTimer = null;
	private boolean doubleClick = false;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
	private LiveGridMetadata gridMetadata = null;
	private LiveGridExtradata gridExtradata = null;
	private boolean isFirstLoading = true;
	private int lastScrollLeft = 0;
	private ToolBarHelper toolBarHelper;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean aIsFirstLoading) {
		isFirstLoading = aIsFirstLoading;
	}

	private boolean needRestoreAfterShowLoadingMessage = false;

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
	public PageGridPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
	}

	/**
	 * Конструктор класса GridPanel.
	 */
	public PageGridPanel(final CompositeContext context, final DataPanelElementInfo element,
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
			if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
				// p.add(new HTML(AppCurrContext.getInstance().getBundleMap()
				// .get("please_wait_data_are_loading")));
				p.add(new HTML("<div class=\"progress-bar\"></div>"));
			} else {
				p.add(new HTML(""));
			}

			// p.add(new
			// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
			// .please_wait_data_are_loading()));
		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				// p.clear();
				// p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

				cpGrid.setEnabled(false);

				needRestoreAfterShowLoadingMessage = true;
			}
		}

		if (isFirstLoading() || isNeedResetLocalContext()) {
			localContext = null;
			setFirstLoading(true);
			setDataGridPanel();
		} else {
			setFirstLoading(false);
			grid.getLoader().load();
		}

	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		GridContext gc = getDetailedContext();

		dataService.getLiveGridMetadata(gc, getElementInfo(),
				new GWTServiceCallback<LiveGridMetadata>(AppCurrContext.getInstance()
						.getBundleMap().get("gridErrorGetTable")) {

					@Override
					public void onSuccess(final LiveGridMetadata aGridMetadata) {

						super.onSuccess(aGridMetadata);

						setDataGridPanelByGrid(aGridMetadata);
					}
				});
	}

	private void setDataGridPanelByGrid(final LiveGridMetadata aGridMetadata) {
		gridMetadata = aGridMetadata;

		beforeUpdateGrid();

		updateGridFull();

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

		RpcProxy<PagingLoadConfig, LiveGridData<LiveGridModel>> proxy =
			new RpcProxy<PagingLoadConfig, LiveGridData<LiveGridModel>>() {

				@Override
				public void load(final PagingLoadConfig loadConfig,
						final AsyncCallback<LiveGridData<LiveGridModel>> callback) {

					cancelTimer();

					lastScrollLeft = grid.getView().getScroller().getScrollLeft();

					GridContext gridContext = getDetailedContext();
					gridContext.getLiveInfo().setOffset(loadConfig.getOffset());
					gridContext.getLiveInfo().setLimit(loadConfig.getLimit());

					if (!loadConfig.getSortInfo().isEmpty()) {
						ColumnConfig<LiveGridModel, ?> colConfig =
							grid.getColumnModel().getColumn(
									getColumnIndexByLiveId(loadConfig.getSortInfo().get(0)
											.getSortField()));
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

								colOriginal.setSorting(Sorting.valueOf(loadConfig.getSortInfo()
										.get(0).getSortDir().name()));

								sortOriginalCols.add(colOriginal);

								gridContext.setSortedColumns(sortOriginalCols);
							}
						}
					}

					if (isFirstLoading()) {
						callback.onSuccess(gridMetadata.getLiveGridData());
						gridExtradata = gridMetadata.getLiveGridData().getLiveGridExtradata();
						afterUpdateGrid();
					} else {
						dataService.getLiveGridData(gridContext, getElementInfo(),
								new AsyncCallback<LiveGridData<LiveGridModel>>() {
									@Override
									public void onFailure(Throwable caught) {
										callback.onFailure(caught);
									}

									@Override
									public void onSuccess(LiveGridData<LiveGridModel> result) {
										callback.onSuccess(result);
										gridExtradata = result.getLiveGridExtradata();
										afterUpdateGrid();
									}
								});
					}

				}

			};

		final ListStore<LiveGridModel> store =
			new ListStore<LiveGridModel>(new ModelKeyProvider<LiveGridModel>() {
				@Override
				public String getKey(LiveGridModel model) {
					return model.getId();
				}
			});

		final PagingLoader<PagingLoadConfig, LiveGridData<LiveGridModel>> loader =
			new PagingLoader<PagingLoadConfig, LiveGridData<LiveGridModel>>(proxy);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, LiveGridModel, LiveGridData<LiveGridModel>>(
				store));

		if (gridMetadata.getUISettings().isVisiblePager()) {
			toolBarTop = new PagingToolBar(gridMetadata.getLiveInfo().getLimit());
			toolBarTop.getElement().getStyle().setProperty("borderBottom", "none");
			pageSizeTop = new TextBox();
			addPageSizeToPagingToolBar(toolBarTop, pageSizeTop);
			toolBarTop.bind(loader);

			if (gridMetadata.getLiveInfo().getLimit() > gridMetadata.getUISettings()
					.getPagerDuplicateRecords()) {
				toolBarBottom = new PagingToolBar(gridMetadata.getLiveInfo().getLimit());
				toolBarBottom.getElement().getStyle().setProperty("borderBottom", "none");
				pageSizeBottom = new TextBox();
				addPageSizeToPagingToolBar(toolBarBottom, pageSizeBottom);
				toolBarBottom.bind(loader);
			}
		}

		List<ColumnConfig<LiveGridModel, ?>> columns =
			new ArrayList<ColumnConfig<LiveGridModel, ?>>();

		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			if (gridMetadata.getUISettings().isVisibleRecordsSelector()) {
				IdentityValueProvider<LiveGridModel> identity =
					new IdentityValueProvider<LiveGridModel>();
				selectionModel = new CheckBoxSelectionModel<LiveGridModel>(identity);
				// columns.add(((CheckBoxSelectionModel<LiveGridModel>)
				// selectionModel).getColumn());
			} else {
				selectionModel = new GridSelectionModel<LiveGridModel>();
			}
		} else {
			selectionModel = new CellSelectionModel<LiveGridModel>();
		}

		String styleColumn = getColumnStyle();
		for (final LiveGridColumnConfig egcc : gridMetadata.getColumns()) {
			ColumnConfig<LiveGridModel, String> column =
				new ColumnConfig<LiveGridModel, String>(new LiveGridModelProvider(egcc.getId()),
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

			if (cs.getColumns().get(0) != null) {

				String s = "";
				if (cs.getColumns().get(0).getDisplayMode() != ColumnValueDisplayMode.SINGLELINE) {
					s = "ext-grid-header-wrap";
				}

				s = s + " ext-grid-header";

				column.setColumnHeaderClassName(s);
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

						// sb.appendHtmlConstant(value);
						sb.appendHtmlConstant("<span qtip='" + value + "'>" + value + "</span>");

					}
				});
			}

			columns.add(column);
		}

		ColumnModel<LiveGridModel> cm = new ColumnModel<LiveGridModel>(columns);

		// ---------------------------

		grid = new com.sencha.gxt.widget.core.client.grid.Grid<LiveGridModel>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						loader.load();
					}
				});
			}
		};
		grid.setSelectionModel(selectionModel);
		grid.setLoader(loader);
		grid.setColumnReordering(true);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setHideHeaders(!gridMetadata.getUISettings().isVisibleColumnsHeader());

		final QuickTip qt = new QuickTip(grid); // чтобы работали хинты на
												// ячейки
		qt.getToolTipConfig().setDismissDelay(0);

		// ---------------------------

		grid.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(final CellClickEvent event) {

				qt.hide();

				if (gridMetadata.getUISettings().isSingleClickBeforeDoubleClick()) {
					handleClick(grid.getStore().get(event.getRowIndex()).getId(), grid
							.getColumnModel().getColumn(event.getCellIndex()).getHeader()
							.asString(), InteractionType.SINGLE_CLICK);
				} else {

					doubleClick = false;

					if (clickTimer != null) {
						clickTimer.cancel();
					}

					clickTimer = new Timer() {
						@Override
						public void run() {
							if (!doubleClick) {
								handleClick(grid.getStore().get(event.getRowIndex()).getId(), grid
										.getColumnModel().getColumn(event.getCellIndex())
										.getHeader().asString(), InteractionType.SINGLE_CLICK);
							}
						}
					};
					clickTimer.schedule(gridMetadata.getUISettings().getDoubleClickTime());

				}
			}
		});
		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellClick(CellDoubleClickEvent event) {

				qt.hide();

				doubleClick = true;

				handleClick(grid.getStore().get(event.getRowIndex()).getId(), grid
						.getColumnModel().getColumn(event.getCellIndex()).getHeader().asString(),
						InteractionType.DOUBLE_CLICK);
			}
		});

		// ---------------------------
		// Стили для записей
		GridViewConfig<LiveGridModel> gvc = new GridViewConfig<LiveGridModel>() {
			@Override
			public String getColStyle(LiveGridModel model,
					ValueProvider<? super LiveGridModel, ?> valueProvider, int rowIndex,
					int colIndex) {
				return "";
			}

			@Override
			public String getRowStyle(LiveGridModel model, int rowIndex) {
				String rowStyle = model.getRowStyle();
				if (rowStyle == null) {
					rowStyle = "";
				}
				return rowStyle;
			}
		};
		grid.getView().setViewConfig(gvc);

		grid.getView().setStripeRows(gridMetadata.getUISettings().isStripeRows());
		grid.getView().setColumnLines(gridMetadata.getUISettings().isColumnLines());

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.setBorders(true);

		ToolBarHelper toolBarHelper = getToolBarHelper();
		toolBarHelper.fillToolBar();
		con.add(toolBarHelper.getToolBarPanel(), new VerticalLayoutData(1, 27));

		if (toolBarTop != null) {
			con.add(toolBarTop, new VerticalLayoutData(1, -1));
		}
		con.add(grid, new VerticalLayoutData(1, 1));
		if (toolBarBottom != null) {
			con.add(toolBarBottom, new VerticalLayoutData(1, -1));
		}
		if ((gridMetadata.getFooter() != null) && (!gridMetadata.getFooter().isEmpty())) {
			ToolBar footerBar = new ToolBar();
			footerBar.addStyleName(ThemeStyles.getStyle().borderTop());
			footerBar.getElement().getStyle().setProperty("borderBottom", "none");

			LabelToolItem footer = new LabelToolItem(gridMetadata.getFooter());
			footerBar.add(footer);

			con.add(footerBar, new VerticalLayoutData(1, -1));
		}

		cpGrid.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				cpGrid.setWidth(event.getWidth() + "px");
			}
		});
		cpGrid.getElement().setZIndex(0);
		cpGrid.setCollapsible(true);
		cpGrid.setHeadingText(gridMetadata.getHeader());
		cpGrid.setWidth(gridMetadata.getUISettings().getGridWidth());
		grid.setWidth("100%");
		grid.setHeight(gridMetadata.getUISettings().getGridHeight());
		cpGrid.setWidget(con);

		// ------------------------------------------------------------------------------

		p.clear();
		p.setSize(PROC100, PROC100);
		p.add(cpGrid);

		// ------------------------------------------------------------------------------

	}

	// CHECKSTYLE:ON

	private void addPageSizeToPagingToolBar(final PagingToolBar toolBar, final TextBox pageSize) {
		pageSize.setValue(Integer.toString(toolBar.getPageSize()), false);
		pageSize.setWidth("30px");
		pageSize.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (toolBarTop != null) {
						toolBarTop.setPageSize(Integer.parseInt(pageSize.getValue()));
						pageSizeTop.setValue(Integer.toString(toolBarTop.getPageSize()), false);
					}
					if (toolBarBottom != null) {
						toolBarBottom.setPageSize(Integer.parseInt(pageSize.getValue()));
						pageSizeBottom.setValue(Integer.toString(toolBarBottom.getPageSize()),
								false);
					}

					toolBar.refresh();
				}
			}
		});

		final int widgetIndex = 6;
		toolBar.insert(pageSize, widgetIndex);
		toolBar.insert(
				new HTML(AppCurrContext.getInstance().getBundleMap().get("pageGridShowAtMost")
						+ " "), widgetIndex);
		toolBar.insert(new SeparatorToolItem(), widgetIndex);
	}

	private void handleClick(final String recId, final String colId,
			final InteractionType interactionType) {

		saveCurrentClickSelection(recId, colId);

		if (!(selectionModel instanceof CellSelectionModel)) {
			selectedRecordsChanged();
		}

		getToolBarHelper().fillToolBar();

		processClick(recId, colId, interactionType);

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
				saveCurrentCheckBoxSelection();
				processSelectionRecords();
			}
		};
		selectionTimer.schedule(Constants.GRID_SELECTION_DELAY);

	}

	private void processSelectionRecords() {
		List<String> selectedRecordIds = new ArrayList<String>();
		for (LiveGridModel lgm : selectionModel.getSelectedItems()) {
			selectedRecordIds.add(lgm.getId());
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

		if (needRestoreAfterShowLoadingMessage) {
			// p.clear();
			// p.add(cpGrid);

			needRestoreAfterShowLoadingMessage = false;

			cpGrid.setEnabled(true);
		}

		if (isFirstLoading) {
			resetSelection();
		} else {
			grid.getView().getScroller().scrollTo(ScrollDirection.LEFT, lastScrollLeft);
		}

		Cell selected = getStoredRecordId();
		if (selectionModel instanceof CellSelectionModel) {
			int row = getRecordIndexById(selected.recId);
			int col = getColumnIndexById(selected.colId);
			if ((row >= 0) && (col >= 0)) {
				((CellSelectionModel<LiveGridModel>) selectionModel).selectCell(row, col);
			}
		} else {
			for (final LiveGridModel lgm : grid.getStore().getAll()) {
				if (lgm.getId().equals(selected.recId)) {

					if (firstSelectionTimer != null) {
						firstSelectionTimer.cancel();
					}

					firstSelectionTimer = new Timer() {
						@Override
						public void run() {
							selectionModel.select(lgm, false);
						}
					};
					final int firstSelectionTimerDelay = 100;
					firstSelectionTimer.schedule(firstSelectionTimerDelay);

					// selectionModel.select(lgm, false);
					break;
				}
			}
		}

		if (isFirstLoading) {
			resetGridSettingsToCurrent();

			runAction(gridExtradata.getActionForDependentElements());
		} else {
			processClick(selected.recId, selected.colId, InteractionType.SINGLE_CLICK);
		}

		setupTimer();

		setFirstLoading(false);

	}

	private int getRecordIndexById(final String recId) {
		int index = -1;
		if (recId != null) {
			int i = 0;
			for (LiveGridModel lgm : grid.getStore().getAll()) {
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
			for (ColumnConfig<LiveGridModel, ?> col : grid.getColumnModel().getColumns()) {
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
			for (ColumnConfig<LiveGridModel, ?> col : grid.getColumnModel().getColumns()) {
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

		for (LiveGridModel lgm : selectionModel.getSelectedItems()) {
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
			cell.recId = gridExtradata.getAutoSelectRecordId();
			cell.colId = gridExtradata.getAutoSelectColumnId();
		}
		return cell;
	}

	private void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.EXT_PAGE_GRID);

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
	public void exportToExcel(final Widget wFrom, final GridToExcelExportType exportType) {

		if (grid.getStore().getAll().size() == 0) {
			MessageBox
					.showSimpleMessage(
							AppCurrContext.getInstance().getBundleMap()
									.get("gridExportToExcelCaption"), AppCurrContext.getInstance()
									.getBundleMap().get("gridExportToExcelEmptyTable"));
			return;
		}

		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(AppCurrContext.getInstance().getBundleMap()
				.get("grid_error_caption_export_excel"));
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			SerializationStreamFactory ssf = dh.getAddObjectSerializer();
			dh.addStdPostParamsToBody(getDetailedContext(), getElementInfo());
			dh.addParam(cs.getClass().getName(), cs.toParamForHttpPost(ssf));

			dh.submit();

			mp.hide();
			mp.show(wFrom);

		} catch (SerializationException e) {
			mp.hide();
			MessageBox.showSimpleMessage(
					AppCurrContext.getInstance().getBundleMap()
							.get("grid_error_caption_export_excel"), e.getMessage());
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

		List<ColumnConfig<LiveGridModel, ?>> columns = grid.getColumnModel().getColumns();

		String d = "";
		for (ColumnConfig<LiveGridModel, ?> c : columns) {
			b.append(d).append(c.getHeader().asString());
			d = "\t";
		}
		b.append("\n");

		List<LiveGridModel> models;
		if (selectionModel.getSelectedItems().size() > 0) {
			models = selectionModel.getSelectedItems();
		} else {
			models = grid.getStore().getAll();
		}

		for (LiveGridModel lgm : models) {
			d = "";
			for (ColumnConfig<LiveGridModel, ?> c : columns) {
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

		dh.setErrorCaption(AppCurrContext.getInstance().getBundleMap()
				.get("grid_error_caption_file_download"));
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

		try {
			dh.addParam("linkId", colLinkId);
			dh.addStdPostParamsToBody(getContext(), getElementInfo());
			dh.addParam("recordId", recId);

			dh.submit();
		} catch (SerializationException e) {
			ru.curs.showcase.app.client.MessageBox.showSimpleMessage(AppCurrContext.getInstance()
					.getBundleMap().get("grid_error_caption_file_download"), e.getMessage());
		}
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.EXT_PAGE_GRID);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		// result.assignNullValues(getContext());
		result.applyCompositeContext(getContext());

		result.setCurrentDatapanelWidth(GeneralDataPanel.getTabPanel().getOffsetWidth());
		result.setCurrentDatapanelHeight(GeneralDataPanel.getTabPanel().getOffsetHeight());

		return result;
	}

	private void addStaticItemToToolBar(final ToolBar toolBar) {
		final TextButton exportToExcelCurrentPage =
			new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE),
					16, 16));
		final TextButton exportToExcelAll =
			new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL), 16, 16));
		final TextButton copyToClipboard =
			new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_COPY_TO_CLIPBOARD), 16, 16));
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(AppCurrContext.getInstance().getBundleMap()
					.get("grid_caption_export_to_excel_current_page"));
			exportToExcelCurrentPage.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					exportToExcel(exportToExcelCurrentPage, GridToExcelExportType.CURRENTPAGE);
				}
			});
			toolBar.add(exportToExcelCurrentPage);
		}
		if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(AppCurrContext.getInstance().getBundleMap()
					.get("grid_caption_export_to_excel_all"));
			exportToExcelAll.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					exportToExcel(exportToExcelAll, GridToExcelExportType.ALL);
				}
			});
			toolBar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(AppCurrContext.getInstance().getBundleMap()
					.get("grid_caption_copy_to_clipboard"));
			copyToClipboard.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					copyToClipboard();
				}
			});
			toolBar.add(copyToClipboard);
		}

	}

	private ToolBarHelper getToolBarHelper() {
		if (this.toolBarHelper == null) {
			final PageGridPanel pageGridPanel = this;
			this.toolBarHelper = new ToolBarHelper(dataService, this) {

				@Override
				public void addStaticItemToToolBar(final ToolBar toolBar) {
					pageGridPanel.addStaticItemToToolBar(toolBar);
				}

				@Override
				public void runAction(final Action ac) {
					pageGridPanel.runAction(ac);
				}
			};
		}
		return this.toolBarHelper;
	}
}
