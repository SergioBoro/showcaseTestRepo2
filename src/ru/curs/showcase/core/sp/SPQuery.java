package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.transform.dom.DOMSource;

import org.slf4j.*;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Абстрактный класс, содержащий базовые константы и функции для вызова хранимых
 * процедур с данными.
 * 
 * @author den
 * 
 */
public abstract class SPQuery extends GeneralXMLHelper implements Closeable {
	public static final String SQL_MARKER = "SQL";
	private static final int MAIN_CONTEXT_INDEX = 2;
	private static final int ADD_CONTEXT_INDEX = 3;
	private static final int FILTER_INDEX = 4;
	private static final int SESSION_CONTEXT_INDEX = 5;

	private static final int ERROR_MES_INDEX = -1;

	protected static final Logger LOGGER = LoggerFactory.getLogger(SPQuery.class);

	private final Map<Integer, Object> params = new TreeMap<>();
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

	private boolean retriveResultSets = false;

	public boolean isRetriveResultSets() {
		return retriveResultSets;
	}

	public void setRetriveResultSets(final boolean aRetriveResultSets) {
		retriveResultSets = aRetriveResultSets;
	}

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
		setStringParam(getMainContextIndex(), "");
		setStringParam(ADD_CONTEXT_INDEX, "");
		setSQLXMLParam(FILTER_INDEX, "");
		setSQLXMLParam(getSessionContextIndex(), "");
		if (context != null) {
			if (context.getMain() != null) {
				setStringParam(getMainContextIndex(), context.getMain());
			}
			if (context.getAdditional() != null) {
				setStringParam(ADD_CONTEXT_INDEX, context.getAdditional());
			}
			if (context.getFilter() != null) {
				setSQLXMLParam(FILTER_INDEX, context.getFilter());
			}
			if (context.getSession() != null) {
				setSQLXMLParam(getSessionContextIndex(), context.getSession());
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
	@Override
	public void close() {
		if (conn != null) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new DBConnectException(e);
				}
			}
			ConnectionFactory.getInstance().release(conn);
			conn = null;
		}
	}

	/**
	 * Стандартный обработчик исключений, включающий в себя особую обработку
	 * SolutionDBException.
	 * 
	 * @param e
	 *            - исходное исключение.
	 */
	protected final BaseException dbExceptionHandler(final SQLException e) {
		if (UserMessageFactory.isExplicitRaised(e)) {
			UserMessageFactory factory = new UserMessageFactory();
			return new ValidateException(factory.build(e));
		} else {
			if (!checkProcExists()) {
				return new SPNotExistsException(getProcName(), getClass());
			}
			return new DBQueryException(e, getProcName(), getClass());
		}
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
			conn = ConnectionFactory.getInstance().acquire();
		}
		sqlTemplate = String.format(getSqlTemplate(templateIndex), getProcName());
		setStatement(conn.prepareCall(sqlTemplate));
		getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом сообщения из БД.
	 */
	protected void prepareStatementWithErrorMes() throws SQLException {
		prepareSQL();
		getStatement().registerOutParameter(getErrorMesIndex(templateIndex),
				java.sql.Types.VARCHAR);
	}

	/**
	 * Функция проверки кода ошибки, который вернула процедура. Проверка
	 * происходит только в том случае, если первым параметром функции является
	 * код возврата. В MSSQL это происходит автоматически.
	 */
	public void checkErrorCode() {
		int errorCode;
		try {
			errorCode = getStatement().getInt(1);
		} catch (SQLException e) {
			// проверка через metadata почему-то глючит с MSSQL JDBC драйвером
			return;
		}
		if (errorCode != 0) {
			String errMess;
			try {
				errMess = getStatement().getString(getErrorMesIndex(templateIndex));
			} catch (SQLException e) {
				errMess = "";
			}
			UserMessageFactory factory = new UserMessageFactory();
			throw new ValidateException(factory.build(errorCode, errMess));
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

	protected int getMainContextIndex() {
		return MAIN_CONTEXT_INDEX;
	}

	protected int getSessionContextIndex() {
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
		boolean res = getStatement().execute();
		AppInfoSingleton.getAppInfo().addExecutedProc(getProcName());
		if (!retriveResultSets) {
			checkErrorCode();
		}
		return res;
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
				throw new ServerObjectCreateCloseException(e);
			}
		}
		return is;
	}

	private InputStream logOutputTextStream(final InputStream is) {
		if (LOGGER.isInfoEnabled()) {
			try {
				StreamConvertor convertor = new StreamConvertor(is);
				String value =
					StreamConvertor.inputToOutputStream(is).toString(TextUtils.DEF_ENCODING);
				logOutputXMLString(value);
				return convertor.getCopy();
			} catch (IOException e) {
				throw new ServerObjectCreateCloseException(e);
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
		if (is == null) {
			throw new FileIsAbsentInDBException();
		}
		String fileName = getStatement().getString(nameIndex);
		StreamConvertor dup;
		try {
			dup = new StreamConvertor(is);
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
		ByteArrayOutputStream os = dup.getOutputStream();
		OutputStreamDataFile result = new OutputStreamDataFile(os, fileName);
		result.setEncoding(TextUtils.JDBC_ENCODING);
		if (result.isXMLFile()) {
			logOutputXMLStream(dup.getCopy());
		} else if (result.isTextFile()) {
			logOutputTextStream(dup.getCopy());
		}
		return result;
	}

	/**
	 * Функция getBytes подходит и для BINARY, и для BLOB.
	 * 
	 * @param dataIndex
	 * @return
	 * @throws SQLException
	 */
	private InputStream getBinaryStream(final int dataIndex) throws SQLException {
		byte[] bt = getStatement().getBytes(dataIndex);
		if (bt == null) {
			return null;
		}
		InputStream is = new ByteArrayInputStream(bt);
		return is;
	}
}