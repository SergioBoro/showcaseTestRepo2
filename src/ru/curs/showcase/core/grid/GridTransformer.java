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

			column.setHorizontalAlignment(com.extjs.gxt.ui.client.Style.HorizontalAlignment
					.valueOf(c.getHorizontalAlignment().toString()));

			columns.add(column);
		}

		egm.setColumns(columns);

		// -------------------------------------------------------

		egm.getLiveInfo().setOffset(grid.getLiveInfo().getOffset());
		egm.getLiveInfo().setLimit(grid.getDataSet().getRecordSet().getPageSize());
		egm.getLiveInfo().setTotalCount(grid.getLiveInfo().getTotalCount());

		// -------------------------------------------------------

		egm.setOriginalColumns(grid.getDataSet().getColumnSet().getColumns());

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

	public static PagingLoadResult<ExtGridData> gridToExtGridData(final Grid grid) {

		// -------------------------------------------------------

		ArrayList<ExtGridData> sublist = new ArrayList<ExtGridData>();

		for (Record rec : grid.getDataSet().getRecordSet().getRecords()) {
			ExtGridData egd = new ExtGridData();
			int index = 0;
			for (Column c : grid.getDataSet().getColumnSet().getColumns()) {
				index++;
				String colId = "col" + String.valueOf(index);
				String val = null;

				switch (c.getValueType()) {
				case IMAGE:
					val = "<a><img border=\"0\" src=\"" + rec.getValue(c) + "\"></a>";
					break;
				default:
					val = rec.getValue(c);
					break;
				}
				egd.set(colId, val);
			}
			sublist.add(egd);
		}

		// -------------------------------------------------------

		return new BasePagingLoadResult<ExtGridData>(sublist, grid.getLiveInfo().getOffset(), grid
				.getLiveInfo().getTotalCount());

	}

}
