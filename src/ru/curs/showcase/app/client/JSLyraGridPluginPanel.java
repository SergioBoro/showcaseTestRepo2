package ru.curs.showcase.app.client;

import java.util.*;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

/**
 * Класс-адаптер панели с внешним плагином типа JSLyraGrid.
 */
public class JSLyraGridPluginPanel extends BasicElementPanelBasis {
	private static final String PROC100 = "100%";

	private static final String STRING_SELECTED_RECORD_IDS_SEPARATOR = "D13&82#9g7";

	private static final String JSGRID_DESERIALIZATION_ERROR = "jsGridDeserializationError";

	private final VerticalPanel p = new VerticalPanel();
	private final HorizontalPanel generalHp = new HorizontalPanel();
	/**
	 * HTML виждет для плагина.
	 */
	private HTML pluginHTML = null;

	private final HorizontalPanel hpHeader = new HorizontalPanel();
	private final HorizontalPanel hpToolbar = new HorizontalPanel();
	private final HorizontalPanel hpFooter = new HorizontalPanel();

	private final MessagePopup mp = new MessagePopup(
			AppCurrContext.getInstance().getBundleMap().get("grid_message_popup_export_to_excel"));

	/**
	 * Основная фабрика для GWT сериализации.
	 */
	private SerializationStreamFactory ssf = null;
	private Timer selectionTimer = null;
	private Timer clickTimer = null;
	private boolean doubleClick = false;
	private DataServiceAsync dataService = null;
	private LyraGridContext localContext = null;

	public LyraGridContext getLocalContext() {
		return localContext;
	}

	private LyraGridMetadata gridMetadata = null;
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

