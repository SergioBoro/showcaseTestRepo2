package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.client.JSLiveGridPluginPanel;

import com.google.gwt.json.client.JSONObject;

/**
 * Класс, реализующий функции обратного вызова из JSLiveGridPluginPanel.
 * 
 */
public final class JSLiveGridPluginPanelCallbacksEvents {

	private JSLiveGridPluginPanelCallbacksEvents() {
	}

	/**
	 * Возвращает текущую JSLiveGridPluginPanel.
	 * 
	 * @param pluginId
	 *            - Id элемента плагина.
	 * 
	 * @return PageGridPluginPanel
	 */
	private static JSLiveGridPluginPanel getCurrentPanel(final String pluginId) {
		return (JSLiveGridPluginPanel) ActionExecuter.getElementPanelById(pluginId);
	}

	public static JSONObject pluginGetHttpParams(final String pluginId, final String offset,
			final String limit, final String sortColId, final String sortColDir) {
		return getCurrentPanel(pluginId).pluginGetHttpParams(Integer.parseInt(offset),
				Integer.parseInt(limit), sortColId, sortColDir);
	}

	public static void pluginAfterLoadData(final String pluginId,
			final String stringLiveGridExtradata) {
		getCurrentPanel(pluginId).pluginAfterLoadData(stringLiveGridExtradata);
	}

	public static void pluginAfterClick(final String pluginId, final String recId,
			final String colId, final String stringSelectedRecordIds) {
		getCurrentPanel(pluginId).pluginAfterClick(recId, colId, stringSelectedRecordIds);
	}

	public static void pluginAfterDoubleClick(final String pluginId, final String recId,
			final String colId, final String stringSelectedRecordIds) {
		getCurrentPanel(pluginId).pluginAfterDoubleClick(recId, colId, stringSelectedRecordIds);
	}

	public static void pluginProcessFileDownload(final String pluginId, final String recId,
			final String colId) {
		getCurrentPanel(pluginId).pluginProcessFileDownload(recId, colId);
	}

}