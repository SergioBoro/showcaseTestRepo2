package ru.curs.showcase.model;

import java.io.IOException;
import java.sql.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPCallHelper extends DataCheckGateway {

	private static final int MAIN_CONTEXT_INDEX = 1;
	private static final int ADD_CONTEXT_INDEX = 2;
	private static final int FILTER_INDEX = 3;
	private static final int SESSION_CONTEXT_INDEX = 4;

	private static final int ERROR_MES_INDEX = -1;

	/**
	 * LOGGER.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SPCallHelper.class);

	/**
	 * Соединение с БД.
	 */
	private Connection conn = null;

	/**
	 * Интерфейс вызова хранимых процедур JDBC.
	 */
	private CallableStatement statement = null;

	/**
	 * Контекст вызова хранимой процедуры.
	 */
	private CompositeContext context = null;

	/**
	 * Имя хранимой процедуры, которую нужно вызвать.
	 */
	private String procName;

	/**
	 * Номер используемого шаблоны запроса. По умолчанию используется первый.
	 */
	private int templateIndex = 0;

	public String getProcName() {
		return procName;
	}

	public void setProcName(final String aProcName) {
		procName = aProcName;
	}

	protected static final String SESSION_CONTEXT_PARAM = "session_context";

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralParameters() throws SQLException {
		statement.setString(getMainContextIndex(templateIndex), "");
		statement.setString(getAddContextIndex(templateIndex), "");
		setSQLXMLParamByString(getFilterIndex(templateIndex), "");
		setSQLXMLParamByString(getSessionContextIndex(templateIndex), "");
		if (context != null) {
			if (context.getMain() != null) {
				statement.setString(getMainContextIndex(templateIndex), context.getMain());
			}
			if (context.getAdditional() != null) {
				statement.setString(getAddContextIndex(templateIndex), context.getAdditional());
			}
			if (context.getFilter() != null) {
				setSQLXMLParamByString(getFilterIndex(templateIndex), context.getFilter());
			}
			if (context.getSession() != null) {
				setSQLXMLParamByString(getSessionContextIndex(templateIndex), context.getSession());
			}
			LOGGER.info("context=" + context.toString());
		}
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(final Connection aConn) {
		conn = aConn;
	}

	public CallableStatement getStatement() {
		return statement;
	}

	public void setStatement(final CallableStatement aCs) {
		statement = aCs;
	}

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	/**
	 * Возвращает шаблон для запуска SQL процедуры.
	 * 
	 * @param index
	 *            - номер шаблона.
	 */
	protected abstract String getSqlTemplate(final int index);

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	public void releaseResources() {
		if (getConn() != null) {
			try {
				getConn().close();
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		}
	}

	/**
	 * Стандартный обработчик исключений, включающий в себя особую обработку
	 * SolutionDBException.
	 * 
	 * @param e
	 *            - исходное исключение.
	 */
	protected final void dbExceptionHandler(final SQLException e) {
		if (ValidateInDBException.isExplicitRaised(e)) {
			throw new ValidateInDBException(e);
		} else {
			if (!checkProcExists()) {
				throw new SPNotExistsException(getProcName());
			}
			handleDBQueryException(e);
		}
	}

	/**
	 * Часть стандартного обработчика исключений, отвечающая за работу с
	 * DBQueryException.
	 * 
	 * @param e
	 *            - исходное исключение.
	 */
	protected void handleDBQueryException(final SQLException e) {
		throw new DBQueryException(e, getProcName(), new DataPanelElementContext(getContext()));
	}

	/**
	 * Проверяет наличие хранимой процедуры в БД.
	 */
	private boolean checkProcExists() {
		String fileName =
			String.format("%s/checkProcExists_%s.sql", AppProps.SCRIPTSDIR, ConnectionFactory
					.getSQLServerType().toString().toLowerCase());

		String sql = "";
		try {
			sql = TextUtils.streamToString(AppProps.loadResToStream(fileName));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, SettingsFileType.SCRIPT);
		}
		if (sql.trim().isEmpty()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.SCRIPT);
		}
		sql = String.format(sql, procName);

		try {
			statement = conn.prepareCall(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return rs.getInt("num") > 0;
			}
		} catch (SQLException e) {
			return true;
		}
		return true;

	}

	/**
	 * Подготавливает SQL запрос.
	 */
	protected void prepareSQL() throws SQLException {
		if (conn == null) {
			conn = ConnectionFactory.getConnection();
		}
		String sql = String.format(getSqlTemplate(templateIndex), getProcName());
		statement = conn.prepareCall(sql);
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом кода ошибки.
	 */
	protected void prepareStatementWithErrorMes() throws SQLException {
		prepareSQL();
		statement.registerOutParameter(1, java.sql.Types.INTEGER);
		statement.registerOutParameter(getErrorMesIndex(templateIndex), java.sql.Types.VARCHAR);
	}

	/**
	 * Функция проверки кода ошибки, который вернула процедура.
	 */
	public void checkErrorCode() throws SQLException {
		int errorCode = getStatement().getInt(1);
		if (errorCode != 0) {
			String errMess = getStatement().getString(getErrorMesIndex(templateIndex));
			throw new ValidateInDBException(errorCode, errMess);
		}
	}

	public int getTemplateIndex() {
		return templateIndex;
	}

	public void setTemplateIndex(final int aTemplateIndex) {
		templateIndex = aTemplateIndex;
	}

	protected void setSQLXMLParamByString(final int index, final String value) throws SQLException {
		String value2 = value;
		if (value2 == null) {
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				value2 = "";
			}
		} else {
			if (value2.isEmpty()
					&& ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				value2 = null;
			}
		}
		SQLXML sqlxml = getConn().createSQLXML();
		sqlxml.setString(value2);
		getStatement().setSQLXML(index, sqlxml);
	}

	protected int getMainContextIndex(final int index) {
		return MAIN_CONTEXT_INDEX;
	}

	protected int getAddContextIndex(final int index) {
		return ADD_CONTEXT_INDEX;
	}

	protected int getFilterIndex(final int index) {
		return FILTER_INDEX;
	}

	protected int getSessionContextIndex(final int index) {
		return SESSION_CONTEXT_INDEX;
	}

	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}
}