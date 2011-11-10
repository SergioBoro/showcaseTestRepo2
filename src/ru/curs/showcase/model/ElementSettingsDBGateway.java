package ru.curs.showcase.model;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для получения настроек элементов.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка метаданных для элемента инф. панели из БД")
public class ElementSettingsDBGateway extends ElementSPCallHelper implements
		ElementSettingsGateway {

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
			execute();
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
	protected DataPanelElementType getElementType() {
		return getElementInfo().getType();
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_INDEX;
	}

}
