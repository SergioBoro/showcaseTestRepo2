package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.*;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.*;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Класс-адаптер панели с внешним плагином типа JSLiveGrid.
 */
public class JSLiveGridPluginPanel extends BasicElementPanelBasis {
	private static final String PROC100 = "100%";

	private final VerticalPanel p = new VerticalPanel();
	private final HorizontalPanel generalHp = new HorizontalPanel();
	/**
	 * HTML виждет для плагина.
	 */
	private HTML pluginHTML = null;

	private final HorizontalPanel hpHeader = new HorizontalPanel();
	private final HorizontalPanel hpToolbar = new HorizontalPanel();
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	// private final PushButton exportToExcelCurrentPage = new PushButton(new
	// Image(
	// Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	// private final PushButton exportToExcelAll = new PushButton(new Image(
	// Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));
	// private final PushButton copyToClipboard = new PushButton(new Image(
	// Constants.GRID_IMAGE_COPY_TO_CLIPBOARD));
	private final MessagePopup mp = new MessagePopup(AppCurrContext.getInstance()
			.getInternationalizedMessages().grid_message_popup_export_to_excel());

	/**
	 * Основная фабрика для GWT сериализации.
	 */
	private SerializationStreamFactory ssf = null;
	private Timer selectionTimer = null;
	private Timer clickTimer = null;
	private boolean doubleClick = false;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;

	public GridContext getLocalContext() {
		return localContext;
	}

	private LiveGridMetadata gridMetadata = null;
	private LiveGridExtradata gridExtradata = null;
	private String stringSelectedRecordIds = null;
	private boolean isFirstLoading = true;

	private boolean isFirstLoading() {
		return isFirstLoading;
	}

	private void setFirstLoading(final boolean aIsFirstLoading) {
		isFirstLoading = aIsFirstLoading;
	}

	private boolean needRestoreAfterShowLoadingMessage = false;

	private ToolBarHelper toolBarHelper = null;

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
	 * Возвращает "сериализатор" для gwt объектов.
	 * 
	 * @return - SerializationStreamFactory.
	 */
	private SerializationStreamFactory getObjectSerializer() {
		if (ssf == null) {
			ssf = WebUtils.createStdGWTSerializer();
		}
		return ssf;
	}

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
															$wnd.gwtGetHttpParams = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterLoadData = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginAfterLoadData(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterClick = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginAfterClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterDoubleClick = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginAfterDoubleClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtProcessFileDownload = @ru.curs.showcase.app.client.api.JSLiveGridPluginPanelCallbacksEvents::pluginProcessFileDownload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															}-*/;

	// CHECKSTYLE:ON