	private String lyraGridSorting = "";

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
															$wnd.gwtGetHttpParamsLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtEditorGetHttpParamsLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginEditorGetHttpParams(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterLoadDataLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterLoadData(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterPartialUpdateLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterPartialUpdate(Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtAfterClickLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);															
															$wnd.gwtAfterDoubleClickLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginAfterDoubleClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtProcessFileDownloadLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginProcessFileDownload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtShowMessageLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginShowMessage(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtSetOldPositionLyra = @ru.curs.showcase.app.client.api.JSLyraGridPluginPanelCallbacksEvents::pluginSetOldPosition(Ljava/lang/String;Ljava/lang/String;);
															}-*/;

	// CHECKSTYLE:ON

	public JSLyraGridPluginPanel(final DataPanelElementInfo element) {
		setContext(null);
		setElementInfo(element);
		setFirstLoading(true);
		setCallbackJSNIFunction();
	}

	public JSLyraGridPluginPanel(final CompositeContext context,
			final DataPanelElementInfo element) {
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
			if (this.getElementInfo().getShowLoadingMessageForFirstTime()) {
				// p.add(new HTML(AppCurrContext.getInstance().getBundleMap()
				// .get("please_wait_data_are_loading")));
				p.add(new HTML("<div class=\"progress-bar\"></div>"));
			} else {
				p.add(new HTML(""));
			}

		} else {
			p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
			if (this.getElementInfo().getShowLoadingMessage()) {
				needRestoreAfterShowLoadingMessage = true;
			}
		}

		if (isFirstLoading() || isNeedResetLocalContext()) {
			localContext = null;
			setFirstLoading(true);
			setDataGridPanel();
		} else {

			setFirstLoading(false);

			if (isPartialUpdate()) {
				partialUpdateGridPanel();
			} else {
				gridMetadata.getEventManager().getEvents().clear();

				String params = "'" + getDivIdPlugin() + "'";
				pluginProc(gridMetadata.getJSInfo().getRefreshProc(), params);
			}

		}
	}

	private void partialUpdateGridPanel() {

		final LyraGridContext gc = getDetailedContext();
		gc.setPartialUpdate(true);

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getGridData(gc, getElementInfo(), new GWTServiceCallback<GridData>(
				AppCurrContext.getInstance().getBundleMap().get("gridErrorGetTable")) {

			@Override
			public void onFailure(final Throwable caught) {
				gc.setPartialUpdate(false);
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(final GridData aLiveGridData) {
				gc.setPartialUpdate(false);
				super.onSuccess(aLiveGridData);
				partialUpdateGridPanelByGrid(aLiveGridData);
			}
		});
	}

	private void partialUpdateGridPanelByGrid(final GridData aLiveGridData) {

		String params = "'" + getElementInfo().getId().toString() + "'" + ", " + "'"
				+ getDivIdPlugin() + "', " + aLiveGridData.getData();

		pluginProc(gridMetadata.getJSInfo().getPartialUpdate(), params);

	}

	private void setDataGridPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		LyraGridContext gc = getDetailedContext();

		dataService.getLyraGridMetadata(gc, getElementInfo(),
				new GWTServiceCallback<LyraGridMetadata>(
						AppCurrContext.getInstance().getBundleMap().get("gridErrorGetTable")) {

					@Override
					public void onSuccess(final LyraGridMetadata aGridMetadata) {

						super.onSuccess(aGridMetadata);

						setDataGridPanelByGrid(aGridMetadata);
					}
				});
	}

	private void setDataGridPanelByGrid(final LyraGridMetadata aGridMetadata) {
		gridMetadata = aGridMetadata;

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

		// ----------------------------------------------------

		final String div = "<div id='";
		final String htmlForPlugin = div + getDivIdPlugin() + "' style='width:"
				+ gridMetadata.getUISettings().getGridWidth() + "; height:"
				+ gridMetadata.getUISettings().getGridHeight() + "px'></div>";

		// ----------------------------------------------------

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

			// for (String param : gridMetadata.getJSInfo().getRequiredJS()) {
			// AccessToDomModel.addScriptLink(param);
			// }

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
		common.put("pageNumber",
				new JSONString(String.valueOf(gridMetadata.getLiveInfo().getPageNumber())));

		if (gridMetadata.getUISettings().isVisiblePager()) {
			common.put("isVisiblePager", new JSONString("true"));
		}

		if (gridMetadata.getUISettings().isVisibleColumnsHeader()) {
			common.put("isVisibleColumnsHeader", new JSONString("true"));
		}

		if (gridMetadata.getUISettings().isAllowTextSelection()) {
			common.put("isAllowTextSelection", new JSONString("true"));
		}

		common.put("loadingMessage", new JSONString(
				AppCurrContext.getInstance().getBundleMap().get("jsGridLoadingMessage")));

		common.put("noDataMessage", new JSONString(
				AppCurrContext.getInstance().getBundleMap().get("jsGridNoRecordsMessage")));

		common.put("stringSelectedRecordIdsSeparator",
				new JSONString(STRING_SELECTED_RECORD_IDS_SEPARATOR));

		if (gridMetadata.getUISettings().getHaColumnHeader() != null) {
			common.put("haColumnHeader", new JSONString(
					gridMetadata.getUISettings().getHaColumnHeader().toString().toLowerCase()));
		}

		if ((getElementInfo().getProcByType(DataPanelElementProcType.ADDRECORD) == null)
				&& (getElementInfo().getProcByType(DataPanelElementProcType.SAVE) == null)) {
			common.put("readonly", new JSONString("true"));
		}

		if ((gridMetadata.getGridSorting() != null)
				&& (gridMetadata.getGridSorting().getSortColId() != null)) {
			common.put("sortColId", new JSONString(gridMetadata.getGridSorting().getSortColId()));
			common.put("sortColDirection", new JSONString(
					gridMetadata.getGridSorting().getSortColDirection().toString()));
		}

		common.put("pagingLinks", new JSONString(
				String.valueOf(gridMetadata.getUISettings().getPagesButtonCount())));

		if (gridMetadata.getLyraGridSorting() != null) {
			common.put("lyraGridSorting", new JSONString(gridMetadata.getLyraGridSorting()));
			lyraGridSorting = gridMetadata.getLyraGridSorting();
		}

		metadata.put("common", common);

		JSONObject columns = new JSONObject();
		for (final GridColumnConfig egcc : gridMetadata.getColumns()) {

			if (!egcc.isVisible()) {
				continue;
			}

			JSONObject column = new JSONObject();
			column.put("id", new JSONString(egcc.getId()));
			if (egcc.getParentId() != null) {
				column.put("parentId", new JSONString(egcc.getParentId()));
			}
			column.put("caption", new JSONString(egcc.getCaption()));

			if (egcc.isReadonly()) {
				column.put("readonly", new JSONString(String.valueOf("true")));
			}

			String valueType = "";
			if (egcc.getValueType() != null) {
				valueType = egcc.getValueType().toString();
			}
			column.put("valueType", new JSONString(valueType));

			column.put("editor", new JSONString(getColumnEditor(egcc)));

			column.put("style", new JSONString(getCommonColumnStyle() + getColumnStyle(egcc)));

			column.put("urlImageFileDownload",
					new JSONString(gridMetadata.getUISettings().getUrlImageFileDownload()));

			columns.put(egcc.getId(), column);

			if (((LyraGridColumnConfig) egcc).isSortingAvailable()) {
				column.put("sortingAvailable", new JSONString(String.valueOf("true")));
			}

		}
		metadata.put("columns", columns);

		if (gridMetadata.getVirtualColumns() != null) {
			JSONObject virtualColumns = new JSONObject();
			for (final VirtualColumn vc : gridMetadata.getVirtualColumns()) {
				JSONObject virtualColumn = new JSONObject();
				virtualColumn.put("id", new JSONString(vc.getId()));
				if (vc.getParentId() != null) {
					virtualColumn.put("parentId", new JSONString(vc.getParentId()));
				}
				if (vc.getWidth() != null) {
					virtualColumn.put("width", new JSONString(vc.getWidth()));
				}
				if (vc.getStyle() != null) {
					virtualColumn.put("style", new JSONString(vc.getStyle()));
				}
				virtualColumn.put("virtualColumnType",
						new JSONString(vc.getVirtualColumnType().toString()));

				virtualColumns.put(vc.getId(), virtualColumn);
			}
			metadata.put("virtualColumns", virtualColumns);
		}

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

		generalHp.clear();
		p.clear();
		p.add(hpHeader);
		// ----------------------------------------

		ToolBarHelper toolBarHelper = getToolBarHelper();

		if (gridMetadata.getUISettings().getGridWidth().contains("px")) {
			int ind = gridMetadata.getUISettings().getGridWidth().indexOf("px");
			String str = gridMetadata.getUISettings().getGridWidth().substring(0, ind).trim();
			int number = Integer.parseInt(str);
			number = number + 2;

			hpToolbar.setWidth(number + "px");
			toolBarHelper.getToolBarPanel().setWidth(number + "px");
		} else {
			hpToolbar.setWidth(gridMetadata.getUISettings().getGridWidth());
		}

		generalHp.setWidth("100%");
		hpToolbar.add(toolBarHelper.getToolBarPanel());
		p.add(hpToolbar);
		hpToolbar.setVisible(gridMetadata.getUISettings().isVisibleToolBar());

		// ----------------------------------------
		p.add(generalHp);
		generalHp.add(pluginHTML);
		p.add(hpFooter);

		// ----------------------------------------

		try {

			runGrid(gridMetadata.getJSInfo().getCreateProc(), params,
					gridMetadata.getJSInfo().getRequiredJS().toArray());
		} catch (JavaScriptException e) {
			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(
						AppCurrContext.getInstance().getBundleMap()
								.get("error_of_plugin_painting"),
						e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
						GeneralException.getMessageType(e.getCause()),
						GeneralException.needDetailedInfo(e.getCause()), null);
			} else {
				MessageBox.showSimpleMessage(AppCurrContext.getInstance().getBundleMap()
						.get("error_of_plugin_painting"), e.getMessage());
			}
		}

	}

