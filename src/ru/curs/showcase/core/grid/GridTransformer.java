package ru.curs.showcase.core.grid;

import java.util.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.util.TextUtils;

import com.extjs.gxt.ui.client.data.*;

/**
 * Класс, преобразующий Grid в ExtGrid.
 * 
 */
public final class GridTransformer {

	private static final int DEF_COLUMN_WIDTH = 100;

	private GridTransformer() {
		throw new UnsupportedOperationException();
	}

	public static ExtGridMetadata gridToExtGridMetadata(final Grid grid) {

		ExtGridMetadata egm = new ExtGridMetadata();

		egm.setHeader(grid.getHeader());
		egm.setFooter(grid.getFooter());

		// -------------------------------------------------------
		List<ExtGridColumnConfig> columns = new ArrayList<ExtGridColumnConfig>();

		int index = 0;
		for (Column c : grid.getDataSet().getColumnSet().getColumns()) {
			index++;
			ExtGridColumnConfig column =
				new ExtGridColumnConfig("col" + String.valueOf(index), c.getCaption(),
						getIntWidthByStringWidth(c.getWidth()));

			if (c.getValueType().isDate()) {
				column.setDateTimeFormat("yyyy MMM dd");
			}

			columns.add(column);
		}

		egm.setColumns(columns);

		// -------------------------------------------------------

		return egm;
	}

	private static Integer getIntWidthByStringWidth(final String w) {
		Integer result = DEF_COLUMN_WIDTH;
		if (w != null) {
			result = TextUtils.getIntSizeValue(w);
		}
		if (result == null) {
			result = DEF_COLUMN_WIDTH;
		}
		return result;
	}

	public static PagingLoadResult<ExtGridData> gridToExtGridData(final GridContext context,
			final Grid grid) {

		// -------------------------------------------------------

		ArrayList<ExtGridData> sublist = new ArrayList<ExtGridData>();

		for (Record rec : grid.getDataSet().getRecordSet().getRecords()) {
			ExtGridData egd = new ExtGridData();
			int index = 0;
			for (Column c : grid.getDataSet().getColumnSet().getColumns()) {
				index++;
				egd.set("col" + String.valueOf(index), rec.getValue(c));
			}
			sublist.add(egd);
		}

		// -------------------------------------------------------

		final int offset = 0;

		// Общее кол - во записей
		final int totalLength = 200;

		return new BasePagingLoadResult<ExtGridData>(sublist, offset, totalLength);

	}

}
