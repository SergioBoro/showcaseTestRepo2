package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.utils.DownloadHelper;

/**
 * @author anlug
 * 
 *         Класс реализующий функции обратного вызова из карты (Map).
 * 
 */
public final class GeoMapPanelCallbacksEvents {

	private GeoMapPanelCallbacksEvents() {

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

		GeoMap gm = (getPanel(mapDivId)).getMap();

		List<GeoMapEvent> events = gm.getEventManager().getEventForFeature(featureId);
		for (GeoMapEvent gmev : events) {
			AppCurrContext.getInstance().setCurrentAction(gmev.getAction());
			ActionExecuter.execAction();
		}
	}

	public static GeoMapPanel getPanel(final String mapDivId) {
		return (GeoMapPanel) ActionExecuter.getElementPanelById(mapDivId.substring(0,
				mapDivId.length() - Constants.MAP_DIV_ID_SUFFIX.length()));
	}

	public static void exportToPNGSuccess(final String mapDivId, final String svg) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.clear();

		dh.setErrorCaption(Constants.EXPORT_TO_PNG_ERROR);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/geoMapToPNG");

		try {
			// SerializationStreamFactory ssf = dh.getObjectSerializer();
			// dh.addStdPostParamsToBody(getDetailedContext(),
			// getElementInfo());
			dh.addParam("svg", svg);
			dh.submit();
		} catch (Exception e) {
			MessageBox.showSimpleMessage(Constants.EXPORT_TO_PNG_ERROR, e.getMessage());
		}
	}

	public static void exportToPNGError(final String mapDivId, final String error) {
		MessageBox.showSimpleMessage(Constants.EXPORT_TO_PNG_ERROR, error);
	}
}
