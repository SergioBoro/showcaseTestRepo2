package ru.curs.showcase.app.api.grid.toolbar;

import java.util.HashMap;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.DataServiceAsync;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Помощник загрузки и формирования панели инструментов.
 * 
 */
public class ToolBarHelper {

	// private static final String TOOLBAR_HEIGHT = "31px";
	private static final String TOOLBAR_HEIGHT = "29px";

	private static final String TOOLBAR_STYLE_AWAITING_RESPONSE = "awaiting-response";
	private static final String TOOLBAR_STYLE_READY = "ready";

	private Timer toolBarRefreshTimer = null;
	private final DataServiceAsync dataService;
	private final SimplePanel panel;
	private final JSBaseGridPluginPanel jsBaseGridPluginPanel;
	private boolean isStaticToolBar = false;
	private boolean needStaticItems;
	private final boolean needAdjustToolBarWidth = true;

	private int blinkingCount = 0;
	private boolean blinkingStartTimer = false;

	private final HashMap<String, Action> actions = new HashMap<String, Action>();

	/**
	 * 
	 * @param oDataService
	 *            - имплементация DataService.
	 * @param oPanel
	 *            - панель на которую помещается ToolBar.
	 */
	public ToolBarHelper(final DataServiceAsync aDataService,
			final JSBaseGridPluginPanel aJSBaseGridPluginPanel) {
		dataService = aDataService;
		jsBaseGridPluginPanel = aJSBaseGridPluginPanel;

		panel = new SimplePanel();

		String toolbarClassName =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarClassName();
		String toolbarStyle =
			jsBaseGridPluginPanel.getGridMetadata().getUISettings().getToolbarStyle();
		if ((toolbarClassName == null) && (toolbarStyle == null)) {
			panel.setHeight(TOOLBAR_HEIGHT);
		} else {
			if (toolbarClassName != null) {
				panel.addStyleName(toolbarClassName);
			}
			if (toolbarStyle != null) {
				panel.getElement().setAttribute("style", toolbarStyle);
			}
		}

	}

	/**
	 * Обновлении панели инструментов. Очистка текущей панели и формирование
	 * панели инструментов на основе полученных метаданных.
	 */
	public void fillToolBar() {
		if ((!panel.getParent().isVisible()) || isStaticToolBar) {
			return;
		}

		if (panel.getWidget() != null) {
			panel.getWidget().removeStyleName(TOOLBAR_STYLE_READY);
			panel.getWidget().addStyleName(TOOLBAR_STYLE_AWAITING_RESPONSE);
			// panel.getWidget().setStyleName(TOOLBAR_STYLE_AWAITING_RESPONSE);
			// panel.getWidget().setStylePrimaryName(TOOLBAR_STYLE_AWAITING_RESPONSE);
		}

		final DataPanelElementInfo elInfo = jsBaseGridPluginPanel.getElementInfo();
		if (elInfo.isToolBarProc()) {

			blinkingStartTimer = true;

			if (toolBarRefreshTimer != null) {
				toolBarRefreshTimer.cancel();
			}

			toolBarRefreshTimer = new Timer() {
				@Override
				public void run() {

					blinkingStartTimer = false;
					blinkingCount++;

					CompositeContext context = jsBaseGridPluginPanel.getContextForJSToolbar();

					dataService.getGridToolBar(context, elInfo,
							new GWTServiceCallback<GridToolBar>(
									"при получении данных панели инструментов грида с сервера") {

								@Override
								public void onSuccess(final GridToolBar result) {

									createJSToolBar(result);

									blinkingCount--;

									panel.getWidget().addStyleName(TOOLBAR_STYLE_READY);
									// panel.getWidget().setStyleName(TOOLBAR_STYLE_READY);
									// panel.getWidget().setStylePrimaryName(TOOLBAR_STYLE_READY);

									Scheduler.get().scheduleDeferred(new Command() {
										@Override
										public void execute() {
											DOM.getElementById("showcaseReady").setAttribute(
													"isReady", "true");
										}
									});
								}
							});
				}
			};
			toolBarRefreshTimer.schedule(Constants.GRID_SELECTION_DELAY);
		} else {
			isStaticToolBar = true;

			createJSToolBar(null);

			panel.getWidget().addStyleName(TOOLBAR_STYLE_READY);
			// panel.getWidget().setStyleName(TOOLBAR_STYLE_READY);
			// panel.getWidget().setStylePrimaryName(TOOLBAR_STYLE_READY);

		}
	}

