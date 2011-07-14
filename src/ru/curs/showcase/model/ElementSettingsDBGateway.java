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

	protected static final String SETTINGS_COL_NAME = "settings";

	@Override
	public ElementRawData get(final CompositeContext context,
			final DataPanelElementInfo elementInfo) throws SQLException {
		init(context, elementInfo);
		setProcName(elementInfo.getMetadataProc().getName());

		try {
			prepareStdStatementWithErrorMes();
			getStatement().execute();
			checkErrorCode();
			return new ElementRawData(this, elementInfo, context);
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	public String getOutSettingsParam() {
		return SETTINGS_COL_NAME;
	}

	@Override
	protected String getSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return getElementInfo().getType();
	}
}
