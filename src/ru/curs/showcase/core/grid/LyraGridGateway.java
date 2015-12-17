package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.sp.*;

/**
 * Интерфейс шлюза к уровню данных для лиры-грид.
 */
public interface LyraGridGateway extends RecordSetElementGateway<GridContext> {

	RecordSetElementRawData getRawSettings(GridContext context, DataPanelElementInfo element);

}
