package ru.curs.showcase.model;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.transform.dom.DOMSource;

import org.slf4j.*;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPCallHelper extends DataCheckGateway {
	public static final String SQL_MARKER = "SQL";
	private static final int MAIN_CONTEXT_INDEX = 1;
	private static final int ADD_CONTEXT_INDEX = 2;
	private static final int FILTER_INDEX = 3;
	private static final int SESSION_CONTEXT_INDEX = 4;

	private static final int ERROR_MES_INDEX = -1;

	/**
	 * LOGGER.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(SPCallHelper.class);

	private final Map<Integer, Object> params = new TreeMap<Integer, Object>();
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
	private String sqlTemplate;

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralParameters() throws SQLException {
		setStringParam(getMainContextIndex(templateIndex), "");
		setStringParam(getAddContextIndex(templateIndex), "");
		setSQLXMLParam(getFilterIndex(templateIndex), "");
		setSQLXMLParam(getSessionContextIndex(templateIndex), "");
		if (context != null) {
			if (context.getMain() != null) {
				setStringParam(getMainContextIndex(templateIndex), context.getMain());
			}
			if (context.getAdditional() != null) {
				setStringParam(getAddContextIndex(templateIndex), context.getAdditional());
			}
			if (context.getFilter() != null) {
				setSQLXMLParam(getFilterIndex(templateIndex), context.getFilter());
			}
			if (context.getSession() != null) {
				setSQLXMLParam(getSessionContextIndex(templateIndex), context.getSession());
			}
		}
	}

	protected void setStringParam(final int index, final String value) throws SQLException {
		getStatement().setString(index, value);
		storeParamValue(index, value);
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
				throw new SPNotExistsException(getProcName(), getClass());
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
		throw new DBQueryException(e, getProcName(), new DataPanelElementContext(getContext()),
				getClass());
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
			sql = TextUtils.streamToString(FileUtils.loadResToStream(fileName));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, fileName, SettingsFileType.SQLSCRIPT);
		}
		if (sql.trim().isEmpty()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.SQLSCRIPT);
		}
		sql = String.format(sql, procName);

		try {
			setStatement(conn.prepareCall(sql));
			ResultSet rs = getStatement().executeQuery();
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
		sqlTemplate = String.format(getSqlTemplate(templateIndex), getProcName());
		setStatement(conn.prepareCall(sqlTemplate));
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом кода ошибки.
	 */
	protected void prepareStatementWithErrorMes() throws SQLException {
		prepareSQL();
		getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
		getStatement().registerOutParameter(getErrorMesIndex(templateIndex),
				java.sql.Types.VARCHAR);
	}

	/**
	 * Функция проверки кода ошибки, который вернула процедура. Проверка
	 * происходит только в том случае, если первым параметром функции является
	 * код возврата.
	 */
	public void checkErrorCode() throws SQLException {
		int errorCode;
		try {
			errorCode = getStatement().getInt(1);
		} catch (Exception e) {
			// проверка через metadata почему-то глючит с MSSQL JDBC драйвером
			return;
		}
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

	protected void setSQLXMLParam(final int index, final String value) throws SQLException {
		String realValue = correctValueForXML(value);

		SQLXML sqlxml = getConn().createSQLXML();
		sqlxml.setString(realValue);
		getStatement().setSQLXML(index, sqlxml);

		storeParamValue(index, realValue);
	}

	private void storeParamValue(final int index, final Object value) {
		if (LOGGER.isInfoEnabled()) {
			if (value instanceof String) {
				params.put(index, value);
			} else if (value instanceof InputStream) {
				try {
					params.put(index, TextUtils.streamToString((InputStream) value));
				} catch (IOException e) {
					throw new ServerLogicError(e);
				}
			} else if (value instanceof Integer) {
				params.put(index, value);
			}
		}
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

	protected boolean execute() throws SQLException {
		String value = SQLUtils.addParamsToSQLTemplate(sqlTemplate, params);
		Marker marker = MarkerFactory.getDetachedMarker(SQL_MARKER);
		marker.add(MarkerFactory.getMarker(LastLogEvents.INPUT));
		LOGGER.info(marker, value);
		return getStatement().execute();
	}

	protected void setBinaryStream(final int parameterIndex, final DataFile<InputStream> file)
			throws SQLException, IOException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			getStatement().setBinaryStream(parameterIndex, file.getData());
		} else {
			StreamConvertor dup = new StreamConvertor(file.getData());
			ByteArrayOutputStream os = dup.getOutputStream();
			getStatement().setBytes(parameterIndex, os.toByteArray());
		}
		if (file.isTextFile()) {
			storeParamValue(parameterIndex, file.getData());
		}
	}

	protected void setIntParam(final int index, final int value) throws SQLException {
		getStatement().setInt(index, value);
		storeParamValue(index, value);
	}

	protected InputStream getInputStreamForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			InputStream is = sqlXml.getBinaryStream();
			is = logOutputXMLStream(is);
			return is;
		}
		return null;
	}

	protected Document getDocumentForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			DOMSource domSource = sqlXml.getSource(DOMSource.class);
			Document doc = (Document) domSource.getNode();
			logOutputXMLDocument(doc);
			return doc;
		}
		return null;
	}

	protected String getStringForXMLParam(final int index) throws SQLException {
		SQLXML sqlXml = getStatement().getSQLXML(index);
		if (sqlXml != null) {
			String result = sqlXml.getString();
			logOutputXMLString(result);
			return result;
		}
		return null;
	}

	private InputStream logOutputXMLStream(final InputStream is) {
		if (LOGGER.isInfoEnabled()) {
			try {
				StreamConvertor convertor = new StreamConvertor(is);
				String value = XMLUtils.streamToString(convertor.getCopy());
				logOutputXMLString(value);
				return convertor.getCopy();
			} catch (IOException e) {
				throw new CreateObjectError(e);
			}
		}
		return is;
	}

	private void logOutputXMLDocument(final Document doc) {
		if (LOGGER.isInfoEnabled()) {
			String value = XMLUtils.documentToString(doc);
			logOutputXMLString(value);
		}
	}

	private void logOutputXMLString(final String value) {
		Marker marker = MarkerFactory.getDetachedMarker(SQL_MARKER);
		marker.add(MarkerFactory.getMarker(LastLogEvents.OUTPUT));
		LOGGER.info(marker, value);
	}

	protected OutputStreamDataFile
			getFileForBinaryStream(final int dataIndex, final int nameIndex) throws SQLException {
		InputStream is = getBinaryStream(dataIndex);
		String fileName = getStatement().getString(nameIndex);
		StreamConvertor dup;
		try {
			dup = new StreamConvertor(is);
		} catch (IOException e) {
			throw new CreateObjectError(e);
		}
		ByteArrayOutputStream os = dup.getOutputStream();
		OutputStreamDataFile result = new OutputStreamDataFile(os, fileName);
		result.setEncoding(TextUtils.JDBC_ENCODING);
		if (result.isTextFile()) {
			logOutputXMLStream(dup.getCopy());
		}
		return result;
	}

	private InputStream getBinaryStream(final int dataIndex) throws SQLException {
		InputStream is = null;
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			Blob blob = getStatement().getBlob(dataIndex);
			is = blob.getBinaryStream();
		} else {
			byte[] bt = getStatement().getBytes(dataIndex);
			is = new ByteArrayInputStream(bt);
		}
		return is;
	}
}