package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Содержит fake функции для того, чтобы заработали "ручные вызовы" сериализации
 * GWT. Не удалять!.
 */
public interface FakeService extends RemoteService {

	void serializeColumnSet(ColumnSet cs);

	void serializeGeoMapExportSettings(GeoMapExportSettings settings);

}
