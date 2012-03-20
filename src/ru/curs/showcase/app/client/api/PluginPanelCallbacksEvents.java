package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;

/**
 * 
 */

/**
 * @author anlug Класс реализующий функции обратного вызова из PluginPanel.
 * 
 */
public final class PluginPanelCallbacksEvents {

	private PluginPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие клика на внешнем плагине (на PluginPanel).
	 * 
	 * @param pluginDivId
	 *            - Id тэга div элемента Plugin.
	 * @param eventId
	 *            - строка-идентификатор события. К примеру, это могут быть
	 *            координаты точки или название экранного элемента, по которому
	 *            кликнул пользователь.
	 * */
	public static void pluginPanelClick(final String pluginDivId, final String eventId) {

		Plugin pl =
			((PluginPanel) ActionExecuter.getElementPanelById(pluginDivId.substring(0,
					pluginDivId.length() - Constants.PLUGIN_DIV_ID_SUFFIX.length()))).getPlugin();

		List<HTMLEvent> events = pl.getEventManager().getEventForLink(eventId);
		for (HTMLEvent chev : events) {
			AppCurrContext.getInstance().setCurrentActionFromElement(chev.getAction(), pl);
			ActionExecuter.execAction();
		}
	}
}
