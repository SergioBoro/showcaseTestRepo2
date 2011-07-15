package ru.curs.showcase.model.grid;

import java.sql.*;
import java.util.Iterator;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridRequestedSettings;
import ru.curs.showcase.model.*;

/**
 * Шлюз к БД для грида.
 * 
 * @author den
 * 
 */
public class GridDBGateway extends CompBasedElementSPCallHelper implements GridGateway {
	static final String FIRST_RECORD_TAG = "firstrecord";
	protected static final String SORT_COLUMNNAME = "sortcols";
	public static final String OUTPUT_COLUMNNAME = "gridsettings";
	static final int DATA_ONLY_IND = 1;

	public GridDBGateway() {
		super();
	}

	public GridDBGateway(final Connection aConn) {
		setConn(aConn);
	}

	@Override
	public ElementRawData getRawDataAndSettings(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final GridRequestedSettings settings) {
		init(context, elementInfo);
		try {
			settings.normalize();

			prepareElementStatementWithErrorMes();
			getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			setupSorting(getStatement(), settings);
			stdGetResults();

			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	private void setupSorting(final CallableStatement cs, final GridRequestedSettings settings)
			throws SQLException {
		if (settings.sortingEnabled()) {
			String sortStatement = "ORDER BY ";
			Iterator<Column> iterator = settings.getSortedColumns().iterator();
			while (iterator.hasNext()) {
				Column col = iterator.next();
				sortStatement =
					sortStatement + String.format("[%s] %s,", col.getId(), col.getSorting());
			}
			sortStatement = sortStatement.substring(0, sortStatement.length() - 1);
			cs.setString(SORT_COLUMNNAME, sortStatement);
		} else {
			cs.setString(SORT_COLUMNNAME, "");
		}
	}

	@Override
	public ElementRawData getRawDataAndSettings(final CompositeContext aContext,
			final DataPanelElementInfo aElement) {
		return getRawDataAndSettings(aContext, aElement, GridRequestedSettings.createDefault());
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case 0:
			return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?, ?)}";
		case DATA_ONLY_IND:
			return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.GRID;
	}

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final GridRequestedSettings settings) {
		init(context, elementInfo);
		setTemplateIndex(DATA_ONLY_IND);
		try {
			settings.normalize();

			prepareElementStatementWithErrorMes();
			getStatement().setInt("firstrecord", settings.getFirstRecord());
			getStatement().setInt("pagesize", settings.getPageSize());
			setupSorting(getStatement(), settings);
			stdGetResults();

			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}
}
