package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с внешним плагином.
 */
public class PluginPanel extends BasicElementPanelBasis {

	public PluginPanel(final CompositeContext context1, final DataPanelElementInfo element1) {
		this.setContext(context1);
		this.setElementInfo(element1);

		generalPluginPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();
		generalPluginPanel.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

		dataService = GWT.create(DataService.class);

		setPluginPanel();

	}

	public PluginPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		this.setElementInfo(element1);
		generalHp = new HorizontalPanel();
		this.setContext(null);

		// я бы убрал этот код-конец
		// createChildPanels();

		generalPluginPanel = new VerticalPanel();
		generalPluginPanel.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
	}

	private void setPluginPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getPlugin(getContext(), getElementInfo(), new GWTServiceCallback<Plugin>(
				Constants.ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final Plugin aPlugin) {

				plugin = aPlugin;
				if (plugin != null) {
					fillPluginPanel(aPlugin);
				}
			}
		});

	}

	/**
	 * Заполняет виджет плагина содержимым. TODO: сделать установку ширины и
	 * высоты div всегда
	 * 
	 * @param Plugin
	 *            plugin
	 */
	protected void fillPluginPanel(final Plugin aPlugin) {
		final String div = "<div id='";
		final String htmlForPlugin;
		// final int n60 = 60;
		// final int n80 = 80;
		// if (aGeoMap.getAutoSize()) {
		// final int width = GeneralDataPanel.getTabPanel().getOffsetWidth() -
		// n60;
		// final int height = GeneralDataPanel.getTabPanel().getOffsetHeight() -
		// n80;

		// htmlForMap =
		// div + getDivIdMap() + "' style = 'width: " + String.valueOf(width)
		// + "px; height: " + String.valueOf(height) + "px'></div>";
		// aGeoMap.applyAutoSizeValuesOnClient(width, height);

		// } else {

		// plugin.getSize().MessageBox.showSimpleMessage("111",
		// plugin.getSize());
		htmlForPlugin =
			div + getDivIdPlugin() + "' style='width:" + aPlugin.getSize().getWidth()
					+ "px; height:" + aPlugin.getSize().getHeight() + "px'></div>";
		// }
		// MessageBox.showSimpleMessage("456", htmlForPlugin);
		// final String htmlForMap =
		// "<div id='" + divIdMap +
		// "' style = 'width: 900px; height: 600px'></div>";

		pluginHTML = new HTML(htmlForPlugin);

		generalPluginPanel.clear();
		generalHp.clear();

		generalPluginPanel.add(generalHp);
		generalHp.add(pluginHTML);

		String params = "'" + getDivIdPlugin() + "'";
		for (String param : aPlugin.getParams()) {
			params = params + ", " + param;
		}

		if (AppCurrContext.getInstance()
				.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
				.indexOf(getDivIdPlugin()) < 0) {
			AppCurrContext.getInstance()
					.getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel()
					.add(getDivIdPlugin());
			for (String param : aPlugin.getRequiredJS()) {
				AccessToDomModel.addScriptLink(param);
			}

			for (String param : aPlugin.getRequiredCSS()) {
				AccessToDomModel.addCSSLink(param);
			}
		}

		try {
			drawPlugin(aPlugin.getCreateProc(), params);
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

		checkForDefaultAction();
		setupTimer();

	}

	/**
	 * VerticalPanel на которой отображен плагин.
	 */
	private final VerticalPanel generalPluginPanel;

	/**
	 * HorizontalPanel на которой отображен плагин.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * Plugin plugin.
	 */
	private Plugin plugin = null;

	/**
	 * @return Возвращает текущий объект типа Plugin - данные внешнего плагина.
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Устанавливает текущий объект типа Plugin - данные внешнего плагина.
	 * 
	 * @param aPlugin
	 *            - объект типа Plugin
	 */
	public void setPlugin(final Plugin aPlugin) {
		this.plugin = aPlugin;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для плагина.
	 */
	private HTML pluginHTML = null;

	/**
	 * Ф-ция, возвращающая панель с внешним плагином, если она необходима.
	 * 
	 * @return - Панель с плагином.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalPluginPanel;
	}

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
	public native void drawPlugin(final String procName, final String params) /*-{
		$wnd.eval(procName + "(" + params + ");");
	}-*/;

	@Override
	public void reDrawPanel(final CompositeContext context1) {

		this.setContext(context1);
		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		if (this.getElementInfo().getShowLoadingMessage()) {
			generalPluginPanel.clear();
			generalPluginPanel.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getPlugin(getContext(), getElementInfo(), new GWTServiceCallback<Plugin>(
				Constants.ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final Plugin aPlugin) {

				plugin = aPlugin;
				if (plugin != null) {
					fillPluginPanel(aPlugin);
					getPanel().setHeight("100%");

				}
			}
		});

	}

	@Override
	public void hidePanel() {
		generalPluginPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalPluginPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (plugin.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(
					plugin.getActionForDependentElements(), plugin);
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return plugin;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			generalPluginPanel.clear();
			generalPluginPanel.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		}
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getPlugin(getContext(), getElementInfo(), new GWTServiceCallback<Plugin>(
				Constants.ERROR_OF_PLUGIN_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final Plugin aPlugin) {

				plugin = aPlugin;
				if (plugin != null) {
					fillPluginPanel(aPlugin);
					getPanel().setHeight("100%");
				}
			}
		});

	}

	// CHECKSTYLE:OFF
	/**
	 * 
	 * Процедура определяющая функцию, которая будет выполняться по клику в
	 * Plugin.
	 * 
	 */
	public native void setCollbackJSNIFunction() /*-{
		$wnd.gwtPluginFunc = 
		@ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::pluginPanelClick(Ljava/lang/String;Ljava/lang/String;);
	}-*/;

	// CHECKSTYLE:ON

	private String getDivIdPlugin() {
		return getElementInfo().getFullId() + Constants.PLUGIN_DIV_ID_SUFFIX;
	}
}
