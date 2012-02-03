package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.*;

import com.extjs.gxt.ui.client.data.*;

/**
 * Класс, преобразующий Grid в ExtGrid.
 * 
 */
public final class GridTransformer {
	private GridTransformer() {
		throw new UnsupportedOperationException();
	}

	public static ExtGridMetadata gridToExtGridMetadata(final Grid grid) {

		ExtGridMetadata egm = new ExtGridMetadata();

		egm.setHeader(grid.getHeader());
		egm.setFooter(grid.getFooter());

		return egm;
	}

	public static PagingLoadResult<ExtGridData> gridToExtGridData(final Grid grid,
			final PagingLoadConfig loadConfig) {

		// return grid;

		return null;
	}

}
