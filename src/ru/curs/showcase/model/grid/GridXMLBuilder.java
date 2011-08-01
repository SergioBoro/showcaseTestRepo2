package ru.curs.showcase.model.grid;

import java.util.List;

import org.w3c.dom.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.Grid;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Построитель XML документов на основе объекта грида.
 * 
 * @author den
 * 
 */
public class GridXMLBuilder extends GeneralXMLHelper {
	/**
	 * Тэг для описания свойств столбца.
	 */
	public static final String COLUMN_TAG = "Column";
	/**
	 * Тэг для ячейки.
	 */
	public static final String CELL_TAG = "Cell";
	/**
	 * Тэг для записи.
	 */
	public static final String ROW_TAG = "Row";
	/**
	 * Корневой тэг для таблицы.
	 */
	public static final String TABLE_TAG = "Table";

	/**
	 * Результирующий документ.
	 */
	private Document result;

	/**
	 * Исходный грид.
	 */
	private final Grid grid;

	/**
	 * Актуальный набор столбцов, который установлен на клиенте. Набор столбцов
	 * из в Grid содержит настройки по умолчанию.
	 */
	private ColumnSet cs;

	public GridXMLBuilder(final Grid aGrid) {
		super();
		grid = aGrid;
	}

	/**
	 * Основная функция построителя, принимающая актуальный набор столбцов.
	 * 
	 * @param aColumnSet
	 *            - актуальный набор столбцов.
	 * 
	 * @return - XML документ.
	 */
	public Document build(final ColumnSet aColumnSet) {
		cs = aColumnSet;
		result = createDoc();
		addColumnsData();
		addHeader();
		addRows();
		return result;
	}

	/**
	 * Тестовая функция построителя - без набора столбцов.
	 * 
	 * @return - XML документ.
	 */
	public Document build() {
		return build(null);
	}

	private void addRows() {
		for (Record cur : grid.getDataSet().getRecordSet().getRecords()) {
			addRow(false, cur);
		}
	}

	private Document createDoc() {
		return XMLUtils.createEmptyDoc(TABLE_TAG);
	}

	private void addHeader() {
		addRow(true, null);
	}

	private void addRow(final boolean isHeader, final Record record) {
		Element rowNode = result.createElement(ROW_TAG);
		for (Column current : getColumns()) {
			Element node = result.createElement(CELL_TAG);
			if (isHeader) {
				node.setAttribute(TYPE_TAG, GridValueType.STRING.toStringForExcel());
				node.appendChild(result.createTextNode(current.getCaption()));
			} else {
				node.setAttribute(TYPE_TAG, current.getValueType().toStringForExcel());
				node.appendChild(result.createTextNode(record.getValue(current)));
			}
			rowNode.appendChild(node);
		}
		getRoot().appendChild(rowNode);
	}

	private void addColumnsData() {
		Element node;
		for (Column current : getColumns()) {
			if (current.getWidth() != null) {
				node = result.createElement(COLUMN_TAG);
				node.setAttribute(WIDTH_TAG, TextUtils.getIntSizeValue(current.getWidth())
						.toString());
				getRoot().appendChild(node);
			}
		}
	}

	private List<Column> getColumns() {
		ColumnSet current;
		if (cs == null) {
			current = grid.getDataSet().getColumnSet();
		} else {
			current = cs;
		}
		return current.getVisibleColumnsByIndex();
	}

	private Element getRoot() {
		return result.getDocumentElement();
	}
}
