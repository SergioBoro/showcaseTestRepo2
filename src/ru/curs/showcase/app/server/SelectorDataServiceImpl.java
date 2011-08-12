package ru.curs.showcase.app.server;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import org.slf4j.*;

import ru.beta2.extra.gwt.ui.selector.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Реализация сервиса для селектора. TODO: переделать на работу через SL.
 */
public class SelectorDataServiceImpl extends RemoteServiceServlet implements SelectorDataService {

	private static final String SELECTOR_ERROR =
		"При получении данных для селектора возникла ошибка: ";

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8719830458845626545L;

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectorDataServiceImpl.class);

	/**
	 * COLUMN_ID.
	 */
	private static final String COLUMN_ID = "ID";

	/**
	 * COLUMN_NAME.
	 */
	private static final String COLUMN_NAME = "NAME";

	/**
	 * NUM.
	 */
	private static final int NUM1 = 1;
	/**
	 * NUM.
	 */
	private static final int NUM2 = 2;
	/**
	 * NUM.
	 */
	private static final int NUM3 = 3;
	/**
	 * NUM.
	 */
	private static final int NUM4 = 4;
	/**
	 * NUM.
	 */
	private static final int NUM5 = 5;

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

			String procCount =
				req.getProcName().substring(0, req.getProcName().indexOf(PROCNAME_SEPARATOR));

			String procList =
				req.getProcName().substring(
						req.getProcName().indexOf(PROCNAME_SEPARATOR)
								+ PROCNAME_SEPARATOR.length());

			Connection conn = ConnectionFactory.getConnection();
			// ЭТАП 1. Подсчёт общего количества записей
			String stmt = String.format("{call %s(?,?,?,?)}", procCount);
			CallableStatement cs = conn.prepareCall(stmt);
			try {
				int c;

				cs.setString(NUM1, req.getParams());
				cs.setString(NUM2, req.getCurValue());
				cs.setBoolean(NUM3, req.isStartsWith());
				cs.registerOutParameter(NUM4, Types.INTEGER);
				cs.execute();
				c = cs.getInt(NUM4);

				ds.setTotalCount(c);
				cs.close();

				// ЭТАП 2. Возврат записей.
				stmt = String.format("{call %s(?,?,?,?,?)}", procList);
				cs = conn.prepareCall(stmt);

				// Мы заранее примерно знаем размер, так что используем
				// ArrayList.
				List<DataRecord> l;

				cs.setString(NUM1, req.getParams());
				cs.setString(NUM2, req.getCurValue());
				cs.setBoolean(NUM3, req.isStartsWith());
				cs.setInt(NUM4, req.getFirstRecord());
				cs.setInt(NUM5, req.getRecordCount());
				ResultSet rs = cs.executeQuery();
				ResultSetMetaData m = rs.getMetaData();
				l = new ArrayList<DataRecord>(req.getRecordCount());
				int aliasId = -1;
				int aliasName = -1;
				for (int i = NUM1; i <= m.getColumnCount(); i++) {
					if (COLUMN_ID.equalsIgnoreCase(m.getColumnName(i))) {
						aliasId = i;
					}
					if (COLUMN_NAME.equalsIgnoreCase(m.getColumnName(i))) {
						aliasName = i;
					}
				}
				if ((aliasId == -1) || (aliasName == -1)) {
					while (rs.next()) {
						DataRecord r = new DataRecord();
						r.setId(rs.getString(1));
						r.setName(rs.getString(2));
						for (int i = NUM3; i <= m.getColumnCount(); i++) {
							r.addParameter(m.getColumnName(i), rs.getString(i));
						}
						l.add(r);
					}
				} else {
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
				ds.setRecords(l);
			} finally {
				cs.close();
				conn.close();
			}

		} catch (Exception e) {
			ds.setTotalCount(0);
			// вернётся пустой датасет.
			LOGGER.error(SELECTOR_ERROR + e.getMessage());
		}

		return ds;
	}

	private void setupSession() throws UnsupportedEncodingException {
		Map<String, List<String>> params =
			ServletUtils.prepareURLParamsMap(perThreadRequest.get());
		CompositeContext context = new CompositeContext(params);
		prepareContext(context);
	}

	private void prepareContext(final CompositeContext context)
			throws UnsupportedEncodingException {
		String sessionContext =
			SessionContextGenerator.generate(perThreadRequest.get().getSession().getId(), context);

		LOGGER.info("Session context: " + sessionContext);
		context.setSession(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		context.getSessionParamsMap().clear();
	}
}
