package ru.curs.showcase.model.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.ElementRawData;

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
}
