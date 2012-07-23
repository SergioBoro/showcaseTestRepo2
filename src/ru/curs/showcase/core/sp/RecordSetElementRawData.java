package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.*;

import javax.sql.RowSet;

import org.joda.time.DateTime;
import org.joda.time.format.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Базовый класс, содержащий необработанные XML данные и метаданные элемента
 * инф.панели.
 * 
 * @author den
 * 
 */
public class RecordSetElementRawData extends ElementRawData implements Closeable {
	/**
	 * Вспомогательный модуль для получения необходимых данных из БД.
	 * Используется при необходимости считывания нескольких блоков данных в
	 * определенном порядке.
	 */
	private final ElementSPQuery spQuery;

	private final PreparedStatement[] statement;

	private int statementIndex = 0;

	/**
	 * Данные, полученные из тега поля settings.
	 */
	private InputStream xmlDS = null;

	public RecordSetElementRawData(final InputStream props,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext,
			final PreparedStatement[] aStatement) {
		super(aElementInfo, aContext, props);
		spQuery = null;
		statement = aStatement;
	}

	public RecordSetElementRawData(final ElementSPQuery aSPQuery,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		super(aElementInfo, aContext);
		spQuery = aSPQuery;
		statement = null;
	}

	public RecordSetElementRawData(final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		super(aElementInfo, aContext);
		spQuery = null;
		statement = null;
	}

