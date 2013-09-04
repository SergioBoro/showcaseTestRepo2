package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.plugin.*;
import ru.curs.showcase.app.api.services.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Загрузчик данных для плагина.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginHelper {
	/**
	 * Интерфейс, посредством которого происходит оповещает о завершении
	 * процесса получение данных.
	 */
	public interface PluginListener {
		/**
		 * Вызывается компонентой при закрытии как по кнопке ОК, так и по кнопке
		 * Отменить.
		 * 
		 * @param selector
		 *            Компонента-селектор.
		 */
		void onComplete(ResponceData responce);
	}

	private final DataServiceAsync getDataService = GWT.create(DataService.class);
	private RequestData requestData;
	private PluginListener listener;

	public GetDataPluginHelper() {

	}

	public GetDataPluginHelper(final RequestData oRequestData, final PluginListener oListener) {
		this.requestData = oRequestData;
		this.listener = oListener;
	}

	public RequestData setRequestData(final RequestData oRequestData) {
		return this.requestData = oRequestData;
	}

	public PluginListener setListener(final PluginListener oListener) {
		return this.listener = oListener;
	}

	public void getData() {
		getDataService.getPluginData(requestData, new AsyncCallback<ResponceData>() {

			@Override
			public void onFailure(final Throwable caught) {
				if (listener != null) {
					listener.onComplete(null);
				}
			}

			@Override
			public void onSuccess(final ResponceData responce) {
				if (listener != null) {
					listener.onComplete(responce);
				}
			}
		});
	}
}
