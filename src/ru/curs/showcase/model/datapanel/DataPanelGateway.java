package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.DataFile;

/**
 * Интерфейс шлюза для загрузки информации об информационных панелях.
 * 
 * @author den
 * 
 */
public interface DataPanelGateway {
	/**
	 * Функция, возвращающая поток с XML документом, описывающим панель, по его
	 * идентификатору.
	 * 
	 * @param dataPanelId
	 *            - идентификатор панели.
	 * @return - файл.
	 */
	DataFile<InputStream> getRawData(final CompositeContext context, String dataPanelId);

	DataFile<InputStream> getRawData(CompositeContext aContext);

	void close();

	void setSourceName(String name);
}
