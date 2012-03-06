package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Асинхронный компаньон FakeService.
 * 
 * @author den
 * 
 */
public interface FakeServiceAsync {

	void saveColumnSet(ColumnSet cs, GeoMapExportSettings settings, AsyncCallback<Void> callback);

}
