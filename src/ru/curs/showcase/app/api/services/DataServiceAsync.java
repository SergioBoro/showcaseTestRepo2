package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный "двойник" основного GWT-RPC интерфейса приложения - DataService.
 */
public interface DataServiceAsync {

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param callback
	 *            - callback.
	 */
	void getNavigator(CompositeContext context, AsyncCallback<Navigator> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param callback
	 *            - callback.
	 * @param action
	 *            - действие.
	 */
	void getDataPanel(Action action, AsyncCallback<DataPanel> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getWebText(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<WebText> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getGrid(GridContext context, DataPanelElementInfo element, AsyncCallback<Grid> callback);

	void getExtGridMetadata(GridContext context, DataPanelElementInfo element,
			AsyncCallback<ExtGridMetadata> callback);

	void getExtGridData(GridContext context, DataPanelElementInfo element,
			AsyncCallback<PagingLoadResult<ExtGridData>> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getChart(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<Chart> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param cs
	 *            - набор столбцов.
	 * @param callback
	 *            - callback.
	 */
	void saveColumnSet(ColumnSet cs, GeoMapExportSettings settings, AsyncCallback<Void> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getGeoMap(CompositeContext context, DataPanelElementInfo element,
			AsyncCallback<GeoMap> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void getXForms(XFormContext context, DataPanelElementInfo element,
			AsyncCallback<XForm> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - текущий контекст.
	 * @param element
	 *            - описание элемента.
	 * @param callback
	 *            - callback.
	 */
	void saveXForms(XFormContext context, DataPanelElementInfo element,
			AsyncCallback<Void> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - контекст, содержащий параметры URL..
	 * @param callback
	 *            - callback.
	 */
	void getServerCurrentState(CompositeContext context, AsyncCallback<ServerState> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param action
	 *            - действие.
	 * @param callback
	 *            - callback.
	 * @throws GeneralException
	 */
	void execServerAction(Action action, AsyncCallback<Void> callback);

	/**
	 * Асинхронная версия.
	 * 
	 * @param context
	 *            - контекст, содержащий параметры URL..
	 * @param callback
	 *            - callback.
	 */
	void getMainPage(CompositeContext context, AsyncCallback<MainPage> callback);

}
