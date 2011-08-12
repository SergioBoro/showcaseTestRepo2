package ru.curs.showcase.model.grid;

import java.sql.*;
import java.text.*;
import java.util.Locale;
import java.util.regex.*;

import javax.sql.RowSet;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.event.EventFactory;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.SQLUtils;

/**
 * Фабрика для создания гридов получающая на вход сырые исходные данные из БД.
 * 
 * @author den
 * 
 */
public class GridDBFactory extends AbstractGridFactory {
	private static final String DEF_DATE_VALUES_FORMAT = "def.date.values.format";
	/**
	 * Не локальная Locale по умолчанию :) Используется для передачи данных в
	 * приложение, которые плохо обрабатывают текущую Locale.
	 */
	private static final Locale DEF_NON_LOCAL_LOCALE = Locale.US;
	/**
	 * Тэг столбца события в гриде.
	 */
	private static final String EVENT_COLUMN_TAG = "column";
	/**
	 * Префикс имени события, определяющий событие в ячейке.
	 */
	private static final String CELL_PREFIX = "cell";

	private RowSet rowset;

	/**
	 * Признак того, что нужно применять форматирование для дат и чисел при
	 * формировании грида. По умолчанию - нужно. Отключать эту опцию необходимо
	 * при экспорте в Excel.
	 */
	private Boolean applyLocalFormatting = true;

	public GridDBFactory(final ElementRawData aRaw, final GridServerState aState) {
		super(aRaw, aState);
	}

	public GridDBFactory(final GridContext context, final GridServerState aState) {
		super(new ElementRawData(null, context), aState);
	}

	public GridDBFactory(final ElementRawData aRaw) {
		super(aRaw, new GridServerState());
	}

