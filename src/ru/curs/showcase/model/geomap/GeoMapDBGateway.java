package ru.curs.showcase.model.geomap;

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
@Description(process = "Загрузка данных для карты из БД")
public class GeoMapDBGateway extends CompBasedElementSPCallHelper implements GeoMapGateway {

	private static final int OUT_SETTINGS_PARAM = 6;

	public static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS_1 = 7;
	public static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS_2 = 8;
	public static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS_3 = 9;
	public static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS_4 = 10;
	public static final int ORA_CURSOR_INDEX_DATA_AND_SETTINS_5 = 11;

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
			return "{call %s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		}
	}

	@Override
	protected DataPanelElementType getElementType() {
		return DataPanelElementType.GEOMAP;
	}

	@Override
	protected void registerOutParameterCursor() throws SQLException {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.ORACLE) {
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS_1,
					OracleTypes.CURSOR);
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS_2,
					OracleTypes.CURSOR);
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS_3,
					OracleTypes.CURSOR);
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS_4,
					OracleTypes.CURSOR);
			getStatement().registerOutParameter(ORA_CURSOR_INDEX_DATA_AND_SETTINS_5,
					OracleTypes.CURSOR);
		}
	}
}
