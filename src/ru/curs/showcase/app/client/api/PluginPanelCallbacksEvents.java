package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.beta2.extra.gwt.ui.plugin.*;
import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.*;

import com.google.gwt.core.client.*;
import com.google.gwt.json.client.*;

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

	/**
	 * Статический метод для создания элемента плагина. Может быть использован
	 * непосредственно в javscript-е на форме.
	 * 
	 * @param oParams
	 *            Объект-хендлер плагина, @see {@link PluginParam)
	 */
	public static void createPlugin(final JavaScriptObject oParams) {
		final PluginParam param = (PluginParam) oParams;
		BasicElementPanel currentPanel = ActionExecuter.getElementPanelById(param.id());
		if (currentPanel != null) {
			PluginComponent pluginComponent =
				new PluginComponentImpl(currentPanel.getContext(), currentPanel.getElementInfo(),
						param);
			if (param.parentId() == null || param.parentId().isEmpty()) {
				// плагин будет отображен во всплывающем окне
				pluginComponent = new WindowPluginDecorator(pluginComponent);
			}
			try {
				pluginComponent.draw();
			} catch (JavaScriptException e) {
				if (e.getCause() != null) {
					MessageBox.showMessageWithDetails(Constants.ERROR_OF_PLUGIN_PAINTING,
							e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
							GeneralException.getMessageType(e.getCause()),
							GeneralException.needDetailedInfo(e.getCause()));
				} else {
					MessageBox.showSimpleMessage(Constants.ERROR_OF_PLUGIN_PAINTING,
							e.getMessage());
				}
			}
		}
	}

	/**
	 * Входные параметры для getData.
	 * 
	 * @author bogatov
	 * 
	 */
	private static final class GetDataPluginParam extends JavaScriptObject {

		protected GetDataPluginParam() {
		};

		/**
		 * Id элемента панели.
		 * 
		 * @return String
		 */
		native String id()/*-{
			return this.id;
		}-*/;

		/**
		 * Id элемента панели.
		 * 
		 * @return String
		 */
		native String parentId()/*-{
			return this.parentId;
		}-*/;

		/**
		 * Id элемента панели.
		 * 
		 * @return String
		 */
		native JavaScriptObject params()/*-{
			return this.params;
		}-*/;

		/**
		 * callback функция возврата данных.
		 * 
		 * @param datas
		 *            массив данных
		 */
		native void callbackFn(final JavaScriptObject datas)/*-{
			if (this.callbackFn != null) {
				this.callbackFn(datas);
			}
		}-*/;
	}

	/**
	 * Получить данные для плагина.
	 * 
	 * @param oParams
	 *            параметры, @see
	 *            {@link PluginPanelCallbacksEvents.GetDataPluginParam)
	 */
	public static void pluginGetData(final JavaScriptObject oParams) {
		if (oParams != null) {
			final GetDataPluginParam param = (GetDataPluginParam) oParams;
			BasicElementPanel currentPanel = ActionExecuter.getElementPanelById(param.id());
			if (currentPanel != null) {
				RequestData requestData = new RequestData();
				requestData.setContext(currentPanel.getContext());

				PluginInfo pluginInfo =
					(PluginInfo) currentPanel
							.getElementInfo()
							.getTab()
							.getElementInfoById(
									PluginComponent.PLUGININFO_ID_PREF + param.parentId());
				if (pluginInfo == null) {
					MessageBox.showSimpleMessage(Constants.ERROR_OF_PLUGIN_GETDATA,
							"Не найден PluginInfo");
					return;
				}

				requestData.setElInfo(pluginInfo);
				requestData.setProcName(pluginInfo.getGetDataProcName());
				if (param.params() != null) {
					JSONObject json = new JSONObject(param.params());
					addParams("param", json, requestData.getParamMap());
				}
				try {
					GetDataPluginHelper helper =
						new GetDataPluginHelper(requestData,
								new GetDataPluginHelper.PluginListener() {

									@Override
									public void onComplete(final ResponceData responce) {
										JavaScriptObject result = null;
										if (responce != null) {
											result = eval(responce.getJsonData());
										}
										param.callbackFn(result);
									}
								});
					helper.getData();
				} catch (JavaScriptException e) {
					if (e.getCause() != null) {
						MessageBox.showMessageWithDetails(Constants.ERROR_OF_PLUGIN_GETDATA,
								e.getMessage(),
								GeneralException.generateDetailedInfo(e.getCause()),
								GeneralException.getMessageType(e.getCause()),
								GeneralException.needDetailedInfo(e.getCause()));
					} else {
						MessageBox.showSimpleMessage(Constants.ERROR_OF_PLUGIN_GETDATA,
								e.getMessage());
					}
				}
			} else {
				MessageBox.showSimpleMessage(Constants.ERROR_OF_PLUGIN_GETDATA,
						"Не найден ElementPanel. Id=" + param.id());
			}
		}
	}

	private static void addParams(final String name, final JSONValue jsonVal,
			final Map<String, String> map) {
		if (jsonVal.isNull() == null) {
			if (jsonVal.isObject() != null) {
				JSONObject jsonObject = jsonVal.isObject();
				for (String key : jsonObject.keySet()) {
					addParams(key, jsonObject.get(key), map);
				}
			} else if (jsonVal.isArray() != null) {
				JSONArray jsonArray = jsonVal.isArray();
				for (int i = 0; i < jsonArray.size(); i++) {
					addParams(name, jsonArray.get(i), map);
				}
			} else if (jsonVal.isString() != null) {
				JSONString jsonString = jsonVal.isString();
				String val = jsonString.stringValue();
				map.put(name, val);
			} else {
				map.put(name, jsonVal.toString());
			}
		}
	}

	/**
	 * Преобразовать строку в JavaScriptObject.
	 * 
	 * @param jsonStr
	 *            строка в формате json
	 * @return JavaScriptObject
	 */
	public static native JavaScriptObject eval(final String jsonStr) /*-{
		return $wnd.eval('(' + jsonStr + ')');
	}-*/;
}