	@Override
	protected void prepareData() {
		try {
			ResultSet rs = getSource().getSpCallHelper().getStatement().getResultSet();
			rowset = SQLUtils.cacheResultSet(rs);
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void checkSourceError() {
		super.checkSourceError();

		try {
			getSource().getSpCallHelper().checkErrorCode();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void fillRecordsAndEvents() {
		try {
			scrollToRequiredPage();
			readRecordsLoop();
			calcRecordsCount();
		} catch (Exception e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void readRecordsLoop() throws SQLException {
		ColumnSet cs = getResult().getDataSet().getColumnSet();
		int counter = getRecordSet().getPageInfo().getFirstRecord();
		int lastNumber = counter + getRecordSet().getPageSize();
		while (rowset.next() && (counter < lastNumber)) {
			Record curRecord = new Record();
			curRecord.setId(String.valueOf(counter++));
			setupStdRecordProps(curRecord);

			String value = null;
			for (Column col : cs.getColumns()) {
				if (col.getValueType() == GridValueType.IMAGE) {
					value =
						String.format("%s/%s",
								AppProps.getRequiredValueByName(AppProps.IMAGES_IN_GRID_DIR),
								rowset.getString(col.getId()));
				} else if (col.getValueType() == GridValueType.LINK) {
					value = rowset.getString(col.getId());
					if (value != null) {
						value = AppProps.replaceVariables(value);
						value = makeSafeXMLAttrValues(value);
					}
				} else if (rowset.getObject(col.getId()) == null) {
					value = "";
				} else if (col.getValueType().isDate()) {
					value = getStringValueOfDate(col);
				} else if (col.getValueType().isNumber()) {
					value = getStringValueOfNumber(col);
				} else {
					value = rowset.getString(col.getId());
				}
				curRecord.setValue(col.getId(), value);
			}
			getRecordSet().getRecords().add(curRecord);
			if (SQLUtils.existsColumn(rowset.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(curRecord.getId(), rowset.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	/**
	 * Функция для замены служебных символов XML (только XML, не HTML!) в
	 * описании ссылки в гриде.
	 * 
	 * @param value
	 *            - текст ссылки.
	 * @return - исправленный текст ссылки.
	 */
	public static String makeSafeXMLAttrValues(final String value) {
		String res = value.trim();

		Pattern pattern = Pattern.compile("(\\&(?!quot;)(?!lt;)(?!gt;)(?!amp;)(?!apos;))");
		Matcher matcher = pattern.matcher(res);
		res = matcher.replaceAll("&amp;");

		pattern =
			Pattern.compile("(?<!=)(\")(?!\\s*openInNewTab)(?!\\s*text)(?!\\s*href)(?!\\s*image)(?!\\s*/\\>)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&quot;");

		pattern = Pattern.compile("(?<!^)(\\<)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&lt;");

		pattern = Pattern.compile("(\\>)(?!$)");
		matcher = pattern.matcher(res);
		res = matcher.replaceAll("&gt;");

		res = res.replace("'", "&apos;");

		return res;
	}

	private void readEvents(final String curRecordId, final String data) {
		EventFactory<GridEvent> factory =
			new EventFactory<GridEvent>(GridEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX, getElementInfo().getType()
				.getPropsSchemaName());
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(curRecordId, data));
	}

	private String getStringValueOfNumber(final Column col) throws SQLException {
		Double value = rowset.getDouble(col.getId());
		NumberFormat nf;
		if (applyLocalFormatting) {
			nf = NumberFormat.getNumberInstance();
		} else {
			nf = NumberFormat.getNumberInstance(DEF_NON_LOCAL_LOCALE);
		}
		if (col.getFormat() != null) {
			nf.setMinimumFractionDigits(Integer.parseInt(col.getFormat()));
			nf.setMaximumFractionDigits(Integer.parseInt(col.getFormat()));
		}
		return nf.format(value);
	}

	private String getStringValueOfDate(final Column col) throws SQLException {
		java.util.Date date = null;
		DateFormat df = null;
		String value = getGridProps().getValueByNameForGrid(DEF_DATE_VALUES_FORMAT);
		Integer style = DateFormat.DEFAULT;
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}
		if (col.getValueType() == GridValueType.DATE) {
			date = rowset.getDate(col.getId());
			if (applyLocalFormatting) {
				df = DateFormat.getDateInstance(style);
			} else {
				df = DateFormat.getDateInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.TIME) {
			date = rowset.getTime(col.getId());
			if (applyLocalFormatting) {
				df = DateFormat.getTimeInstance(style);
			} else {
				df = DateFormat.getTimeInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.DATETIME) {
			date = rowset.getTimestamp(col.getId());
			if (applyLocalFormatting) {
				df = DateFormat.getDateTimeInstance(style, style);
			} else {
				df = DateFormat.getDateTimeInstance(style, style, DEF_NON_LOCAL_LOCALE);
			}
		}
		if (date == null) {
			return "";
		}
		return df.format(date);
	}

	private void calcRecordsCount() throws SQLException {
		if (getElementInfo().loadByOneProc()) {
			rowset.last();
			serverState().setTotalCount(rowset.getRow());
		}

		getRecordSet().setPagesTotal(
				(int) Math.ceil((float) serverState().getTotalCount()
						/ getRecordSet().getPageSize()));
	}

	private void scrollToRequiredPage() throws SQLException {
		if (!getElementInfo().loadByOneProc()) {
			return;
		}

		int recNum = 1;
		rowset.setFetchSize(getRecordSet().getPageSize());
		while (recNum++ < getRecordSet().getPageInfo().getFirstRecord()) {
			if (!rowset.next()) {
				// возвращаем 0 записей, если переданная страница не существует
				rowset.previous();
				break;
			}
		}
	}

	@Override
	protected void fillColumns() {
		ColumnSet cs = getResult().getDataSet().getColumnSet();
		Column curColumn = null;
		ResultSetMetaData md;
		try {
			md = rowset.getMetaData();
			for (int i = 1; i <= md.getColumnCount(); i++) {
				if (md.getColumnLabel(i).equals(PROPERTIES_SQL_TAG)) {
					continue;
				}
				String colId = md.getColumnLabel(i);
				curColumn = getResult().getColumnById(colId);
				if (curColumn == null) {
					curColumn = createColumn(colId);
					cs.getColumns().add(curColumn);
				}
				curColumn.setIndex(i - 1);
				determineValueType(curColumn, md.getColumnType(i));
				setupStdColumnProps(curColumn);
				curColumn.setSorting(getCallContext().getSortingForColumn(curColumn));
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void determineValueType(final Column column, final int sqlType) {
		if (column.getValueType() != null) {
			return; // тип задан явно
		}
		if (SQLUtils.isStringType(sqlType)) {
			column.setValueType(GridValueType.STRING);
		} else if (SQLUtils.isIntType(sqlType)) {
			column.setValueType(GridValueType.INT);
		} else if (SQLUtils.isFloatType(sqlType)) {
			column.setValueType(GridValueType.FLOAT);
		} else if (SQLUtils.isDateType(sqlType)) {
			column.setValueType(GridValueType.DATE);
		} else if (SQLUtils.isTimeType(sqlType)) {
			column.setValueType(GridValueType.TIME);
		} else if (SQLUtils.isDateTimeType(sqlType)) {
			column.setValueType(GridValueType.DATETIME);
		} else {
			column.setValueType(GridValueType.STRING);
		}
	}

	public Boolean getApplyLocalFormatting() {
		return applyLocalFormatting;
	}

	public void setApplyLocalFormatting(final Boolean aApplyLocalFormatting) {
		applyLocalFormatting = aApplyLocalFormatting;
	}
}
