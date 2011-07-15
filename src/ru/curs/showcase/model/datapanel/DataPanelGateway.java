package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import ru.curs.showcase.model.DataFile;

/**
 * Интерфейс шлюза для загрузки информации об информационных панелях.
 * 
 * @author den
 * 
 */
public interface DataPanelGateway {
	/**
	 * Функция получения идентификатора первой вкладки панели по идентификатору
	 * панели.
	 * 
	 * @param dataPanelId
	 *            - идентификатор панели.
	 * @return - идентификатор вкладки.
	 */
	String getFirstTabId(String dataPanelId);

	/**
	 * Функция, возвращающая поток с XML документом, описывающим панель, по его
	 * идентификатору.
	 * 
	 * @param dataPanelId
	 *            - идентификатор панели.
	 * @return - файл.
	 */
	DataFile<InputStream> getRawData(String dataPanelId);

	/**
	 * Проверка на существование вкладки в файле.
	 * 
	 * @param aDataPanelId
	 *            - идентификатор панели.
	 * @param aTabValue
	 *            - значение идентификатора вкладки.
	 */
	boolean tabExists(String aDataPanelId, String aTabValue);
}