	// CHECKSTYLE:ON

	private native void runGrid(final String procName, final String params,
			final Object[] list) /*-{
		if (list != null) {
			for (var x = 0; x < list.length; x++) {
				$wnd.safeIncludeJS(list[x]);
			}
		}
		$wnd.eval(procName + "(" + params + ");");
	}-*/;

	/**
	 * 
	 * Вызов процедуры в плагине.
	 * 
	 * @param procName
	 *            - имя js - процедуры
	 * @param params
	 *            - параметры js - процедуры
	 * 
	 */
	private native String pluginProc(final String procName, final String params) /*-{
		return $wnd.eval(procName + "(" + params + ");");
	}-*/;

	public void pluginProcessFileDownload(final String recId, final String colId) {
		String colLinkId = null;
		for (GridColumnConfig lgcc : gridMetadata.getColumns()) {
			if (colId.equals(lgcc.getId())) {
				colLinkId = lgcc.getLinkId();
				break;
			}
		}

		if (colLinkId != null) {
			DownloadHelper dh = DownloadHelper.getInstance();
			dh.setEncoding(FormPanel.ENCODING_URLENCODED);
			dh.clear();

			dh.setErrorCaption(AppCurrContext.getInstance().getBundleMap()
					.get("grid_error_caption_file_download"));
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridFileDownload");

			try {
				dh.addParam("linkId", colLinkId);

				dh.addParam(getContext().getClass().getName(),
						getContext().toParamForHttpPost(getObjectSerializer()));
				dh.addParam(DataPanelElementInfo.class.getName(),
						getElementInfo().toParamForHttpPost(getObjectSerializer()));

				dh.addParam("recordId", recId);

				dh.submit();
			} catch (SerializationException e) {
				ru.curs.showcase.app.client.MessageBox.showSimpleMessage(AppCurrContext
						.getInstance().getBundleMap().get("grid_error_caption_file_download"),
						e.getMessage());
			}
		}
	}

