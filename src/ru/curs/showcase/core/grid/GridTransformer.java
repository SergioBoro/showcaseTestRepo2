package ru.curs.showcase.core.grid;

import java.io.IOException;
import java.util.*;

import org.xml.sax.SAXException;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.util.TextUtils;

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
		for (Column c : grid.getDataSet().getColumnSet().getColumnsByIndex()) {
			index++;
			ExtGridColumnConfig column =
				new ExtGridColumnConfig("col" + String.valueOf(index), c.getCaption(),
						getIntWidthByStringWidth(c.getWidth()));

			if (c.getValueType().isDate()) {
				column.setDateTimeFormat("yyyy MMM dd");
			}

			column.setHorizontalAlignment(com.extjs.gxt.ui.client.Style.HorizontalAlignment
					.valueOf(c.getHorizontalAlignment().toString()));

			column.setValueType(c.getValueType());

			column.setLinkId(c.getLinkId());

			columns.add(column);

			if (c == grid.getAutoSelectColumn()) {
				egm.setAutoSelectColumn(column);

			}
		}

		egm.setColumns(columns);

		egm.setAutoSelectRecord(grid.getAutoSelectRecord());

		// -------------------------------------------------------

		egm.getLiveInfo().setOffset(grid.getLiveInfo().getOffset());
		egm.getLiveInfo().setLimit(grid.getDataSet().getRecordSet().getPageSize());
		egm.getLiveInfo().setTotalCount(grid.getLiveInfo().getTotalCount());

		// -------------------------------------------------------

		egm.setOriginalColumnSet(grid.getDataSet().getColumnSet());

		// -------------------------------------------------------

		egm.setUISettings(grid.getUISettings());

		// -------------------------------------------------------

		return egm;
	}

	public static ExtGridPagingLoadResult<ExtGridData> gridToExtGridData(final Grid grid) {

		// -------------------------------------------------------

		ArrayList<ExtGridData> sublist = new ArrayList<ExtGridData>();

		for (Record rec : grid.getDataSet().getRecordSet().getRecords()) {
			ExtGridData egd = new ExtGridData();

			egd.setId(rec.getId());

			egd.setRowStyle(rec.getAttributes().getValue(
					ru.beta2.extra.gwt.ui.GeneralConstants.STYLE_CLASS_TAG));

			int index = 0;
			for (Column c : grid.getDataSet().getColumnSet().getColumnsByIndex()) {
				index++;
				String colId = "col" + String.valueOf(index);
				String val = null;

				switch (c.getValueType()) {
				case IMAGE:
					val = "<a><img border=\"0\" src=\"" + rec.getValue(c) + "\"></a>";
					break;
				case LINK:
					val = getLink(rec.getValue(c));
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

		ExtGridPagingLoadResult<ExtGridData> egplr =
			new ExtGridPagingLoadResult<ExtGridData>(sublist, grid.getLiveInfo().getOffset(), grid
					.getLiveInfo().getTotalCount());

		ExtGridExtradata ege = new ExtGridExtradata();
		ege.setGridEventManager(grid.getEventManager());
		ege.setAutoSelectRecord(grid.getAutoSelectRecord());
		egplr.setExtGridExtradata(ege);

		return egplr;

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

	private static String getLink(final String value) {
		String result = null;

		try {
			org.w3c.dom.Element el =
				ru.curs.showcase.util.xml.XMLUtils.stringToDocument(value).getDocumentElement();

			String href = el.getAttribute("href");
			String text = el.getAttribute("text");
			if ((text == null) || text.isEmpty()) {
				text = href;
			}
			String image = el.getAttribute("image");
			String openInNewTab = el.getAttribute("openInNewTab");
			String target = null;
			if (Boolean.parseBoolean(openInNewTab)) {
				target = "_blank";
			}

			result = "<a class=\"gwt-Anchor\" href=\"" + href + "\" ";
			if (target != null) {
				result = result + "target=\"_blank\"";
			}
			result = result + ">";
			if ((image == null) || image.isEmpty()) {
				result = result + text;
			} else {
				String alt = text != null ? " alt=\"" + text + "\"" : "";
				result = result + "<img border=\"0\" src=\"" + image + "\"" + alt + "/>";
			}
			result = result + "</a>";

		} catch (SAXException | IOException e) {
			result = null;
		}

		return result;
	}
}
