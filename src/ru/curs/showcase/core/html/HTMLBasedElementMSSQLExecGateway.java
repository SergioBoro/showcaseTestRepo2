package ru.curs.showcase.core.html;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.MSSQLExecGateway;
import ru.curs.showcase.util.Description;

/**
 * Реализация шлюза к БД с sql скриптами для получения данных для элементов инф.
 * панели типа вебтекст, xform и UI плагин.
 * 
 * @author den
 * 
 */
@Description(
		process = "Загрузка данных для вебтекста, xform или UI плагина из БД с помощью sql скрипта")
public class HTMLBasedElementMSSQLExecGateway extends HTMLBasedElementQuery implements HTMLGateway {

	private static final int MAIN_CONTEXT_INDEX = 3;
	private static final int OUT_SETTINGS_PARAM_INDEX = 9;
	private static final int DATA_PARAM_INDEX = 8;

	private final MSSQLExecGateway mssql = new MSSQLExecGateway(this) {
		@Override
		protected String getParamsDeclaration() {
			return "@main_context varchar(MAX), @add_context varchar(MAX), @filterinfo xml, "
					+ "@session_context xml, @element_Id varchar(MAX), @data xml output, @settings xml output, "
					+ super.getParamsDeclaration();
		}
	};

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	protected void prepareSQL() throws SQLException {
		mssql.prepareSQL();
	}

	@Override
	protected String getSqlTemplate(final int aIndex) {
		return mssql.getSqlTemplate(aIndex);
	}

	@Override
	protected int getReturnParamIndex() {
		return mssql.getReturnParamIndex();
	}

	@Override
	protected int getErrorMesIndex(final int aIndex) {
		return mssql.getErrorMesIndex(aIndex);
	}

	@Override
	public int getDataParam() {
		return DATA_PARAM_INDEX;
	}

	@Override
	protected int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM_INDEX;
	}

	@Override
	protected int getMainContextIndex() {
		return MAIN_CONTEXT_INDEX;
	}
}
