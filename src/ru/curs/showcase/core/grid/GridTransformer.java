package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.grid.*;

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

	public static Grid gridToExtGridData(final Grid grid) {

		return grid;
	}

}
