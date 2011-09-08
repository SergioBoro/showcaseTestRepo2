package ru.curs.showcase.model.chart;

import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.*;

/**
 * Шлюз для получения данных для графика из БД.
 * 
 * @author den
 * 
 */
public class ChartDBGateway extends CompBasedElementSPCallHelper implements ChartGateway {

	private static final int OUT_SETTINGS_PARAM = 6;

	private static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS = 7;

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return "{call %s(?, ?, ?, ?, ?, ?)}";
		} else {
			return "{call %s(?, ?, ?, ?, ?, ?, ?)}";
		}
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.CHART;
	}

	@Override
	protected void registerOutParameterCursor() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS,
					OracleTypes.CURSOR);
		}
	}

}
