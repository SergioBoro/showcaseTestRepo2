package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.sp.RecordSetElementRawData;

/**
 * Шлюз для получения данных и настроек элемента lyra-grid, где источником
 * данных является celesta скрипт.
 */
public class LyraGridCelestaGateway implements LyraGridGateway {

	@Override
	public RecordSetElementRawData getRawData(final GridContext context,
			final DataPanelElementInfo element) {

		return null;
	}

	@Override
	public RecordSetElementRawData getRawSettings(final GridContext context,
			final DataPanelElementInfo element) {

		return null;
	}

}