	public JSLiveGridPluginPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
	}

	public JSLiveGridPluginPanel(final CompositeContext context, final DataPanelElementInfo element) {
		setContext(context);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();

		refreshPanel();
	}

	@Override
	public void reDrawPanel(final CompositeContext context) {
		setContext(context);

		refreshPanel();
	}

	@Override
	public void refreshPanel() {
		if (isFirstLoading()) {
			p.add(new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
					.please_wait_data_are_loading()));
		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				// // p.clear();
				// // p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

				// cpGrid.setEnabled(false);

				needRestoreAfterShowLoadingMessage = true;

			}
		}

		if (isFirstLoading() || isNeedResetLocalContext()) {
			localContext = null;
			setFirstLoading(true);
			setDataGridPanel();
		} else {
			setFirstLoading(false);
			String params = "'" + getDivIdPlugin() + "'";
			pluginRefresh(gridMetadata.getJSInfo().getRefreshProc(), params);
		}
	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		GridContext gc = getDetailedContext();

		dataService.getLiveGridMetadata(gc, getElementInfo(),
				new GWTServiceCallback<LiveGridMetadata>(AppCurrContext.getInstance()
						.getInternationalizedMessages().gridErrorGetTable()) {

					@Override
					public void onSuccess(final LiveGridMetadata aGridMetadata) {
						setDataGridPanelByGrid(aGridMetadata);
					}
				});
	}

	private void setDataGridPanelByGrid(final LiveGridMetadata aGridMetadata) {
		gridMetadata = aGridMetadata;
		gridExtradata = gridMetadata.getLiveGridData().getLiveGridExtradata();

		beforeUpdateGrid();

		updateGridFull();

		p.setHeight(PROC100);
	}

	@Override
	protected void internalResetLocalContext() {
		localContext = null;
	}

	private String getDivIdPlugin() {
		return getElementInfo().getFullId() + Constants.PLUGIN_DIV_ID_SUFFIX;
	}

	private void beforeUpdateGrid() {
		resetLocalContext();
	}

	// CHECKSTYLE:OFF
	private void updateGridFull() {

		// MessageBox.showSimpleMessage("",
		// "gridMetadata.getUISettings().getGridWidth() = "
		// + gridMetadata.getUISettings().getGridWidth()
		// + ", gridMetadata.getUISettings().getGridHeight() = "
		// + gridMetadata.getUISettings().getGridHeight());

		final String div = "<div id='";
		final String htmlForPlugin =
			div + getDivIdPlugin() + "' style='width:"
					+ gridMetadata.getUISettings().getGridWidth() + "; height:"
					+ gridMetadata.getUISettings().getGridHeight() + "px'></div>";

		pluginHTML = new HTML(htmlForPlugin);

		String params =
			"'" + getElementInfo().getId().toString() + "'" + ", " + "'" + getDivIdPlugin() + "'";

		if (AppCurrContext.getInstance()
				.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
				.indexOf(getDivIdPlugin()) < 0) {
			AppCurrContext.getInstance()
					.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
					.add(getDivIdPlugin());
			// for (String param : aPlugin.getRequiredCSS()) {
			// AccessToDomModel.addCSSLink(param);
			// }
			for (String param : gridMetadata.getJSInfo().getRequiredJS()) {
				AccessToDomModel.addScriptLink(param);
			}

		}

		// ----------------------------------------

		JSONObject metadata = new JSONObject();

		JSONObject common = new JSONObject();
		common.put("limit", new JSONString(String.valueOf(gridMetadata.getLiveInfo().getLimit())));

		String selectionModel = "CELL";
		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectionModel = "RECORDS";
		}
		common.put("selectionModel", new JSONString(selectionModel));

		Cell selected = getStoredRecordId();
		if (selected.recId != null) {
			common.put("selRecId", new JSONString(selected.recId));
		}
		if (selected.colId != null) {
			common.put("selColId", new JSONString(selected.colId));
		}

		if (gridMetadata.getUISettings().isVisiblePager()) {
			common.put("isVisiblePager", new JSONString("true"));
		}

		if (gridMetadata.getUISettings().isVisibleColumnsHeader()) {
			common.put("isVisibleColumnsHeader", new JSONString("true"));
		}

		common.put("loadingMessage", new JSONString(AppCurrContext.getInstance()
				.getInternationalizedMessages().jsGridLoadingMessage()));

		metadata.put("common", common);

		JSONObject columns = new JSONObject();
		for (final LiveGridColumnConfig egcc : gridMetadata.getColumns()) {
			JSONObject column = new JSONObject();
			column.put("id", new JSONString(egcc.getId()));
			column.put("caption", new JSONString(egcc.getCaption()));
			String valueType = "";
			if (egcc.getValueType() != null) {
				valueType = egcc.getValueType().toString();
			}
			column.put("valueType", new JSONString(valueType));

			column.put("style", new JSONString(getCommonColumnStyle() + getColumnStyle(egcc)));

			column.put("urlImageFileDownload", new JSONString(gridMetadata.getUISettings()
					.getUrlImageFileDownload()));

			columns.put(egcc.getId(), column);
		}
		metadata.put("columns", columns);

		JSONObject data = new JSONObject();
		JSONArray rows = new JSONArray();
		int i = 0;
		for (LiveGridModel lgm : gridMetadata.getLiveGridData().getData()) {
			JSONObject row = new JSONObject();
			for (String key : lgm.getMap().keySet()) {
				if (lgm.getMap().get(key) == null) {
					row.put(key, null);
				} else {
					row.put(key, new JSONString((String) lgm.getMap().get(key)));
				}
			}
			rows.set(i, row);
			i++;
		}
		data.put("rows", rows);
		data.put("total",
				new JSONString(Integer.toString(gridMetadata.getLiveGridData().getTotalLength())));

		metadata.put("data", data);

		params = params + ", " + metadata;

		// ----------------------------------------

		hpHeader.clear();
		HTML header = new HTML();
		if (!gridMetadata.getHeader().isEmpty()) {
			header.setHTML(gridMetadata.getHeader());
		}
		hpHeader.add(header);

		hpFooter.clear();
		HTML footer = new HTML();
		if (!gridMetadata.getFooter().isEmpty()) {
			footer.setHTML(gridMetadata.getFooter());
		}
		hpFooter.add(footer);

		p.setSize(PROC100, PROC100);

		hpHeader.setSize(PROC100, PROC100);
		hpFooter.setSize(PROC100, PROC100);

		// hpToolbar.setHeight(PROC100);
		// hpToolbar.setSpacing(1);
		// if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage())
		// {
		// exportToExcelCurrentPage.setTitle(AppCurrContext.getInstance()
		// .getInternationalizedMessages().grid_caption_export_to_excel_current_page());
		// exportToExcelCurrentPage.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(final ClickEvent event) {
		// exportToExcel(exportToExcelCurrentPage,
		// GridToExcelExportType.CURRENTPAGE);
		// }
		// });
		// hpToolbar.add(exportToExcelCurrentPage);
		// }
		// if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
		// exportToExcelAll.setTitle(AppCurrContext.getInstance().getInternationalizedMessages()
		// .grid_caption_export_to_excel_all());
		// exportToExcelAll.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(final ClickEvent event) {
		// exportToExcel(exportToExcelAll, GridToExcelExportType.ALL);
		// }
		// });
		// hpToolbar.add(exportToExcelAll);
		// }
		// if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
		// copyToClipboard.setTitle(AppCurrContext.getInstance().getInternationalizedMessages()
		// .grid_caption_copy_to_clipboard());
		// copyToClipboard.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(final ClickEvent event) {
		// copyToClipboard();
		// }
		// });
		// hpToolbar.add(copyToClipboard);
		// }

		generalHp.clear();
		p.clear();
		p.add(hpHeader);
		// ----------------------------------------

		ToolBarHelper toolBarHelper = getToolBarHelper();
		toolBarHelper.getToolBarPanel().setWidth(gridMetadata.getUISettings().getGridWidth());

		hpToolbar.add(toolBarHelper.getToolBarPanel());
		p.add(hpToolbar);

		// ----------------------------------------
		p.add(generalHp);
		generalHp.add(pluginHTML);
		p.add(hpFooter);

		// ----------------------------------------

		try {
			drawPlugin(gridMetadata.getJSInfo().getCreateProc(), params);
		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(AppCurrContext.getInstance()
						.getInternationalizedMessages().error_of_plugin_painting(),
						e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
						GeneralException.getMessageType(e.getCause()),
						GeneralException.needDetailedInfo(e.getCause()));
			} else {
				MessageBox
						.showSimpleMessage(AppCurrContext.getInstance()
								.getInternationalizedMessages().error_of_plugin_painting(),
								e.getMessage());
			}
		}

	}

	// CHECKSTYLE:ON

	/**
	 * 
	 * Процедура прорисовки плагина.
	 * 
	 * @param procName
	 *            - имя js - процедуры для прорисовки плагина
	 * @param params
	 *            - параметры js - процедуры для прорисовки плагина
	 * 
	 */
	private native void drawPlugin(final String procName, final String params) /*-{
		$wnd.eval(procName + "(" + params + ");");
	}-*/;

	private native void pluginRefresh(final String procName, final String params) /*-{
		$wnd.eval(procName + "(" + params + ");");
	}-*/;

	private native String pluginClipboard(final String procName, final String params) /*-{
		return $wnd.eval(procName + "(" + params + ");");
	}-*/;

	public void pluginProcessFileDownload(final String recId, final String colId) {
		String colLinkId = null;
		for (LiveGridColumnConfig lgcc : gridMetadata.getColumns()) {
			if (colId.equals(lgcc.getId())) {
				colLinkId = lgcc.getLinkId();
				break;
			}
		}

		if (colLinkId != null) {
			DownloadHelper dh = DownloadHelper.getInstance();
			dh.setEncoding(FormPanel.ENCODING_URLENCODED);
			dh.clear();

			dh.setErrorCaption(AppCurrContext.getInstance().getInternationalizedMessages()
					.grid_error_caption_file_download());
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

			try {
				dh.addParam("linkId", colLinkId);

				dh.addParam(getContext().getClass().getName(),
						getContext().toParamForHttpPost(getObjectSerializer()));
				dh.addParam(DataPanelElementInfo.class.getName(), getElementInfo()
						.toParamForHttpPost(getObjectSerializer()));

				dh.addParam("recordId", recId);

				dh.submit();
			} catch (SerializationException e) {
				ru.curs.showcase.app.client.MessageBox.showSimpleMessage(AppCurrContext
						.getInstance().getInternationalizedMessages()
						.grid_error_caption_file_download(), e.getMessage());
			}
		}
	}

	public JSONObject pluginGetHttpParams(final int offset, final int limit,
			final String sortColId, final String sortColDir) {

		GridContext gridContext = getDetailedContext();
		gridContext.getLiveInfo().setOffset(offset);
		gridContext.getLiveInfo().setLimit(limit);

		Column colOriginal = null;
		for (Column c : gridMetadata.getOriginalColumnSet().getColumns()) {
			if (sortColId.equals(c.getId())) {
				colOriginal = c;
				break;
			}
		}
		if (colOriginal != null) {
			List<Column> sortOriginalCols = new ArrayList<Column>();
			colOriginal.setSorting(ru.curs.gwt.datagrid.model.Sorting.valueOf(sortColDir));
			sortOriginalCols.add(colOriginal);
			gridContext.setSortedColumns(sortOriginalCols);
		}

		JSONObject params = new JSONObject();
		try {
			params.put("gridContextName", new JSONString(gridContext.getClass().getName()));
			params.put("gridContextValue",
					new JSONString(gridContext.toParamForHttpPost(getObjectSerializer())));

			params.put("elementInfoName", new JSONString(getElementInfo().getClass().getName()));
			params.put("elementInfoValue",
					new JSONString(getElementInfo().toParamForHttpPost(getObjectSerializer())));
		} catch (SerializationException e) {
			params.put("error", new JSONString(AppCurrContext.getInstance()
					.getInternationalizedMessages().jsGridSerializationError()));
		}

		return params;
	}

	public void pluginAfterLoadData(final String stringLiveGridExtradata) {
		if (!stringLiveGridExtradata.isEmpty()) {
			try {
				LiveGridExtradata gridExtradataNew =
					(LiveGridExtradata) getObjectSerializer().createStreamReader(
							stringLiveGridExtradata).readObject();

				boolean needAdd;
				for (ru.curs.showcase.app.api.grid.GridEvent ev : gridExtradataNew
						.getEventManager().getEvents()) {
					needAdd = true;
					for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridExtradata
							.getEventManager().getEvents()) {
						if (ev.getId1().equals(evOld.getId1())
								&& ev.getId2().equals(evOld.getId2())
								&& (ev.getInteractionType() == evOld.getInteractionType())) {
							needAdd = false;
							break;
						}
					}
					if (needAdd) {
						gridExtradata.getEventManager().getEvents().add(ev);
					}
				}

			} catch (SerializationException e) {
				MessageBox.showSimpleMessage("afterHttpPostFromPlugin", AppCurrContext
						.getInstance().getInternationalizedMessages().jsGridDeserializationError()
						+ " LiveGridExtradata: " + e.getMessage());
			}
		}

		afterUpdateGrid();
	}

	public void pluginAfterClick(final String recId, final String colId,
			final String aStringSelectedRecordIds) {

		stringSelectedRecordIds = aStringSelectedRecordIds;

		if (gridMetadata.getUISettings().isSingleClickBeforeDoubleClick()) {
			handleClick(recId, colId, InteractionType.SINGLE_CLICK);
		} else {

			doubleClick = false;

			if (clickTimer != null) {
				clickTimer.cancel();
			}

			clickTimer = new Timer() {
				@Override
				public void run() {
					if (!doubleClick) {
						handleClick(recId, colId, InteractionType.SINGLE_CLICK);
					}
				}
			};
			clickTimer.schedule(gridMetadata.getUISettings().getDoubleClickTime());

		}

	}

	public void pluginAfterDoubleClick(final String recId, final String colId,
			final String aStringSelectedRecordIds) {
		stringSelectedRecordIds = aStringSelectedRecordIds;

		doubleClick = true;

		handleClick(recId, colId, InteractionType.DOUBLE_CLICK);
	}

	private void handleClick(final String recId, final String colId,
			final InteractionType interactionType) {

		saveCurrentClickSelection(recId, colId);

		if (gridMetadata.getUISettings().isSelectOnlyRecords()) {
			selectedRecordsChanged();
		}

		hpToolbar.setHeight(String.valueOf(hpToolbar.getOffsetHeight()) + "px");
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
				processSelectionRecords();
			}
		};
		selectionTimer.schedule(Constants.GRID_SELECTION_DELAY);

		saveCurrentCheckBoxSelection();
	}

	private void processSelectionRecords() {
		List<String> selectedRecordIds = new ArrayList<String>();

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(",");
			for (String s : strs) {
				selectedRecordIds.add(s);
			}
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
			// // p.clear();
			// // p.add(cpGrid);

			needRestoreAfterShowLoadingMessage = false;

			// cpGrid.setEnabled(true);
		}

		if (isFirstLoading) {
			resetSelection();

			resetGridSettingsToCurrent();

			hpToolbar.setHeight(String.valueOf(hpToolbar.getOffsetHeight()) + "px");
			toolBarHelper.fillToolBar();

			runAction(gridExtradata.getActionForDependentElements());

		}

		setupTimer();

		setFirstLoading(false);

	}

	private String getColumnStyle(final LiveGridColumnConfig egcc) {
		String style = "";

		style = style + "width:" + egcc.getWidth() + "px;";

		if (egcc.getHorizontalAlignment() != null) {
			style =
				style + "text-align:" + egcc.getHorizontalAlignment().toString().toLowerCase()
						+ ";";
		}

		return style;
	}

	private String getCommonColumnStyle() {
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

		ColumnSet cs = gridMetadata.getOriginalColumnSet();
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
		// selectionModel.deselectAll();
		stringSelectedRecordIds = null;
		if (localContext == null) {
			return;
		}
		localContext.getSelectedRecordIds().clear();
		localContext.setCurrentColumnId(null);
		localContext.setCurrentRecordId(null);
	}

	private void saveCurrentCheckBoxSelection() {
		localContext.getSelectedRecordIds().clear();

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(",");
			for (String s : strs) {
				localContext.getSelectedRecordIds().add(s);
			}
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

	protected void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);

		Cell selected = getStoredRecordId();
		saveCurrentClickSelection(selected.recId, selected.colId);

		stringSelectedRecordIds = selected.recId;
		saveCurrentCheckBoxSelection();
	}

	/**
	 * Экспорт в Excel.
	 * 
	 * @param exportType
	 *            GridToExcelExportType
	 */
	public void exportToExcel(final Widget wFrom, final GridToExcelExportType exportType) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(AppCurrContext.getInstance().getInternationalizedMessages()
				.grid_error_caption_export_excel());
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			SerializationStreamFactory ssfExcel = dh.getAddObjectSerializer();

			dh.addParam(getDetailedContext().getClass().getName(), getDetailedContext()
					.toParamForHttpPost(getObjectSerializer()));
			dh.addParam(DataPanelElementInfo.class.getName(),
					getElementInfo().toParamForHttpPost(getObjectSerializer()));

			dh.addParam(gridMetadata.getOriginalColumnSet().getClass().getName(), gridMetadata
					.getOriginalColumnSet().toParamForHttpPost(ssfExcel));

			dh.submit();

			mp.hide();
			mp.show(wFrom);

		} catch (SerializationException e) {
			mp.hide();
			MessageBox.showSimpleMessage(AppCurrContext.getInstance()
					.getInternationalizedMessages().grid_error_caption_export_excel(),
					e.getMessage());
		}
	}

	/**
	 * Передача в буфер обмена.
	 * 
	 * @return ClipboardDialog
	 * 
	 */
	public ClipboardDialog copyToClipboard() {
		String params = "'" + getDivIdPlugin() + "'";
		String s = pluginClipboard(gridMetadata.getJSInfo().getClipboardProc(), params);

		ClipboardDialog cd = new ClipboardDialog(s);
		cd.center();
		return cd;
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = localContext;
		if (result == null) {
			result = GridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
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
			exportToExcelCurrentPage.setTitle(AppCurrContext.getInstance()
					.getInternationalizedMessages().grid_caption_export_to_excel_current_page());
			exportToExcelCurrentPage.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					exportToExcel(exportToExcelCurrentPage, GridToExcelExportType.CURRENTPAGE);
				}
			});
			toolBar.add(exportToExcelCurrentPage);
		}
		if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(AppCurrContext.getInstance().getInternationalizedMessages()
					.grid_caption_export_to_excel_all());
			exportToExcelAll.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					exportToExcel(exportToExcelAll, GridToExcelExportType.ALL);
				}
			});
			toolBar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(AppCurrContext.getInstance().getInternationalizedMessages()
					.grid_caption_copy_to_clipboard());
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
			final JSLiveGridPluginPanel liveGridPanel = this;
			this.toolBarHelper = new ToolBarHelper(dataService, this) {

				@Override
				public void addStaticItemToToolBar(final ToolBar toolBar) {
					liveGridPanel.addStaticItemToToolBar(toolBar);
				}

				@Override
				public void runAction(final Action ac) {
					liveGridPanel.runAction(ac);
				}
			};
		}
		return this.toolBarHelper;
	}

}