	/**
	 * Функция принудительно освобождает ресурсы, используемые шлюзом для
	 * получения данных. Должна быть вызвана после работы фабрики по построению
	 * навигатора.
	 * 
	 */
	@Override
	public void close() {
		if (statement != null) {
			try {
				ConnectionFactory.getInstance().release(statement[0].getConnection());
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		} else {
			spQuery.close();
		}
	}

	public void checkErrorCode() {
		if (spQuery != null) {
			spQuery.checkErrorCode();
		}
	}

	public PreparedStatement getStatement() {
		if (statement != null) {
			return statement[0];
		}
		return spQuery.getStatement();
	}

	private boolean hasResultSet() throws SQLException {
		if (statement != null) {
			return statementIndex < statement.length;
		}
		if (statementIndex > 0) {
			return spQuery.getStatement().getMoreResults();
		}
		return true;
	}

	public ResultSet nextResultSet() {
		try {
			if (!hasResultSet()) {
				return null;
			}
			if (statement != null) {
				return statement[statementIndex++].getResultSet();
			}
			statementIndex++;
			return spQuery.getStatement().getResultSet();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	public InputStream getXmlDS() {
		return xmlDS;
	}

	public void setXmlDS(final InputStream aXmlDS) {
		xmlDS = aXmlDS;
	}

	/**
	 * Подготавливает настройки элемента.
	 * 
	 */
	public void prepareSettings() {
		if (getSettings() != null) {
			return;
		}
		try {
			setSettings(spQuery.getValidatedSettings());
			if (getXmlDS() == null) {
				setXmlDS(spQuery.getXmlDS());
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	// ----------------------------------------------

	public void prepareXmlDS() {

		if (!((getElementInfo().getType() == DataPanelElementType.GRID) || (getElementInfo()
				.getType() == DataPanelElementType.CHART))) {
			return;
		}

		try {
			ResultSet rs = getResultSetAccordingToSQLServerType();
			if (rs != null) {
				RowSet rowset = SQLUtils.cacheResultSet(rs);

				InputStream is;
				switch (getElementInfo().getType()) {
				case GRID:
					is = fillXmlDSForGrid(rowset);
					break;
				case CHART:
					is = fillXmlDSForChart(rowset);
					break;
				default:
					is = null;
					break;
				}

				setXmlDS(is);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}

	}

	private ResultSet getResultSetAccordingToSQLServerType() throws SQLException {
		switch (ConnectionFactory.getSQLServerType()) {
		case MSSQL:
			return nextResultSet();
		case POSTGRESQL:
			CallableStatement cs = (CallableStatement) getStatement();
			if (cs.getObject(1) instanceof ResultSet) {
				return (ResultSet) cs.getObject(1);
			} else {
				return null;
			}
		case ORACLE:
			cs = (CallableStatement) getStatement();
			try {
				return (ResultSet) cs.getObject(1);
			} catch (SQLException e) {
				return (ResultSet) cs.getObject(cs.getParameterMetaData().getParameterCount());
			}
		default:
			return null;
		}

	}

	private InputStream fillXmlDSForGrid(final RowSet rowset) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(os, TextUtils.DEF_ENCODING);

			ResultSetMetaData md = rowset.getMetaData();
			writer.append("<" + GeneralXMLHelper.XML_DATASET_TAG + ">");
			while (rowset.next()) {
				writer.append("<" + GeneralXMLHelper.RECORD_TAG + ">");
				for (int i = 1; i <= md.getColumnCount(); i++) {
					if (GeneralXMLHelper.PROPERTIES_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
						writer.append(rowset.getString(md.getColumnLabel(i)));
					} else if (GeneralXMLHelper.ID_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
						writer.append("<_x007e__x007e_" + GeneralXMLHelper.ID_TAG + ">"
								+ rowset.getString(md.getColumnLabel(i)) + "</_x007e__x007e_"
								+ GeneralXMLHelper.ID_TAG + ">");
					} else {
						String tagName = XMLUtils.escapeTagXml(md.getColumnLabel(i));
						String s = "<" + tagName;
						s =
							s + " " + GeneralXMLHelper.SQLTYPE_ATTR + "=\""
									+ String.valueOf(md.getColumnType(i)) + "\"";
						s = s + ">";
						String value;
						if (SQLUtils.isGeneralizedDateType(md.getColumnType(i))) {
							value = getStringValueOfDate(rowset, i);
						} else if (SQLUtils.isStringType(md.getColumnType(i))) {
							value =
								XMLUtils.escapeValueXml(rowset.getString(md.getColumnLabel(i)));
						} else {
							value = rowset.getString(md.getColumnLabel(i));
						}
						s = s + value + "</" + tagName + ">";
						writer.append(s);
					}
				}
				writer.append("</" + GeneralXMLHelper.RECORD_TAG + ">");
			}
			writer.append("</" + GeneralXMLHelper.XML_DATASET_TAG + ">");

			writer.close();

			return StreamConvertor.outputToInputStream(os);
		} catch (SQLException | IOException e) {
			throw new SAXError(e);
		}

	}

	private String getStringValueOfDate(final RowSet rowset, final int colIndex)
			throws SQLException {
		ResultSetMetaData md = rowset.getMetaData();
		int sqltype = md.getColumnType(colIndex);
		String colName = md.getColumnLabel(colIndex);

		java.util.Date date = null;
		if (SQLUtils.isDateType(sqltype)) {
			date = rowset.getDate(colName);
		} else if (SQLUtils.isTimeType(sqltype)) {
			date = rowset.getTime(colName);
		} else if (SQLUtils.isDateTimeType(sqltype)) {
			date = rowset.getTimestamp(colName);
		}
		if (date == null) {
			return "";
		}

		DateTime dt = new DateTime(date);
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			String type = md.getColumnTypeName(colIndex);
			if (("date".equalsIgnoreCase(type)) || ("datetime2".equalsIgnoreCase(type))) {
				dt = dt.plusDays(2);
			}
		}
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		return fmt.print(dt);
	}

	private InputStream fillXmlDSForChart(final RowSet rowset) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(os, TextUtils.DEF_ENCODING);

			ResultSetMetaData md = rowset.getMetaData();
			writer.append("<" + GeneralXMLHelper.XML_DATASET_TAG + ">");
			while (rowset.next()) {
				writer.append("<" + GeneralXMLHelper.RECORD_TAG + ">");
				for (int i = 1; i <= md.getColumnCount(); i++) {
					if (GeneralXMLHelper.PROPERTIES_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
						writer.append(rowset.getString(md.getColumnLabel(i)));
					} else {
						String tagName = XMLUtils.escapeTagXml(md.getColumnLabel(i));
						writer.append("<" + tagName + ">" + rowset.getString(md.getColumnLabel(i))
								+ "</" + tagName + ">");
					}
				}
				writer.append("</" + GeneralXMLHelper.RECORD_TAG + ">");
			}
			writer.append("</" + GeneralXMLHelper.XML_DATASET_TAG + ">");

			writer.close();

			return StreamConvertor.outputToInputStream(os);
		} catch (SQLException | IOException e) {
			throw new SAXError(e);
		}
	}
}
