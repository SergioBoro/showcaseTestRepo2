package ru.curs.showcase.core.grid;

import java.io.*;
import java.sql.*;

import javax.sql.RowSet;

import oracle.jdbc.OracleTypes;

import org.joda.time.DateTime;
import org.joda.time.format.*;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для грида из БД")
public class GridDBGateway extends CompBasedElementSPQuery implements GridGateway {

	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int SORTCOLS_INDEX = 7;

	private static final int OUT_SETTINGS_PARAM = 8;

	private static final int ERROR_MES_INDEX_DATA_AND_SETTINGS = 9;

	private static final int FIRST_RECORD_INDEX = 8;
	private static final int PAGE_SIZE_INDEX = 9;

	private static final int DATA_AND_SETTINS_QUERY = 0;
	private static final int DATA_ONLY_QUERY = 1;
	private static final int FILE_DOWNLOAD = 2;

	private static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS = 10;

	private static final int RECORD_ID_INDEX = 7;
	private static final int FILENAME_INDEX = 8;
	private static final int FILE_INDEX = 9;
	private static final int ERROR_MES_INDEX_FILE_DOWNLOAD = 10;

	public GridDBGateway() {
		super();
	}

	public GridDBGateway(final Connection aConn) {
		super();
		setConn(aConn);
	}

	private void setupSorting(final GridContext settings) throws SQLException {
		if (settings.sortingEnabled()) {
			StringBuilder builder = new StringBuilder("ORDER BY ");
			for (Column col : settings.getSortedColumns()) {
				builder.append(String.format("\"%s\" %s,", col.getId(), col.getSorting()));
			}
			String sortStatement = builder.substring(0, builder.length() - 1);
			setStringParam(SORTCOLS_INDEX, sortStatement);
		} else {
			setStringParam(SORTCOLS_INDEX, "");
		}
	}

	@Override
	public RecordSetElementRawData getRawDataAndSettings(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setRetriveResultSets(true);
		try {
			context.normalize();

			prepareElementStatementWithErrorMes();
			getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			setupSorting(context);
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
						OracleTypes.CURSOR);
			}
			stdGetResults();

			RecordSetElementRawData raw = new RecordSetElementRawData(this, elementInfo, context);
			prepareXmlDS(raw);
			return raw;
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case DATA_AND_SETTINS_QUERY:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
			}
		case DATA_ONLY_QUERY:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
		case FILE_DOWNLOAD:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	public RecordSetElementRawData getRawData(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setRetriveResultSets(true);
		setTemplateIndex(DATA_ONLY_QUERY);
		try {
			context.normalize();

			prepareSQL();
			setupGeneralElementParameters();
			setIntParam(FIRST_RECORD_INDEX, context.getPageInfo().getFirstRecord());
			setIntParam(PAGE_SIZE_INDEX, context.getPageSize());
			setupSorting(context);
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				getStatement().registerOutParameter(1, Types.OTHER);
			}
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				getStatement().registerOutParameter(1, OracleTypes.CURSOR);
			}
			stdGetResults();

			RecordSetElementRawData raw = new RecordSetElementRawData(this, elementInfo, context);
			prepareXmlDS(raw);
			return raw;
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		switch (index) {
		case DATA_AND_SETTINS_QUERY:
			return ERROR_MES_INDEX_DATA_AND_SETTINGS;
		case FILE_DOWNLOAD:
			return ERROR_MES_INDEX_FILE_DOWNLOAD;
		default:
			return -1;
		}
	}

	@Override
	protected void registerOutParameterCursor() throws SQLException {
	}

	@Override
	public OutputStreamDataFile downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final ID linkId, final String recordId) {
		init(context, elementInfo);
		setTemplateIndex(FILE_DOWNLOAD);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());

		try (SPQuery query = this) {
			try {
				prepareElementStatementWithErrorMes();
				setStringParam(RECORD_ID_INDEX, recordId);
				getStatement().registerOutParameter(FILENAME_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_INDEX, getBinarySQLType());
				execute();
				OutputStreamDataFile result = getFileForBinaryStream(FILE_INDEX, FILENAME_INDEX);
				return result;
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

	private void prepareXmlDS(final RecordSetElementRawData raw) {
		try {
			ResultSet rs = getResultSetAccordingToSQLServerType(raw);
			if (rs != null) {
				RowSet rowset = SQLUtils.cacheResultSet(rs);
				InputStream is = fillXmlDS(rowset);
				raw.setXmlDS(is);
			}
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}

	}

	private ResultSet getResultSetAccordingToSQLServerType(final RecordSetElementRawData raw)
			throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return raw.nextResultSet();
		} else {
			CallableStatement cs = (CallableStatement) raw.getStatement();
			try {
				return (ResultSet) cs.getObject(1);
			} catch (SQLException e) {
				return (ResultSet) cs.getObject(cs.getParameterMetaData().getParameterCount());
			}
		}
	}

	private InputStream fillXmlDS(final RowSet rowset) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(os, TextUtils.DEF_ENCODING);

			ResultSetMetaData md = rowset.getMetaData();
			writer.append("<" + XML_DATASET_TAG + ">");
			while (rowset.next()) {
				writer.append("<" + RECORD_TAG + ">");
				for (int i = 1; i <= md.getColumnCount(); i++) {
					if (PROPERTIES_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
						writer.append(rowset.getString(md.getColumnLabel(i)));
					} else if (ID_SQL_TAG.equalsIgnoreCase(md.getColumnLabel(i))) {
						writer.append("<" + ID_TAG + ">" + rowset.getString(md.getColumnLabel(i))
								+ "</" + ID_TAG + ">");
					} else {
						String tagName = XMLUtils.escapeTagXml(md.getColumnLabel(i));
						String s = "<" + tagName;
						s =
							s + " " + SQLTYPE_ATTR + "=\"" + String.valueOf(md.getColumnType(i))
									+ "\"";
						s = s + ">";
						String value;
						if (SQLUtils.isGeneralizedDateType(md.getColumnType(i))) {
							value = getStringValueOfDate(rowset, i);
						} else {
							value = XMLUtils.escapeTagXml(rowset.getString(md.getColumnLabel(i)));
						}
						s = s + value + "</" + tagName + ">";
						writer.append(s);
					}
				}
				writer.append("</" + RECORD_TAG + ">");
			}
			writer.append("</" + XML_DATASET_TAG + ">");

			writer.flush();

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

}
