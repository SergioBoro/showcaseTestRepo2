package ru.curs.showcase.model.grid;

import java.sql.*;

import oracle.jdbc.OracleTypes;
import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для грида из БД")
public class GridDBGateway extends CompBasedElementSPCallHelper implements GridGateway {

	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int MAIN_CONTEXT_INDEX = 2;
	private static final int ADD_CONTEXT_INDEX = 3;
	private static final int FILTER_INDEX = 4;
	private static final int SESSION_CONTEXT_INDEX = 5;
	private static final int ELEMENTID_INDEX = 6;
	private static final int SORTCOLS_INDEX = 7;

	private static final int OUT_SETTINGS_PARAM = 8;

	private static final int ERROR_MES_INDEX_DATA_AND_SETTINGS = 9;

	private static final int FIRST_RECORD_INDEX = 7;
	private static final int PAGE_SIZE_INDEX = 8;

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

	private void setupSorting(final GridContext settings, final int queryType) throws SQLException {
		if (settings.sortingEnabled()) {
			StringBuilder builder = new StringBuilder("ORDER BY ");
			for (Column col : settings.getSortedColumns()) {
				builder.append(String.format("\"%s\" %s,", col.getId(), col.getSorting()));
			}
			String sortStatement = builder.substring(0, builder.length() - 1);
			setStringParam(getSortColumnsIndex(queryType), sortStatement);
		} else {
			setStringParam(getSortColumnsIndex(queryType), "");
		}
	}

	@Override
	public ElementRawData getRawDataAndSettings(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		try {
			context.normalize();

			prepareElementStatementWithErrorMes();
			getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			setupSorting(context, DATA_AND_SETTINS_QUERY);
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
						OracleTypes.CURSOR);
			}
			stdGetResults();

			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case DATA_AND_SETTINS_QUERY:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			}
		case DATA_ONLY_QUERY:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				return "{call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
			}
		case FILE_DOWNLOAD:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	protected DataPanelElementType getElementType() {
		return DataPanelElementType.GRID;
	}

	@Override
	public ElementRawData getRawData(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setTemplateIndex(DATA_ONLY_QUERY);
		try {
			context.normalize();

			prepareSQL();
			setupGeneralElementParameters();
			setIntParam(getAdjustParamIndexAccordingToSQLServerType(FIRST_RECORD_INDEX), context
					.getPageInfo().getFirstRecord());
			setIntParam(getAdjustParamIndexAccordingToSQLServerType(PAGE_SIZE_INDEX),
					context.getPageSize());
			setupSorting(context, DATA_ONLY_QUERY);
			if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
				getStatement().registerOutParameter(1, Types.OTHER);
			}
			if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
				getStatement().registerOutParameter(1, OracleTypes.CURSOR);
			}
			stdGetResults();

			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	protected int getMainContextIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(MAIN_CONTEXT_INDEX - 1);
		} else {
			return MAIN_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getAddContextIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(ADD_CONTEXT_INDEX - 1);
		} else {
			return ADD_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getFilterIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(FILTER_INDEX - 1);
		} else {
			return FILTER_INDEX;
		}
	}

	@Override
	protected int getSessionContextIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(SESSION_CONTEXT_INDEX - 1);
		} else {
			return SESSION_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getElementIdIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(ELEMENTID_INDEX - 1);
		} else {
			return ELEMENTID_INDEX;
		}
	}

	private int getSortColumnsIndex(final int queryType) {
		if (queryType == DATA_ONLY_QUERY) {
			return getAdjustParamIndexAccordingToSQLServerType(SORTCOLS_INDEX - 1);
		} else {
			return SORTCOLS_INDEX;
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
			final DataPanelElementInfo elementInfo, final String linkId, final String recordId) {
		init(context, elementInfo);
		setTemplateIndex(FILE_DOWNLOAD);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());
		OutputStreamDataFile result = null;

		try {
			try {
				prepareElementStatementWithErrorMes();
				setStringParam(RECORD_ID_INDEX, recordId);
				getStatement().registerOutParameter(FILENAME_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_INDEX, getBinarySQLType());
				execute();
				checkErrorCode();
				result = getFileForBinaryStream(FILE_INDEX, FILENAME_INDEX);
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return result;
	}

}
