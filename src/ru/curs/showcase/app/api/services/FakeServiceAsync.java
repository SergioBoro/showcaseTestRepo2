package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.app.api.grid.LiveGridExtradata;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный компаньон FakeService.
 * 
 * @author den
 * 
 */
public interface FakeServiceAsync {

	void serializeColumnSet(ColumnSet cs, AsyncCallback<Void> callback);

	void
			serializeGeoMapExportSettings(GeoMapExportSettings settings,
					AsyncCallback<Void> callback);

	void serializeLiveGridExtradata(AsyncCallback<LiveGridExtradata> callback);

	void serializeUserMessage(AsyncCallback<UserMessage> callback);

}
