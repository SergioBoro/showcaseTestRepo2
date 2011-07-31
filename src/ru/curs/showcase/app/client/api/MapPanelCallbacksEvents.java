package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.client.*;

/**
 * @author anlug
 * 
 *         Класс реализующий функции обратного вызова из карты (Map).
 * 
 */
public final class MapPanelCallbacksEvents {

	private MapPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на карте (на Map).
	 * 
	 * @param mapDivId
	 *            - Id карты (ID тэга div для карты)
	 * @param featureId
	 *            - ID нажатого объекта (области или точки) карты
	 */

	public static void mapPanelClick(final String mapDivId, final String featureId) {

		// MessageBox.showSimpleMessage("Тест карты",
		// "Сообщение вызвано при нажатии на карте "
		// + mapDivId + " из gwt кода на объекте " + featureId);

		GeoMap gm =
			((MapPanel) ActionExecuter.getElementPanelById(mapDivId.substring(0, mapDivId.length()
					- Constants.MAP_DIV_ID_SUFFIX.length()))).getMap();

		List<GeoMapEvent> events = gm.getEventManager().getEventForFeature(featureId);
		for (GeoMapEvent gmev : events) {
			AppCurrContext.getInstance().setCurrentAction(gmev.getAction());
			ActionExecuter.execAction();
		}

	}
}
