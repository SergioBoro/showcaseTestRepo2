package ru.curs.showcase.app.server;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;

import oracle.jdbc.OracleTypes;

import org.slf4j.*;

import ru.beta2.extra.gwt.ui.selector.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.ServletUtils;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Реализация сервиса для селектора. TODO: переделать на работу через SL.
 */
public class SelectorDataServiceImpl extends RemoteServiceServlet implements SelectorDataService {

	private static final String SELECTOR_ERROR =
		"При получении данных для селектора возникла ошибка: ";

	private static final long serialVersionUID = 8719830458845626545L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SelectorDataServiceImpl.class);

	private static final String COLUMN_ID = "ID";

	private static final String COLUMN_NAME = "NAME";

	private static final int NUM1 = 1;
	private static final int NUM2 = 2;
	private static final int NUM3 = 3;
	private static final int NUM4 = 4;
	private static final int NUM5 = 5;
	private static final int NUM6 = 6;
	private static final int NUM7 = 7;
	private static final int NUM8 = 8;
	private static final int NUM9 = 9;
	private static final int NUM10 = 10;

	private static final int BY_ONE_PROC = 1;
	private static final int BY_TWO_PROC = 2;

	/**
	 * PROCNAME_SEPARATOR.
	 */
	private static final String PROCNAME_SEPARATOR = "FDCF8ABB9B6540A89E350010424C2B80";

	@Override
	public DataSet getSelectorData(final DataRequest req) {
		DataSet ds = new DataSet();
		try {
			setupSession();

			ds.setFirstRecord(req.getFirstRecord());
			ds.setTotalCount(0);

			if (req.getProcName().indexOf(PROCNAME_SEPARATOR) > -1) {
				String procCount =
					req.getProcName().substring(0, req.getProcName().indexOf(PROCNAME_SEPARATOR));

				String procList =
					req.getProcName().substring(
							req.getProcName().indexOf(PROCNAME_SEPARATOR)
									+ PROCNAME_SEPARATOR.length());

				getDataByTwoProc(req, procCount, procList, ds);
			} else {
				getDataByOneProc(req, req.getProcName(), ds);
			}

		} catch (UnsupportedEncodingException | SQLException e) {
			ds.setTotalCount(0);
			// вернётся пустой датасет.
			LOGGER.error(SELECTOR_ERROR + e.getMessage());
		}

		return ds;
	}

	private void getDataByTwoProc(final DataRequest req, final String procCount,
			final String procList, final DataSet ds) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		// ЭТАП 1. Подсчёт общего количества записей
		String stmt = String.format("{call %s(?,?,?,?,?,?,?,?)}", procCount);
		CallableStatement cs = conn.prepareCall(stmt);
		try {
			int c;

			setupGeneralParameters(cs, req, true);
			cs.setString(NUM5, req.getParams());
			cs.setString(NUM6, req.getCurValue());
			cs.setBoolean(NUM7, req.isStartsWith());
			cs.registerOutParameter(NUM8, Types.INTEGER);
			cs.execute();
			AppInfoSingleton.getAppInfo().addExecutedProc(procCount);
			c = cs.getInt(NUM8);

			ds.setTotalCount(c);
			cs.close();

			// ЭТАП 2. Возврат записей.
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				conn.setAutoCommit(false);
			}
			stmt = String.format(getSqlTemplate(BY_TWO_PROC), procList);
			cs = conn.prepareCall(stmt);

			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				cs.registerOutParameter(NUM10, Types.OTHER);
			}
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				cs.registerOutParameter(NUM10, OracleTypes.CURSOR);
			}
			setupGeneralParameters(cs, req, false);
			cs.setString(getParamsIndex(), req.getParams());
			cs.setString(getCurValueIndex(), req.getCurValue());
			cs.setBoolean(getIsStartsWithIndex(), req.isStartsWith());
			cs.setInt(getFirstRecordIndex(), req.getFirstRecord());
			cs.setInt(getRecordCountIndex(), req.getRecordCount());

			ResultSet rs = getResultSet(cs);
			AppInfoSingleton.getAppInfo().addExecutedProc(procList);

			// Мы заранее примерно знаем размер, так что используем
			// ArrayList.
			ArrayList<DataRecord> l = new ArrayList<DataRecord>(req.getRecordCount());
			fillArrayListOfDataRecord(rs, l);
			ds.setRecords(l);
		} finally {
			cs.close();
			ConnectionFactory.getInstance().release(conn);
		}
	}

	private void getDataByOneProc(final DataRequest req, final String procListAndCount,
			final DataSet ds) throws SQLException {
		Connection conn = ConnectionFactory.getInstance().acquire();
		String stmt = String.format(getSqlTemplate(BY_ONE_PROC), procListAndCount);
		CallableStatement cs = conn.prepareCall(stmt);

		try {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				cs.registerOutParameter(NUM10, OracleTypes.CURSOR);
			}
			setupGeneralParameters(cs, req, false);
			cs.setString(getParamsIndex(), req.getParams());
			cs.setString(getCurValueIndex(), req.getCurValue());
			cs.setBoolean(getIsStartsWithIndex(), req.isStartsWith());
			cs.setInt(getFirstRecordIndex(), req.getFirstRecord());
			cs.setInt(getRecordCountIndex(), req.getRecordCount());
			cs.registerOutParameter(getCountAllRecordsIndex(), Types.INTEGER);

			ResultSet rs = getResultSet(cs);
			AppInfoSingleton.getAppInfo().addExecutedProc(procListAndCount);

			// Мы заранее примерно знаем размер, так что используем
			// ArrayList.
			ArrayList<DataRecord> l = new ArrayList<DataRecord>(req.getRecordCount());
			fillArrayListOfDataRecord(rs, l);

			int c = cs.getInt(getCountAllRecordsIndex());

			ds.setTotalCount(c);

			ds.setRecords(l);
		} finally {
			cs.close();
			ConnectionFactory.getInstance().release(conn);
		}
	}

	private void setupSession() throws UnsupportedEncodingException {
		CompositeContext context = ServletUtils.prepareURLParamsContext(perThreadRequest.get());
		prepareContext(context);
	}

	private void prepareContext(final CompositeContext context) {
		XMLSessionContextGenerator generator = new XMLSessionContextGenerator(context);
		String sessionContext = generator.generate();

		context.setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		context.getSessionParamsMap().clear();
	}

	private String getSqlTemplate(final int index) {
		if (index == BY_TWO_PROC) {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{call %s(?,?,?,?,?,?,?,?,?)}";
			} else {
				return "{? = call %s(?,?,?,?,?,?,?,?,?)}";
			}
		} else {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{call %s(?,?,?,?,?,?,?,?,?,?)}";
			} else {
				return "{? = call %s(?,?,?,?,?,?,?,?,?,?)}";
			}
		}
	}

	private ResultSet getResultSet(final CallableStatement cs) throws SQLException {
		cs.execute();
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return cs.getResultSet();
		} else {
			return (ResultSet) cs.getObject(NUM10);
		}
	}

	private void fillArrayListOfDataRecord(final ResultSet rs, final ArrayList<DataRecord> l)
			throws SQLException {
		ResultSetMetaData m = rs.getMetaData();
		int aliasId = -1;
		int aliasName = -1;
		for (int i = NUM5; i <= m.getColumnCount(); i++) {
			if (COLUMN_ID.equalsIgnoreCase(m.getColumnName(i))) {
				aliasId = i;
			}
			if (COLUMN_NAME.equalsIgnoreCase(m.getColumnName(i))) {
				aliasName = i;
			}
		}
		if ((aliasId == -1) || (aliasName == -1)) {
			aliasId = 1;
			aliasName = 2;
		}
		while (rs.next()) {
			DataRecord r = new DataRecord();
			r.setId(rs.getString(aliasId));
			r.setName(rs.getString(aliasName));
			for (int i = NUM1; i <= m.getColumnCount(); i++) {
				if ((i != aliasId) && (i != aliasName)) {
					r.addParameter(m.getColumnName(i), rs.getString(i));
				}
			}
			l.add(r);
		}
	}

	private int getMainContextIndex() {
		return NUM1;
	}

	private int getAdditionalContextIndex() {
		return NUM2;
	}

	private int getFilterContextIndex() {
		return NUM3;
	}

	private int getSessionContextIndex() {
		return NUM4;
	}

	private int getParamsIndex() {
		return NUM5;
	}

	private int getCurValueIndex() {
		return NUM6;
	}

	private int getIsStartsWithIndex() {
		return NUM7;
	}

	private int getFirstRecordIndex() {
		return NUM8;
	}

	private int getRecordCountIndex() {
		return NUM9;
	}

	private int getCountAllRecordsIndex() {
		return NUM10;
	}

	private void setStringParam(final CallableStatement cs, final int index, final String value)
			throws SQLException {
		cs.setString(index, value);
	}

	private void setSQLXMLParam(final CallableStatement cs, final int index, final String value)
			throws SQLException {
		String realValue = correctValueForXML(value);

		SQLXML sqlxml = cs.getConnection().createSQLXML();
		sqlxml.setString(realValue);
		cs.setSQLXML(index, sqlxml);
	}

	private String correctValueForXML(final String value) {
		String realValue = value;
		if (realValue == null) {
			if (ConnectionFactory.getSQLServerType() != SQLServerType.POSTGRESQL) {
				realValue = "";
			}
		} else {
			if (realValue.isEmpty()
					&& ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				realValue = null;
			}
		}
		return realValue;
	}

	private void setupGeneralParameters(final CallableStatement cs, final DataRequest req,
			final boolean isForRecordCount) throws SQLException {
		setStringParam(cs, getMainContextIndex(), "");
		setStringParam(cs, getAdditionalContextIndex(), "");
		setSQLXMLParam(cs, getFilterContextIndex(), "");
		setSQLXMLParam(cs, getSessionContextIndex(), "");

		if (req.getAddData().getData1() != null) {
			setStringParam(cs, getMainContextIndex(), req.getAddData().getData1());
		}
		if (req.getAddData().getData2() != null) {
			setStringParam(cs, getAdditionalContextIndex(), req.getAddData().getData2());
		}
		if (req.getAddData().getData3() != null) {
			setSQLXMLParam(cs, getFilterContextIndex(), req.getAddData().getData3());
		}
		if (req.getAddData().getData4() != null) {
			setSQLXMLParam(cs, getSessionContextIndex(), req.getAddData().getData4());
		}

	}

}