	public void pluginSetOldPosition(final String oldPosition) {
		LyraGridContext gridContext = getDetailedContext();
		// gridContext.setDgridOldPosition(Integer.parseInt(oldPosition));
		gridContext.getLiveInfo().setOffset(Integer.parseInt(oldPosition));
	}

	public JSONObject pluginGetHttpParams(final int offset, final int limit,
			final String sortColId, final String sortColDir, final String refreshId) {

		LyraGridContext gridContext = getDetailedContext();
		gridContext.setDgridOldPosition(gridContext.getLiveInfo().getOffset());
		gridContext.getLiveInfo().setOffset(offset);
		gridContext.getLiveInfo().setLimit(limit);

		gridContext.setRefreshId(refreshId);

		if ((sortColId != null) && (sortColDir != null)) {
			GridSorting gs = new GridSorting(sortColId, Sorting.valueOf(sortColDir));
			gridContext.setGridSorting(gs);

			String newLyraGridSorting = "\"" + gs.getSortColId() + "\"";
			if (gs.getSortColDirection() == Sorting.DESC) {
				newLyraGridSorting = newLyraGridSorting + " desc";
			}

			if (!newLyraGridSorting
					.equalsIgnoreCase(lyraGridSorting.toLowerCase().replace(" asc", ""))) {
				lyraGridSorting = newLyraGridSorting;
				gridContext.setSortingChanged(true);
				gridContext.setDgridOldPosition(0);
			}
		}

		if (isFirstLoading()) {
			gridContext.setExternalSortingOrFilteringChanged(true);
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
					AppCurrContext.getInstance().getBundleMap().get("jsGridSerializationError")));
		} finally {
			gridContext.setRefreshId(null);
			gridContext.setSortingChanged(false);
			gridContext.setExternalSortingOrFilteringChanged(false);
		}

		return params;
	}

	public JSONObject pluginEditorGetHttpParams(final String data, final String editorType) {

		LyraGridContext gridContext = getDetailedContext();
		if ("save".equalsIgnoreCase(editorType)) {
			JSONObject column = new JSONObject();
			int i = 1;
			for (final GridColumnConfig egcc : gridMetadata.getColumns()) {
				column.put("col" + String.valueOf(i), new JSONString(egcc.getCaption()));
				i++;
			}
			String json =
				"{\"savedata\":{\"data\":" + data + ", \"columns\":" + column.toString() + "}}";
			gridContext.setEditorData(json);

			gridContext.setAddRecordData(null);
		} else {
			gridContext.setEditorData(null);

			String json = "{\"addrecorddata\":{\"currentRecordId\":\""
					+ gridContext.getCurrentRecordId() + "\"}}";
			gridContext.setAddRecordData(json);
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
					AppCurrContext.getInstance().getBundleMap().get("jsGridSerializationError")));
		}

		return params;
	}

	public void pluginAfterLoadData(final String stringEvents, final String totalCount) {

		if (stringEvents != null) {
			try {

				@SuppressWarnings("unchecked")
				List<GridEvent> eventsNew = (List<GridEvent>) getObjectSerializer()
						.createStreamReader(stringEvents).readObject();

				List<GridEvent> eventsAdd;
				if (gridMetadata.getEventManager().getEvents().size() == 0) {
					eventsAdd = eventsNew;
				} else {
					eventsAdd = new ArrayList<GridEvent>();

					boolean needAdd;
					for (ru.curs.showcase.app.api.grid.GridEvent ev : eventsNew) {
						needAdd = true;
						for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridMetadata
								.getEventManager().getEvents()) {
							if (ev.extEquals(evOld)) {
								needAdd = false;
								break;
							}
						}
						if (needAdd) {
							eventsAdd.add(ev);
						}
					}
				}
				gridMetadata.getEventManager().getEvents().addAll(eventsAdd);

			} catch (SerializationException e) {
				MessageBox
						.showSimpleMessage("afterHttpPostFromPlugin",
								AppCurrContext.getInstance().getBundleMap()
										.get(JSGRID_DESERIALIZATION_ERROR) + " Events: "
										+ e.getMessage());
			}
		}

		afterUpdateGrid();

		LyraGridContext gridContext = getDetailedContext();
		gridContext.getLiveInfo().setTotalCount(Integer.parseInt(totalCount));

	}

	public void pluginAfterPartialUpdate(final String stringEvents) {

		if (stringEvents != null) {
			try {

				@SuppressWarnings("unchecked")
				List<GridEvent> eventsNew = (List<GridEvent>) getObjectSerializer()
						.createStreamReader(stringEvents).readObject();

				for (ru.curs.showcase.app.api.grid.GridEvent ev : eventsNew) {
					for (ru.curs.showcase.app.api.grid.GridEvent evOld : gridMetadata
							.getEventManager().getEvents()) {
						if (ev.extEquals(evOld)) {
							gridMetadata.getEventManager().getEvents().remove(evOld);
							break;
						}
					}
					gridMetadata.getEventManager().getEvents().add(ev);
				}

			} catch (SerializationException e) {
				MessageBox
						.showSimpleMessage("afterHttpPostFromPlugin",
								AppCurrContext.getInstance().getBundleMap()
										.get(JSGRID_DESERIALIZATION_ERROR) + " Events: "
										+ e.getMessage());
			}
		}

	}

	public void pluginShowMessage(final String stringMessage, final String editorType) {

		if (!stringMessage.isEmpty()) {
			try {
				UserMessage um = (UserMessage) getObjectSerializer()
						.createStreamReader(stringMessage).readObject();
				if (um != null) {

					String textMessage = um.getText();
					if ((textMessage == null) || textMessage.isEmpty()) {
						return;
					}

					MessageType typeMessage = um.getType();
					if (typeMessage == null) {
						typeMessage = MessageType.INFO;
					}

					String captionMessage = um.getCaption();
					if (captionMessage == null) {
						captionMessage =
							AppCurrContext.getInstance().getBundleMap().get("okMessage");
					}

					String subtypeMessage = um.getSubtype();

					MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
							false, subtypeMessage);

				}

			} catch (SerializationException e) {
				MessageBox
						.showSimpleMessage("pluginShowMessage",
								AppCurrContext.getInstance().getBundleMap()
										.get(JSGRID_DESERIALIZATION_ERROR) + " UserMessage: "
										+ e.getMessage());
			}
		}

		// MessageBox.showSimpleMessage(gridContext.getCurrentColumnId(),
		// gridContext.getCurrentRecordId());

		LyraGridContext gridContext = getDetailedContext();
		gridContext.setEditorData(null);

		InteractionType it;
		if ("save".equalsIgnoreCase(editorType)) {
			it = InteractionType.SAVE_DATA;
		} else {
			it = InteractionType.ADD_RECORD;
		}

		processClick(gridContext.getCurrentRecordId(), gridContext.getCurrentColumnId(), it);

	}

	public void pluginAfterClick(final String recId, final String colId,
			final String aStringSelectedRecordIds) {
		stringSelectedRecordIds = aStringSelectedRecordIds;

		if (gridMetadata.getUISettings().isSingleClickBeforeDoubleClick()) {
			handleClick(recId, colId, InteractionType.SINGLE_CLICK);
		} else {

			if (clickTimer != null) {
				clickTimer.cancel();
			}

			clickTimer = new Timer() {
				@Override
				public void run() {
					if (!doubleClick) {
						handleClick(recId, colId, InteractionType.SINGLE_CLICK);
					}
					doubleClick = false;
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
			gridMetadata.getEventManager().getEventForCell(rowId, colId, interactionType);

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

		if (stringSelectedRecordIds != null) {
			String[] strs = stringSelectedRecordIds.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
			for (String s : strs) {
				selectedRecordIds.add(s);
			}
		}

		Action ac = gridMetadata.getEventManager()
				.getSelectionActionForDependentElements(selectedRecordIds);

		runAction(ac);
	}

	/**
	 * Замечание: сбрасывать состояние грида нужно обязательно до вызова
	 * отрисовки зависимых элементов. Иначе потеряем выделенную запись или
	 * ячейку в related!
	 * 
	 */

	private void afterUpdateGrid() {

		needRestoreAfterShowLoadingMessage = false;

		if (isFirstLoading) {

			resetSelection();

			resetGridSettingsToCurrent();

			hpToolbar.setHeight(String.valueOf(hpToolbar.getOffsetHeight()) + "px");
			toolBarHelper.fillToolBar();

			runAction(gridMetadata.getActionForDependentElements());

		}

		setupTimer();

		setFirstLoading(false);

	}

	private String getColumnStyle(final GridColumnConfig egcc) {
		String style = "";

		style = style + "width:" + egcc.getWidth() + "px;";

		if (egcc.getHorizontalAlignment() != null) {
			style = style + "text-align:" + egcc.getHorizontalAlignment().toString().toLowerCase()
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

		if ((gridMetadata.getFontSize() != null) && (gridMetadata.getFontModifiers() != null)) {
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

		if (gridMetadata.getUISettings().getDisplayMode() != ColumnValueDisplayMode.SINGLELINE) {
			style = style + "white-space:normal;";
		} else {
			style = style + "white-space:nowrap;";
		}

		return style;
	}

	private String getColumnEditor(final GridColumnConfig egcc) {

		String editor = egcc.getEditor();

		if (editor == null) {
			String columnEditor = "text";

			GridValueType valueType = egcc.getValueType();
			if (valueType.isGeneralizedString()) {
				columnEditor = "text";
			}
			if (valueType.isNumber()) {
				columnEditor = "number";
			}
			if (valueType.isDate()) {
				columnEditor = "date";
			}

			editor =
				"{editOn: has('touch') ? 'click' : 'dblclick', editor: '" + columnEditor + "'}";
		}

		return editor;
	}

	/**
	 * Локальный класс для работы с ячейкой грида в Showcase.
	 * 
	 * @author den
	 * 
	 */
	class Cell {
		private String recId = null;
		private String colId = null;
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
			String[] strs = stringSelectedRecordIds.split(STRING_SELECTED_RECORD_IDS_SEPARATOR);
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
			cell.recId = gridMetadata.getAutoSelectRecordId();
			cell.colId = gridMetadata.getAutoSelectColumnId();
		}
		return cell;
	}

	protected void resetGridSettingsToCurrent() {
		localContext = new LyraGridContext();
		localContext.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);

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
	private void exportToExcel(final Widget wFrom, final GridToExcelExportType exportType) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.setEncoding(FormPanel.ENCODING_URLENCODED);
		dh.clear();

		dh.setErrorCaption(AppCurrContext.getInstance().getBundleMap()
				.get("grid_error_caption_export_excel"));
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/gridToExcel");

		try {
			dh.addParam(exportType.getClass().getName(), exportType.toString());

			@SuppressWarnings("unused")
			SerializationStreamFactory ssfExcel = dh.getAddObjectSerializer();

			dh.addParam(getDetailedContext().getClass().getName(),
					getDetailedContext().toParamForHttpPost(getObjectSerializer()));
			dh.addParam(DataPanelElementInfo.class.getName(),
					getElementInfo().toParamForHttpPost(getObjectSerializer()));

			dh.submit();

			mp.hide();
			mp.show(wFrom);

		} catch (SerializationException e) {
			mp.hide();
			MessageBox.showSimpleMessage(AppCurrContext.getInstance().getBundleMap()
					.get("grid_error_caption_export_excel"), e.getMessage());
		}
	}

	/**
	 * Передача в буфер обмена.
	 * 
	 * @return ClipboardDialog
	 * 
	 */
	private ClipboardDialog copyToClipboard() {
		String params = "'" + getDivIdPlugin() + "'";
		String s = pluginProc(gridMetadata.getJSInfo().getClipboardProc(), params);

		ClipboardDialog cd = new ClipboardDialog(s);
		cd.center();
		return cd;
	}

	@Override
	public LyraGridContext getDetailedContext() {
		LyraGridContext result = localContext;
		if (result == null) {
			result = LyraGridContext.createFirstLoadDefault();
			result.setSubtype(DataPanelElementSubType.JS_LYRA_GRID);
		}
		result.setIsFirstLoad(isNeedResetLocalContext());
		result.applyCompositeContext(getContext());

		return result;
	}

	// CHECKSTYLE:OFF
	private void addStaticItemToToolBar(final ToolBar toolBar) {
		if (gridMetadata.getUISettings().isVisibleExportToExcelCurrentPage()) {
			final TextButton exportToExcelCurrentPage =
				new TextButton("",
						IconHelper.getImageResource(
								UriUtils.fromSafeConstant(
										Constants.GRID_IMAGE_EXPORT_TO_EXCEL_CURRENT_PAGE),
								16, 16));

			exportToExcelCurrentPage.setTitle(AppCurrContext.getInstance().getBundleMap()
					.get("grid_caption_export_to_excel_current_page"));
			exportToExcelCurrentPage.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					MessageBox.showMessageWithDetails("Информация",
							"Данный функционал временно отключен", "", MessageType.INFO, false,
							null);

					// exportToExcel(exportToExcelCurrentPage,
					// GridToExcelExportType.CURRENTPAGE);
				}
			});
			toolBar.add(exportToExcelCurrentPage);
		}
		// if (gridMetadata.getUISettings().isVisibleExportToExcelAll()) {
		// final TextButton exportToExcelAll =
		// new TextButton("", IconHelper.getImageResource(
		// UriUtils.fromSafeConstant(Constants.GRID_IMAGE_EXPORT_TO_EXCEL_ALL),
		// 16,
		// 16));
		//
		// exportToExcelAll.setTitle(AppCurrContext.getInstance().getBundleMap()
		// .get("grid_caption_export_to_excel_all"));
		// exportToExcelAll.addSelectHandler(new SelectHandler() {
		// @Override
		// public void onSelect(final SelectEvent event) {
		// exportToExcel(exportToExcelAll, GridToExcelExportType.ALL);
		// }
		// });
		// toolBar.add(exportToExcelAll);
		// }
		if (gridMetadata.getUISettings().isVisibleCopyToClipboard()) {
			final TextButton copyToClipboard = new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_COPY_TO_CLIPBOARD), 16, 16));

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

		if (gridMetadata.getUISettings().isVisibleFilter()) {

			// lyra

			// final TextButton filter =
			// new TextButton("", IconHelper.getImageResource(
			// UriUtils.fromSafeConstant(Constants.GRID_IMAGE_FILTER), 16, 16));
			// filter.setTitle(AppCurrContext.getInstance().getBundleMap().get("grid_caption_filter"));
			// final JSLyraGridPluginPanel lyraGridPanel = this;
			// filter.addSelectHandler(new SelectHandler() {
			// @Override
			// public void onSelect(final SelectEvent event) {
			// new JSFilter(lyraGridPanel);
			// }
			// });
			// toolBar.add(filter);
		}

		if (getElementInfo().getProcByType(DataPanelElementProcType.ADDRECORD) != null) {
			final TextButton addRecord = new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_ADD_RECORD), 16, 16));
			addRecord.setTitle(
					AppCurrContext.getInstance().getBundleMap().get("grid_caption_add_record"));
			addRecord.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					editorAddRecord();
				}
			});
			toolBar.add(addRecord);
		}
		if ((getElementInfo().getProcByType(DataPanelElementProcType.SAVE) != null)
				&& gridMetadata.getUISettings().isVisibleSave()) {
			final TextButton save = new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_SAVE), 16, 16));
			save.setTitle(AppCurrContext.getInstance().getBundleMap().get("grid_caption_save"));
			save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					editorSave();
				}
			});
			toolBar.add(save);
		}
		if ((getElementInfo().getProcByType(DataPanelElementProcType.SAVE) != null)
				&& gridMetadata.getUISettings().isVisibleRevert()) {
			final TextButton revert = new TextButton("", IconHelper.getImageResource(
					UriUtils.fromSafeConstant(Constants.GRID_IMAGE_REVERT), 16, 16));
			revert.setTitle(
					AppCurrContext.getInstance().getBundleMap().get("grid_caption_revert"));
			revert.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(final SelectEvent event) {
					editorRevert();
				}
			});
			toolBar.add(revert);
		}

	}

	// CHECKSTYLE:ON

	private ToolBarHelper getToolBarHelper() {
		if (this.toolBarHelper == null) {
			final JSLyraGridPluginPanel liveGridPanel = this;
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

	private void editorAddRecord() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getAddRecordProc(), params);
	}

	private void editorSave() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getSaveProc(), params);
	}

	private void editorRevert() {
		String params = "'" + getDivIdPlugin() + "'";
		pluginProc(gridMetadata.getJSInfo().getRevertProc(), params);
	}

}