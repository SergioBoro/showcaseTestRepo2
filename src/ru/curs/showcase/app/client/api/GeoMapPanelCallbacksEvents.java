package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.google.gwt.regexp.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FormPanel;

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

		GeoMap gm = getPanel(mapDivId).getMap();

		List<GeoMapEvent> events = gm.getEventManager().getEventForFeature(featureId);
		for (GeoMapEvent gmev : events) {
			AppCurrContext.getInstance().setCurrentActionFromElement(gmev.getAction(), gm);
			ActionExecuter.execAction();
		}
	}

	public static GeoMapPanel getPanel(final String mapDivId) {
		String elementId = getElementIdByGeomapDivId(mapDivId);
		return (GeoMapPanel) ActionExecuter.getElementPanelById(elementId);
	}

	public static String getElementIdByGeomapDivId(final String mapDivId) {
		String elementId = null;
		RegExp pattern = RegExp.compile("dpe_.*__(.*)" + Constants.MAP_DIV_ID_SUFFIX, "i");
		MatchResult res = pattern.exec(mapDivId);
		if (res.getGroupCount() == 2) {
			elementId = res.getGroup(1);
		}
		return elementId;
	}

	/**
	 * Обязательно выставляем ENCODING_MULTIPART - иначе будут проблемы с
	 * большими картами - например, картой мира.
	 * 
	 */
	public static void exportToPNGSuccess(final String mapDivId, final String imageFormat,
			final String svg) {
		DownloadHelper dh = DownloadHelper.getInstance();
		dh.clear();

		dh.setErrorCaption(Constants.EXPORT_TO_PNG_ERROR);
		dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/geoMapExport"
				+ com.google.gwt.user.client.Window.Location.getQueryString());
		dh.setEncoding(FormPanel.ENCODING_MULTIPART);
		try {
			GeoMap map = getPanel(mapDivId).getMap();
			SerializationStreamFactory ssf = dh.getAddObjectSerializer();
			dh.addParam(map.getExportSettings().getClass().getName(), map.getExportSettings()
					.toParamForHttpPost(ssf));
			dh.addParam(ImageFormat.class.getName(), imageFormat);
			dh.addParam("svg", svg);
			dh.submit();
		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(Constants.EXPORT_TO_PNG_ERROR, e.getMessage());
		}
	}

	public static void exportToPNGError(final String mapDivId, final String error) {
		MessageBox.showSimpleMessage(Constants.EXPORT_TO_PNG_ERROR + "(djeo)", error);
	}
}
