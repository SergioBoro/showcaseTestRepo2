package ru.curs.showcase.core.grid;

import java.sql.*;

import oracle.jdbc.OracleTypes;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.IncorrectElementException;
import ru.curs.showcase.core.sp.SPQuery;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для грида из БД")
public class GridDBGateway extends AbstractGridDBGateway {

	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int SORTCOLS_INDEX = 7;

	private static final int OUT_SETTINGS_PARAM = 8;

	private static final int ERROR_MES_INDEX_DATA_AND_SETTINGS = 9;

	private static final int FIRST_RECORD_INDEX = 8;
	private static final int PAGE_SIZE_INDEX = 9;

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

	@Override
	protected int getSortColsIndex() {
		return SORTCOLS_INDEX;
	}

	@Override
	protected void prepareForGetDataAndSettings() throws SQLException {
		prepareElementStatementWithErrorMes();
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
					OracleTypes.CURSOR);
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
				return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
			} else {
				return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?)}";
			}
		case DATA_ONLY_QUERY:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?)}";
		case FILE_DOWNLOAD:
			return "{? = call %s (?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	protected void prepareForGetData() throws SQLException {
		prepareSQL();
		setupGeneralElementParameters();

		setupRange();

		if (ConnectionFactory.getSQLServerType() == SQLServerType.POSTGRESQL) {
			getStatement().registerOutParameter(1, Types.OTHER);
		}
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(1, OracleTypes.CURSOR);
		}
	}

	@Override
	protected int getPageSizeIndex() {
		return PAGE_SIZE_INDEX;
	}

	@Override
	protected int getFirstRecordIndex() {
		return FIRST_RECORD_INDEX;
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

}
