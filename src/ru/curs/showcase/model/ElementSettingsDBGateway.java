package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Шлюз к БД для получения настроек элементов.
 * 
 * @author den
 * 
 */
public class ElementSettingsDBGateway extends ElementSPCallHelper implements
		ElementSettingsGateway {

	private static final int MAIN_CONTEXT_INDEX = 2;
	private static final int ADD_CONTEXT_INDEX = 3;
	private static final int FILTER_INDEX = 4;
	private static final int SESSION_CONTEXT_INDEX = 5;
	private static final int ELEMENTID_INDEX = 6;
	private static final int OUT_SETTINGS_PARAM = 7;
	private static final int ERROR_MES_INDEX = 8;

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setProcName(elementInfo.getMetadataProc().getName());

		try {
			prepareElementStatementWithErrorMes();
			getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
			getStatement().execute();
			checkErrorCode();
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
		return "{? = call %s(?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return getElementInfo().getType();
	}

	@Override
	protected int getMainContextIndex(final int index) {
		return MAIN_CONTEXT_INDEX;
	}

	@Override
	protected int getAddContextIndex(final int index) {
		return ADD_CONTEXT_INDEX;
	}

	@Override
	protected int getFilterIndex(final int index) {
		return FILTER_INDEX;
	}

	@Override
	protected int getSessionContextIndex(final int index) {
		return SESSION_CONTEXT_INDEX;
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

	@Override
	protected int getElementIdIndex(final int index) {
		return ELEMENTID_INDEX;
	}

}
