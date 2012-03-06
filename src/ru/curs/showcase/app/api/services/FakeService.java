package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Основной GWT-RPC интерфейс для приложения. Основное назначение - передача
 * данных для отображения в UI.
 */
public interface FakeService extends RemoteService {

	/**
	 * Fake функция для того, чтобы заработала сериализация GWT для класса
	 * ColumnSet. Не удалять!
	 * 
	 * @param cs
	 *            - набор столбцов.
	 * @throws GeneralException
	 */
	void saveColumnSet(ColumnSet cs, GeoMapExportSettings settings);
}
