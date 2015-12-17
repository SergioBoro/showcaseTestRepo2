package ru.curs.showcase.app.api.services;

import java.util.List;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.app.api.grid.GridEvent;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Содержит fake функции для того, чтобы заработали "ручные вызовы" сериализации
 * GWT. Не удалять!.
 */
public interface FakeService extends RemoteService {

	void serializeGeoMapExportSettings(GeoMapExportSettings settings);

	List<GridEvent> serializeEvents();

	UserMessage serializeUserMessage();

}
