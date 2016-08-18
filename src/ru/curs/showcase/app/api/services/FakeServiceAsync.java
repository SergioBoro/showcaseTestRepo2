package ru.curs.showcase.app.api.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.app.api.grid.GridEvent;

/**
 * Асинхронный компаньон FakeService.
 * 
 * @author den
 * 
 */
public interface FakeServiceAsync {

	void serializeGeoMapExportSettings(GeoMapExportSettings settings,
			AsyncCallback<Void> callback);

	void serializeEvents(AsyncCallback<List<GridEvent>> callback);

	void serializeUserMessage(AsyncCallback<UserMessage> callback);

	void serializeThrowable(AsyncCallback<Throwable> callback);

}
