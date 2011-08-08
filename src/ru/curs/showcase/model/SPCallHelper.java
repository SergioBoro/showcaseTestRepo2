package ru.curs.showcase.model;

import java.sql.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.DBConnectException;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPCallHelper extends DataCheckGateway {
	/**
	 * LOGGER.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SPCallHelper.class);

	private static final int ERROR_MES_COL_INDEX = 4;

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
		statement.setString(MAIN_CONTEXT_TAG, "");
		statement.setString(ADD_CONTEXT_TAG, "");
		statement.setString(SESSION_CONTEXT_PARAM, "");
		statement.setString(FILTER_TAG, "");
		if (context != null) {
			if (context.getMain() != null) {
				statement.setString(MAIN_CONTEXT_TAG, context.getMain());
			}
			if (context.getAdditional() != null) {
				statement.setString(ADD_CONTEXT_TAG, context.getAdditional());
			}
			if (context.getSession() != null) {
				statement.setString(SESSION_CONTEXT_PARAM, context.getSession());
			}
			if (context.getFilter() != null) {
				statement.setString(FILTER_TAG, context.getFilter());
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
		String sql =
			String.format(
					"IF (OBJECT_ID('[dbo].[%1$s]') IS NOT NULL AND (OBJECTPROPERTY(OBJECT_ID('[dbo].[%1$s]'),'IsProcedure')=1))",
					procName)
					+ " SELECT 'exists' AS [result] ELSE SELECT 'absent' AS [result]";
		try {
			statement = conn.prepareCall(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				return "exists".equals(rs.getString("result"));
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
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			statement.registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
		} else {
			statement.registerOutParameter(ERROR_MES_COL_INDEX, java.sql.Types.VARCHAR);
		}
	}

	/**
	 * Функция проверки кода ошибки, который вернула процедура.
	 */
	public void checkErrorCode() throws SQLException {
		int errorCode = getStatement().getInt(1);
		if (errorCode != 0) {
			throw new ValidateInDBException(errorCode, getStatement().getString(ERROR_MES_COL));
		}
	}

	public int getTemplateIndex() {
		return templateIndex;
	}

	public void setTemplateIndex(final int aTemplateIndex) {
		templateIndex = aTemplateIndex;
	}
}