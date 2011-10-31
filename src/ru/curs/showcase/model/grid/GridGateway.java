package ru.curs.showcase.model.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Интерфейс шлюза к уровню данных для грида. Возвращает данные и метаданные для
 * грида.
 * 
 * @author den
 * 
 */
public interface GridGateway {

	ElementRawData getRawDataAndSettings(GridContext context, DataPanelElementInfo element);

	ElementRawData getRawData(GridContext context, DataPanelElementInfo element);

	/**
	 * Возвращает файл для грида.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - идентификатор хранимой процедуры для скачивания файла
	 * @param recordId
	 *            - идентификатор записи грида для скачивания файла
	 * @return - файл.
	 */
	OutputStreamDataFile downloadFile(CompositeContext context, DataPanelElementInfo elementInfo,
			String linkId, String recordId);

}