	// CHECKSTYLE:OFF
	private void createJSToolBar(final GridToolBar gridToolBar) {

		String htmlForPlugin = "<div id='" + jsBaseGridPluginPanel.getDivIdToolBar() + "'></div>";

		HTML pluginHTML = new HTML(htmlForPlugin);

		// if (needAdjustToolBarWidth) {
		// needAdjustToolBarWidth = false;
		// panel.setWidth(String.valueOf(panel.getOffsetWidth() + 2) + "px");
		// }

		panel.clear();
		panel.add(pluginHTML);

		JSONObject metadata = new JSONObject();

		JSONObject common = new JSONObject();
		if (gridToolBar != null) {
			if (gridToolBar.getStyle() != null) {
				common.put("style", new JSONString(gridToolBar.getStyle()));
			}
			if (gridToolBar.getClassName() != null) {
				common.put("className", new JSONString(gridToolBar.getClassName()));
			}
		}
		metadata.put("common", common);

		String params =
			"'" + jsBaseGridPluginPanel.getElementInfo().getId().toString() + "'" + ", '"
					+ jsBaseGridPluginPanel.getDivIdToolBar() + "'";

		needStaticItems = true;
		if (gridToolBar != null) {
			JSONObject jsonDynamicItems = new JSONObject();
			int id = 0;
			for (AbstractToolBarItem obj : gridToolBar.getItems()) {

				id++;

				JSONObject jsonItem = new JSONObject();
				jsonItem.put("id", new JSONString(String.valueOf(id)));

				if (obj instanceof ToolBarItem) {
					final ToolBarItem item = (ToolBarItem) obj;

					if (!item.isVisible()) {
						continue;
					}

					jsonItem.put("type", new JSONString(String.valueOf("item")));
					createJSBaseItem(jsonItem, item);

					actions.put(String.valueOf(id), item.getAction());

				} else if (obj instanceof ToolBarGroup) {
					ToolBarGroup group = (ToolBarGroup) obj;

					if (!group.isVisible()) {
						continue;
					}

					jsonItem.put("type", new JSONString(String.valueOf("group")));
					createJSBaseItem(jsonItem, group);

					int id2;
					int id2Inc = 0;
					for (AbstractToolBarItem item : group.getItems()) {
						id2Inc++;
						id2 = 100 * id + id2Inc;

						JSONObject jsonItem2 = new JSONObject();
						jsonItem2.put("id", new JSONString(String.valueOf(id2)));

						if (item instanceof ToolBarItem) {
							final ToolBarItem item2 = (ToolBarItem) item;

							if (!item2.isVisible()) {
								continue;
							}

							jsonItem2.put("type", new JSONString(String.valueOf("item")));
							createJSBaseItem(jsonItem2, item2);

							actions.put(String.valueOf(id2), item2.getAction());

						} else if (item instanceof ToolBarSeparator) {
							createJSSeparator(jsonItem2);
						}

						jsonItem.put(String.valueOf(id2), jsonItem2);

					}

				} else if (obj instanceof ToolBarSeparator) {
					createJSSeparator(jsonItem);
				}

				jsonDynamicItems.put(String.valueOf(id), jsonItem);

			}
			metadata.put("dynamicItems", jsonDynamicItems);
		}

		if (needStaticItems) {
			JSONObject jsonStaticItems = new JSONObject();
			jsBaseGridPluginPanel.addStaticItemToJSToolBar(jsonStaticItems);
			metadata.put("staticItems", jsonStaticItems);
		}

		params = params + ", " + metadata;

		runToolBar(params);

	}

	// CHECKSTYLE:ON

	private void createJSBaseItem(final JSONObject jsonItem, final BaseToolBarItem item) {
		jsonItem.put("disable", new JSONString(String.valueOf(item.isDisable())));
		jsonItem.put("text", new JSONString(item.getText()));
		jsonItem.put("hint", new JSONString(item.getHint()));
		jsonItem.put("style", new JSONString(item.getStyle()));
		jsonItem.put("className", new JSONString(item.getClassName()));
		jsonItem.put("iconClassName", new JSONString(item.getIconClassName()));

		if (item.getId() != null) {
			jsonItem.put("id", new JSONString(item.getId()));
			needStaticItems = false;
		}

		if (item.getPopupText() != null) {
			jsonItem.put("popupText", new JSONString(item.getPopupText()));
		}

		if (item instanceof ToolBarItem) {
			final ToolBarItem toolBarItem = (ToolBarItem) item;
			Action ac = toolBarItem.getAction();
			if ((ac != null) && (ac.containsServerActivity())) {
				jsonItem.put("needEnableDisableState", new JSONString(String.valueOf(true)));
			}
		}

	}

	private void createJSSeparator(final JSONObject jsonItem) {
		jsonItem.put("type", new JSONString(String.valueOf("separator")));
	}

	private native void runToolBar(final String params) /*-{

		try {
			$wnd.eval("createGridToolBar(" + params + ");");
		} catch (e) {
			$wnd.safeIncludeJS("js/ui/grids/toolbar.js");
			$wnd.eval("createGridToolBar(" + params + ");");
		}

	}-*/;

	public Action getAction(final String actionId) {
		return actions.get(actionId);
	}

	public boolean needBlinking() {
		return blinkingStartTimer || (blinkingCount > 0);
	}

	public Panel getToolBarPanel() {
		return panel;
	}

}
