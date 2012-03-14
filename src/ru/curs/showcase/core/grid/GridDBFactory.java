package ru.curs.showcase.core.grid;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import javax.sql.RowSet;
import javax.xml.stream.*;

import org.joda.time.DateTime;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import ru.beta2.extra.gwt.ui.GeneralConstants;
import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

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

	public GridDBFactory(final RecordSetElementRawData aRaw, final GridServerState aState) {
		super(aRaw, aState);
	}

	public GridDBFactory(final GridContext context, final DataPanelElementInfo aElementInfo,
			final GridServerState aState) {
		super(new RecordSetElementRawData(aElementInfo, context), aState);
	}

	public GridDBFactory(final RecordSetElementRawData aRaw) {
		super(aRaw, new GridServerState());
	}

	@Override
	protected void prepareData() {
		try {
			ResultSet rs = getResultSetAccordingToSQLServerType(getSource());
			if (rs != null) {
				rowset = SQLUtils.cacheResultSet(rs);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	@Override
	protected void checkSourceError() {
		super.checkSourceError();
		if ((rowset == null) && (getXmlDS() == null)) {
			throw new DBQueryException(getElementInfo(), NO_RESULTSET_ERROR);
		}
	}

	@Override
	protected void fillRecordsAndEvents() {
		try {
			scrollToRequiredPage();
			readRecords();
			checkRecordIdUniqueness();
			calcRecordsCount();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	private void readRecords() throws SQLException {
		ColumnSet cs = getResult().getDataSet().getColumnSet();

		int counter;
		int lastNumber;
		if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
			counter = getResult().getLiveInfo().getFirstRecord();
			lastNumber = counter + getResult().getLiveInfo().getLimit();
		} else {
			counter = getRecordSet().getPageInfo().getFirstRecord();
			lastNumber = counter + getRecordSet().getPageSize();
		}

		for (; rowset.next() && (counter < lastNumber); counter++) {
			Record curRecord = new Record();
			if (SQLUtils.existsColumn(rowset.getMetaData(), ID_SQL_TAG)) {
				curRecord.setId(rowset.getString(ID_SQL_TAG));
			} else {
				curRecord.setId(String.valueOf(counter));
			}
			curRecord.setIndex(counter);
			setupStdRecordProps(curRecord);

			for (Column col : cs.getColumns()) {
				curRecord.setValue(col.getId(), getCellValue(col));
			}
			getRecordSet().getRecords().add(curRecord);
			if (SQLUtils.existsColumn(rowset.getMetaData(), PROPERTIES_SQL_TAG)) {
				readEvents(curRecord, rowset.getString(PROPERTIES_SQL_TAG));
			}
		}
	}

	private String getCellValue(final Column col) throws SQLException {
		String value;
		if (rowset.getObject(col.getId()) == null) {
			value = "";
		} else if (col.getValueType() == GridValueType.IMAGE) {
			value =
				String.format("%s/%s",
						AppProps.getRequiredValueByName(AppProps.IMAGES_IN_GRID_DIR),
						rowset.getString(col.getId()));
		} else if (col.getValueType() == GridValueType.LINK) {
			value = rowset.getString(col.getId());
			value = AppProps.replaceVariables(value);
			value = makeSafeXMLAttrValues(value);
		} else if (col.getValueType() == GridValueType.DOWNLOAD) {
			value = rowset.getString(col.getId());
			value = AppProps.replaceVariables(value);
			// value = makeSafeXMLAttrValues(value);
		} else if (col.getValueType().isDate()) {
			value = getStringValueOfDate(col);
		} else if (col.getValueType().isNumber()) {
			value = getStringValueOfNumber(col);
		} else {
			value = rowset.getString(col.getId());
		}
		return value;
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

	/**
	 * Считывает события для записи и ее ячеек, а также доп. параметры записи.
	 * Один из таких параметров - стиль CSS. Есть возможность задать несколько
	 * стилей для каждой записи.
	 */
	private void readEvents(final Record record, final String data) {
		EventFactory<GridEvent> factory =
			new EventFactory<GridEvent>(GridEvent.class, getCallContext());
		factory.initForGetSubSetOfEvents(EVENT_COLUMN_TAG, CELL_PREFIX, getElementInfo().getType()
				.getPropsSchemaName());
		SAXTagHandler recPropHandler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				String value;
				if (record.getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG) == null) {
					initRecAttrs(record);
					value = attrs.getValue(NAME_TAG);
				} else {
					value =
						record.getAttributes().getValue(GeneralConstants.STYLE_CLASS_TAG) + " "
								+ attrs.getValue(NAME_TAG);
				}

				record.getAttributes().setValue(GeneralConstants.STYLE_CLASS_TAG, value);
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { GeneralConstants.STYLE_CLASS_TAG };
				return tags;
			}

		};
		factory.addHandler(recPropHandler);
		getResult().getEventManager().getEvents()
				.addAll(factory.getSubSetOfEvents(new ID(record.getId()), data));
	}

	private String getStringValueOfNumber(final Column col) throws SQLException {
		Double value = rowset.getDouble(col.getId());
		return getStringValueOfNumber(value, col);
	}

	private String getStringValueOfNumber(final Double value, final Column col) {
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
		if (col.getValueType() == GridValueType.DATE) {
			date = rowset.getDate(col.getId());
		} else if (col.getValueType() == GridValueType.TIME) {
			date = rowset.getTime(col.getId());
		} else if (col.getValueType() == GridValueType.DATETIME) {
			date = rowset.getTimestamp(col.getId());
		}
		if (date == null) {
			return "";
		}
		date = adjustDate(rowset, col.getId(), date);
		return getStringValueOfDate(date, col);
	}

	private String getStringValueOfDate(final java.util.Date date, final Column col) {
		DateFormat df = null;
		String value = getGridProps().getStringValue(DEF_DATE_VALUES_FORMAT);
		Integer style = DateFormat.DEFAULT;
		if (value != null) {
			style = DateTimeFormat.valueOf(value).ordinal();
		}
		if (col.getValueType() == GridValueType.DATE) {
			if (applyLocalFormatting) {
				df = DateFormat.getDateInstance(style);
			} else {
				df = DateFormat.getDateInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.TIME) {
			if (applyLocalFormatting) {
				df = DateFormat.getTimeInstance(style);
			} else {
				df = DateFormat.getTimeInstance(style, DEF_NON_LOCAL_LOCALE);
			}
		} else if (col.getValueType() == GridValueType.DATETIME) {
			if (applyLocalFormatting) {
				df = DateFormat.getDateTimeInstance(style, style);
			} else {
				df = DateFormat.getDateTimeInstance(style, style, DEF_NON_LOCAL_LOCALE);
			}
		}
		return df.format(date);
	}

	private java.util.Date adjustDate(final RowSet rs, final String colId,
			final java.util.Date date) throws SQLException {
		java.util.Date result = date;

		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			int index = SQLUtils.getColumnIndex(rs.getMetaData(), colId);
			if (index > -1) {
				String type = rs.getMetaData().getColumnTypeName(index);
				if (("date".equalsIgnoreCase(type)) || ("datetime2".equalsIgnoreCase(type))) {
					Calendar c = Calendar.getInstance();
					c.setTime(result);
					c.add(Calendar.DAY_OF_MONTH, 2);
					result = c.getTime();
				}
			}
		}

		return result;
	}

	private void calcRecordsCount() throws SQLException {
		if (getElementInfo().loadByOneProc() && (getXmlDS() == null)) {
			rowset.last();
			serverState().setTotalCount(rowset.getRow());
		}

		getRecordSet().setPagesTotal(
				(int) Math.ceil((float) serverState().getTotalCount()
						/ getRecordSet().getPageSize()));

		if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
			getResult().getLiveInfo().setTotalCount(serverState().getTotalCount());
		}
	}

	private void scrollToRequiredPage() throws SQLException {
		if (!getElementInfo().loadByOneProc()) {
			return;
		}

		int recNum = 1;
		int fetchSize;
		int firstRecord;

		if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
			fetchSize = getResult().getLiveInfo().getLimit();
			firstRecord = getResult().getLiveInfo().getFirstRecord();
		} else {
			fetchSize = getRecordSet().getPageSize();
			firstRecord = getRecordSet().getPageInfo().getFirstRecord();
		}

		rowset.setFetchSize(fetchSize);
		while (recNum++ < firstRecord) {
			if (!rowset.next()) {
				// возвращаем 0 записей, если переданная страница не
				// существует
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
				if (isServiceColumn(md.getColumnLabel(i))) {
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

	private boolean isServiceColumn(final String aColumnLabel) {
		boolean result = false;
		if (getXmlDS() == null) {
			result =
				PROPERTIES_SQL_TAG.equalsIgnoreCase(aColumnLabel)
						|| ID_SQL_TAG.equalsIgnoreCase(aColumnLabel);
		} else {
			result =
				PROPS_TAG.equalsIgnoreCase(aColumnLabel) || ID_TAG.equalsIgnoreCase(aColumnLabel);
		}
		return result;
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

	private static final String SAX_ERROR_MES = "XML-датасет грида";

	@Override
	protected void fillColumnsAndRecordsAndEventsByXmlDS() {

		XmlDSHandler handler = new XmlDSHandler();
		SimpleSAX sax = new SimpleSAX(getXmlDS(), handler, SAX_ERROR_MES);
		sax.parse();

		try {
			postProcessingByXmlDS();
			checkRecordIdUniqueness();
			serverState().setTotalCount(handler.getCounterRecord());
			calcRecordsCount();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}

	}

	/**
	 * Формирует грид на основе XML-датасета.
	 */
	private class XmlDSHandler extends DefaultHandler {

		private static final String RECORD_TAG = "rec";

		private boolean processRecord = false;
		private boolean processValue = false;

		private int firstNumber;
		private int lastNumber;
		private int counterRecord = 0;
		private int counterColumn = 1;

		private Column curColumn = null;
		private Record curRecord = null;
		private ByteArrayOutputStream osValue = null;
		private XMLStreamWriter writerValue = null;

		public XmlDSHandler() {
			super();

			if (getCallContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID) {
				firstNumber = getResult().getLiveInfo().getFirstRecord();
				lastNumber = firstNumber + getResult().getLiveInfo().getLimit();
			} else {
				firstNumber = getRecordSet().getPageInfo().getFirstRecord();
				lastNumber = firstNumber + getRecordSet().getPageSize();
			}
		}

		private int getCounterRecord() {
			return counterRecord;
		}

		private boolean isRequiredPage() {
			return (firstNumber <= counterRecord) && (counterRecord < lastNumber);
		}

		@Override
		public void startElement(final String uri, final String localName, final String name,
				final Attributes atts) {
			if (RECORD_TAG.equals(localName)) {
				counterRecord++;
				if (isRequiredPage()) {
					processRecord = true;
					curRecord = new Record();
					return;
				}
			}

			if (!processRecord || !isRequiredPage()) {
				return;
			}

			if (processValue) {
				try {
					writerValue.writeStartElement(localName);
					for (int i = 0; i < atts.getLength(); i++) {
						writerValue.writeAttribute(atts.getQName(i), atts.getValue(i));
					}
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			} else {
				String colId = XMLUtils.unEscapeTagXml(localName);
				curColumn = getResult().getColumnById(colId);

				if (counterRecord == firstNumber) {
					if (curColumn == null) {
						curColumn = createColumn(colId);
						getResult().getDataSet().getColumnSet().getColumns().add(curColumn);
					}
					curColumn.setIndex(counterColumn - 1);
					determineValueType(curColumn, Types.VARCHAR);
					setupStdColumnProps(curColumn);
					curColumn.setSorting(getCallContext().getSortingForColumn(curColumn));
					counterColumn++;
				}

				processValue = true;
				osValue = new ByteArrayOutputStream();
				try {
					writerValue =
						XMLOutputFactory.newInstance().createXMLStreamWriter(osValue,
								TextUtils.DEF_ENCODING);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length) {
			if (!isRequiredPage()) {
				return;
			}

			if (processValue) {
				try {
					writerValue.writeCharacters(ch, start, length);
				} catch (XMLStreamException e) {
					throw new SAXError(e);
				}
				return;
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String name) {
			if (!isRequiredPage()) {
				return;
			}

			if (RECORD_TAG.equals(localName)) {
				curRecord.setIndex(counterRecord);
				setupStdRecordProps(curRecord);
				getRecordSet().getRecords().add(curRecord);
				processRecord = false;
				return;
			}

			if (processValue) {
				String colId = XMLUtils.unEscapeTagXml(localName);
				try {
					if (curColumn == getResult().getColumnById(colId)) {
						curRecord.setValue(curColumn.getId(),
								osValue.toString(TextUtils.DEF_ENCODING));
						processValue = false;
					} else {
						writerValue.writeEndElement();
					}
				} catch (XMLStreamException | UnsupportedEncodingException e) {
					throw new SAXError(e);
				}
			}
		}

	}

	private void postProcessingByXmlDS() {
		Column idColumn = getResult().getColumnById(ID_TAG);
		Column propsColumn = getResult().getColumnById(PROPS_TAG);
		for (Record rec : getRecordSet().getRecords()) {
			if (idColumn != null) {
				rec.setId(rec.getValue(idColumn));
			} else {
				rec.setId(String.valueOf(rec.getIndex()));
			}
			if (propsColumn != null) {
				readEvents(rec, "<" + PROPS_TAG + ">" + rec.getValue(propsColumn) + "</"
						+ PROPS_TAG + ">");
			}
		}
		if (idColumn != null) {
			getResult().getDataSet().getColumnSet().getColumns().remove(idColumn);
		}
		if (propsColumn != null) {
			getResult().getDataSet().getColumnSet().getColumns().remove(propsColumn);
		}

		for (Record rec : getRecordSet().getRecords()) {
			for (Column col : getResult().getDataSet().getColumnSet().getColumns()) {
				rec.setValue(col.getId(), getCellValueForXmlDS(rec.getValue(col), col));
			}
		}
	}

	private String getCellValueForXmlDS(final String aValue, final Column col) {
		String value = aValue;
		if (value == null) {
			value = "";
		}
		if (col.getValueType() == GridValueType.IMAGE) {
			value =
				String.format("%s/%s",
						AppProps.getRequiredValueByName(AppProps.IMAGES_IN_GRID_DIR), value);
		} else if (col.getValueType() == GridValueType.LINK) {
			value = AppProps.replaceVariables(value);
			value = normalizeLink(value);
			value = makeSafeXMLAttrValues(value);
		} else if (col.getValueType() == GridValueType.DOWNLOAD) {
			value = AppProps.replaceVariables(value);
		} else if (col.getValueType().isDate()) {
			DateTime dt = new DateTime(value);
			java.util.Date date = dt.toDate();
			value = getStringValueOfDate(date, col);
		} else if (col.getValueType().isNumber()) {
			value = getStringValueOfNumber(Double.valueOf(value), col);
		}
		return value;
	}

	private static String normalizeLink(final String aValue) {
		String value = aValue.trim();
		value = value.replace("></" + GridValueType.LINK.toString().toLowerCase() + ">", "/>");

		return value;
	}

}
