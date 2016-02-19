package ru.curs.showcase.core.grid;

import org.w3c.dom.*;

import ru.curs.lyra.LyraFieldType;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Утилиты серверной части грида.
 * 
 */
public final class GridUtils {

	private static final String FILTER_TAG = "filter";

	private GridUtils() {
		throw new UnsupportedOperationException();
	}

	public static void fillFilterContextByFilterInfo(final GridContext gridContext)
			throws Exception {
		fillFilterContextByFilterOrListOfValuesInfo(gridContext, false);
	}

	public static void fillFilterContextByListOfValuesInfo(final GridContext gridContext)
			throws Exception {
		fillFilterContextByFilterOrListOfValuesInfo(gridContext, true);
	}

	private static void fillFilterContextByFilterOrListOfValuesInfo(final GridContext gridContext,
			final boolean isListOfValues) throws Exception {
		if (gridContext.getGridFilterInfo().getFilters().size() > 0) {
			String filterContext = gridContext.getFilter();
			if ((filterContext == null) || filterContext.isEmpty()) {
				filterContext = "<" + FILTER_TAG + "></" + FILTER_TAG + ">";
			}
			Document doc = XMLUtils.stringToDocument(filterContext);

			Document docFilterInfo;
			if (isListOfValues) {
				docFilterInfo = XMLUtils.objectToXML(gridContext.getGridListOfValuesInfo());
			} else {
				docFilterInfo = XMLUtils.objectToXML(gridContext.getGridFilterInfo());
			}
			Element inserted = docFilterInfo.getDocumentElement();
			Element child = (Element) doc.importNode(inserted, true);
			doc.getElementsByTagName(FILTER_TAG).item(0).appendChild(child);

			String result = XMLUtils.documentToString(doc);
			gridContext.setFilter(result);
		}
	}

	public static GridValueType getGridValueTypeByLyraFieldType(final LyraFieldType lft) {
		switch (lft) {
		case BLOB:
			return GridValueType.STRING;
		case BIT:
			return GridValueType.STRING;
		case DATETIME:
			return GridValueType.DATETIME;
		case REAL:
			return GridValueType.FLOAT;
		case INT:
			return GridValueType.INT;
		case VARCHAR:
			return GridValueType.STRING;
		default:
			return GridValueType.STRING;
		}
	}

}
