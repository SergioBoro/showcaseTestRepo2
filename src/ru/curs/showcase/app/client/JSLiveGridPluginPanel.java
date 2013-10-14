package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

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

	private final PushButton exportToExcelCurrentPage = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE));
	private final PushButton exportToExcelAll = new PushButton(new Image(
			Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL));
	private final PushButton copyToClipboard = new PushButton(new Image(
			Constants.GRID_IMAGE_COPY_TO_CLIPBOARD));
	private final MessagePopup mp = new MessagePopup(Constants.GRID_MESSAGE_POPUP_EXPORT_TO_EXCEL);

	/**
	 * Основная фабрика для GWT сериализации.
	 */
	private SerializationStreamFactory ssf = null;
	private Timer selectionTimer = null;
	private Timer clickTimer = null;
	private boolean doubleClick = false;
	private DataServiceAsync dataService = null;
	private GridContext localContext = null;
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
			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
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

		dataService
				.getLiveGridMetadata(gc, getElementInfo(),
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
		// // dg.setSize(PROC100, PROC100);
		hpToolbar.setHeight(PROC100);
		// dg.setWidth("95%");

		hpToolbar.setSpacing(1);
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			exportToExcelCurrentPage.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_CURRENT_PAGE);
			exportToExcelCurrentPage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(exportToExcelCurrentPage, GridToExcelExportType.CURRENTPAGE);
				}
			});
			hpToolbar.add(exportToExcelCurrentPage);
		}
		if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
			exportToExcelAll.setTitle(Constants.GRID_CAPTION_EXPORT_TO_EXCEL_ALL);
			exportToExcelAll.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					exportToExcel(exportToExcelAll, GridToExcelExportType.ALL);
				}
			});
			hpToolbar.add(exportToExcelAll);
		}
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			copyToClipboard.setTitle(Constants.GRID_CAPTION_COPY_TO_CLIPBOARD);
			copyToClipboard.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					// copyToClipboard();
				}
			});
			hpToolbar.add(copyToClipboard);
		}

		generalHp.clear();
		p.clear();
		p.add(hpHeader);
		p.add(hpToolbar);
		p.add(generalHp);
		generalHp.add(pluginHTML);
		p.add(hpFooter);

		// ----------------------------------------

		// p.clear();
		// generalHp.clear();
		//
		// p.add(generalHp);
		// generalHp.add(pluginHTML);

		// ----------------------------------------

		try {
			drawPlugin(gridMetadata.getJSInfo().getCreateProc(), params);
		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(Constants.ERROR_OF_PLUGIN_PAINTING,
						e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
						GeneralException.getMessageType(e.getCause()),
						GeneralException.needDetailedInfo(e.getCause()));
			} else {
				MessageBox.showSimpleMessage(Constants.ERROR_OF_PLUGIN_PAINTING, e.getMessage());
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

			dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD);
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
				ru.curs.showcase.app.client.MessageBox.showSimpleMessage(
						Constants.GRID_ERROR_CAPTION_FILE_DOWNLOAD, e.getMessage());
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
			params.put("error", new JSONString(
					"Ошибка при сериализации параметров для Http-запроса плагина."));
		}

		return params;
	}

	public void pluginAfterLoadData(final String stringLiveGridExtradata) {
		try {
			LiveGridExtradata gridExtradataNew =
				(LiveGridExtradata) getObjectSerializer().createStreamReader(
						stringLiveGridExtradata).readObject();

			boolean needAdd;
			for (ru.curs.showcase.app.api.grid.GridEvent ev : gridExtradataNew.getEventManager()
					.getEvents()) {
				needAdd = true;
				for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridExtradata
						.getEventManager().getEvents()) {
					if (ev.getId1().equals(evOld.getId1()) && ev.getId2().equals(evOld.getId2())
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
			MessageBox.showSimpleMessage("afterHttpPostFromPlugin",
					"Ошибка при десериализации объекта LiveGridExtradata: " + e.getMessage());
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

	private void resetGridSettingsToCurrent() {
		localContext = new GridContext();
		localContext.setSubtype(DataPanelElementSubType.EXT_LIVE_GRID);

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
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL);
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
			MessageBox
					.showSimpleMessage(Constants.GRID_ERROR_CAPTION_EXPORT_EXCEL, e.getMessage());
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
		result.applyCompositeContext(getContext());

		result.setCurrentDatapanelWidth(GeneralDataPanel.getTabPanel().getOffsetWidth());
		result.setCurrentDatapanelHeight(GeneralDataPanel.getTabPanel().getOffsetHeight());

		return result;
	}

}