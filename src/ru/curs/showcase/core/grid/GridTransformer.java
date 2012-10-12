package ru.curs.showcase.core.grid;

import java.io.IOException;
import java.util.*;

import org.xml.sax.SAXException;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Класс, преобразующий Grid в LiveGrid.
 * 
 */
public final class GridTransformer {

	private static final int DEF_COLUMN_WIDTH = 100;

	private GridTransformer() {
		throw new UnsupportedOperationException();
	}

	public static LiveGridMetadata gridToLiveGridMetadata(final Grid grid) {

		LiveGridMetadata lgm = new LiveGridMetadata();
		lgm.setId(grid.getId());

		lgm.setHeader(grid.getHeader());
		lgm.setFooter(grid.getFooter());

		// -------------------------------------------------------

		List<LiveGridColumnConfig> columns = new ArrayList<LiveGridColumnConfig>();

		int index = 0;
		for (Column c : grid.getDataSet().getColumnSet().getColumnsByIndex()) {
			if ("hasChildren".equalsIgnoreCase(c.getCaption())) {
				continue;
			}

			index++;
			LiveGridColumnConfig column =
				new LiveGridColumnConfig("col" + String.valueOf(index), c.getCaption(),
						getIntWidthByStringWidth(c.getWidth()));

			column.setHorizontalAlignment(c.getHorizontalAlignment());

			column.setValueType(c.getValueType());

			column.setLinkId(c.getLinkId());

			columns.add(column);
		}

		lgm.setColumns(columns);

		// -------------------------------------------------------

		lgm.getLiveInfo().setOffset(grid.getLiveInfo().getOffset());
		lgm.getLiveInfo().setLimit(grid.getDataSet().getRecordSet().getPageSize());
		lgm.getLiveInfo().setTotalCount(grid.getLiveInfo().getTotalCount());

		// -------------------------------------------------------

		lgm.setOriginalColumnSet(grid.getDataSet().getColumnSet());

		// -------------------------------------------------------

		lgm.setUISettings(grid.getUISettings());

		if ((grid.getDataSet().getRecordSet().getRecords() != null)
				&& (grid.getDataSet().getRecordSet().getRecords().size() > 0)
				&& (grid.getDataSet().getRecordSet().getRecords().get(0) != null)) {
			Record record = grid.getDataSet().getRecordSet().getRecords().get(0);
			lgm.setTextColor(record.getTextColor());
			lgm.setBackgroundColor(record.getBackgroundColor());
			lgm.setFontSize(record.getFontSize());
			lgm.setFontModifiers(record.getFontModifiers());
		}

		// -------------------------------------------------------

		return lgm;
	}

	public static LiveGridData<LiveGridModel> gridToLiveGridData(final Grid grid) {

		// -------------------------------------------------------

		ArrayList<LiveGridModel> sublist = new ArrayList<LiveGridModel>();

		for (Record rec : grid.getDataSet().getRecordSet().getRecords()) {
			LiveGridModel lgm = new LiveGridModel();

			lgm.setId(rec.getId());

			lgm.setRowStyle(rec.getAttributes().getValue(
					ru.beta2.extra.gwt.ui.GeneralConstants.STYLE_CLASS_TAG));

			int index = 0;
			for (Column c : grid.getDataSet().getColumnSet().getColumnsByIndex()) {
				index++;
				String colId = "col" + String.valueOf(index);
				String val = null;

				if (c.getValueType() == null) {
					val = rec.getValue(c);
				} else {
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
				}
				lgm.set(colId, val);

			}
			sublist.add(lgm);
		}

		// -------------------------------------------------------

		LiveGridData<LiveGridModel> lgd =
			new LiveGridData<LiveGridModel>(sublist, grid.getLiveInfo().getOffset(), grid
					.getLiveInfo().getTotalCount());

		LiveGridExtradata lge = new LiveGridExtradata();
		lge.setId(grid.getId());

		lge.setDefaultAction(grid.getDefaultAction());
		lge.setGridEventManager(grid.getEventManager());

		String autoSelectRecordId = null;
		if (grid.getAutoSelectRecord() != null) {
			autoSelectRecordId = grid.getAutoSelectRecord().getId();
		}
		lge.setAutoSelectRecordId(autoSelectRecordId);

		String autoSelectColumnId = null;
		if (grid.getAutoSelectColumn() != null) {
			autoSelectColumnId = grid.getAutoSelectColumn().getId();
		}
		lge.setAutoSelectColumnId(autoSelectColumnId);

		lgd.setLiveGridExtradata(lge);

		return lgd;

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

	public static TreeGridData<TreeGridModel> gridToTreeGridData(final Grid grid) {

		// -------------------------------------------------------

		TreeGridData<TreeGridModel> models = new TreeGridData<TreeGridModel>();

		for (Record rec : grid.getDataSet().getRecordSet().getRecords()) {
			TreeGridModel tgm = new TreeGridModel();

			tgm.setId(rec.getId());

			tgm.setRowStyle(rec.getAttributes().getValue(
					ru.beta2.extra.gwt.ui.GeneralConstants.STYLE_CLASS_TAG));

			int index = 0;
			for (Column c : grid.getDataSet().getColumnSet().getColumnsByIndex()) {
				if ("hasChildren".equalsIgnoreCase(c.getCaption())) {
					tgm.setHasChildren(TextUtils.stringToBoolean(rec.getValue(c)));
				} else {
					index++;
					String colId = "col" + String.valueOf(index);
					String val = null;

					if (c.getValueType() == null) {
						val = rec.getValue(c);
					} else {
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
					}
					tgm.set(colId, val);
				}
			}
			models.add(tgm);
		}

		// -------------------------------------------------------

		LiveGridExtradata lge = new LiveGridExtradata();
		lge.setId(grid.getId());

		lge.setDefaultAction(grid.getDefaultAction());
		lge.setGridEventManager(grid.getEventManager());

		String autoSelectRecordId = null;
		if (grid.getAutoSelectRecord() != null) {
			autoSelectRecordId = grid.getAutoSelectRecord().getId();
		}
		lge.setAutoSelectRecordId(autoSelectRecordId);

		String autoSelectColumnId = null;
		if (grid.getAutoSelectColumn() != null) {
			autoSelectColumnId = grid.getAutoSelectColumn().getId();
		}
		lge.setAutoSelectColumnId(autoSelectColumnId);

		models.setLiveGridExtradata(lge);

		// -------------------------------------------------------

		return models;

	}

}
