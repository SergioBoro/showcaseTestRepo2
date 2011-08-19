package ru.curs.showcase.model.grid;

import java.sql.*;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
public class GridDBGateway extends CompBasedElementSPCallHelper implements GridGateway {

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

	private static final int DATA_ONLY_QUERY = 1;
	private static final int DATA_AND_SETTINS_QUERY = 0;

	public GridDBGateway() {
		super();
	}

	public GridDBGateway(final Connection aConn) {
		super();
		setConn(aConn);
	}

	private void setupSorting(final CallableStatement cs, final GridContext settings,
			final int queryType) throws SQLException {
		if (settings.sortingEnabled()) {
			StringBuilder builder = new StringBuilder("ORDER BY ");
			for (Column col : settings.getSortedColumns()) {
				builder.append(String.format("\"%s\" %s,", col.getId(), col.getSorting()));
			}
			String sortStatement = builder.substring(0, builder.length() - 1);
			cs.setString(getSortColumnsIndex(queryType), sortStatement);
		} else {
			cs.setString(getSortColumnsIndex(queryType), "");
		}
	}

	private int getSortColumnsIndex(final int queryType) {
		if (queryType == DATA_ONLY_QUERY) {
			return SORTCOLS_INDEX - 1;
		} else {
			return SORTCOLS_INDEX;
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
			setupSorting(getStatement(), context, DATA_AND_SETTINS_QUERY);
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
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
		case DATA_ONLY_QUERY:
			return "{call %s(?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	protected DataPanelElementType getGatewayType() {
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
			getStatement().setInt(FIRST_RECORD_INDEX, context.getPageInfo().getFirstRecord());
			getStatement().setInt(PAGE_SIZE_INDEX, context.getPageSize());
			setupSorting(getStatement(), context, DATA_ONLY_QUERY);
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
			return MAIN_CONTEXT_INDEX - 1;
		} else {
			return MAIN_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getAddContextIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return ADD_CONTEXT_INDEX - 1;
		} else {
			return ADD_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getFilterIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return FILTER_INDEX - 1;
		} else {
			return FILTER_INDEX;
		}
	}

	@Override
	protected int getSessionContextIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return SESSION_CONTEXT_INDEX - 1;
		} else {
			return SESSION_CONTEXT_INDEX;
		}
	}

	@Override
	protected int getElementIdIndex(final int index) {
		if (index == DATA_ONLY_QUERY) {
			return ELEMENTID_INDEX - 1;
		} else {
			return ELEMENTID_INDEX;
		}
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		if (index == DATA_AND_SETTINS_QUERY) {
			return ERROR_MES_INDEX_DATA_AND_SETTINGS;
		} else {
			return -1;
		}
	}

}
