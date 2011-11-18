package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPQuery;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для навигатора из БД")
public class NavigatorDBGateway extends SPQuery implements NavigatorGateway {

	private static final int SESSION_CONTEXT_INDEX = 2;
	private static final int NAVIGATOR_INDEX = 3;

	private String sourceName;

	public String getSourceName() {
		return sourceName;
	}

	@Override
	public InputStream getRawData(final CompositeContext context) {
		init(context);
		try {
			prepareSQL();
			setSQLXMLParam(getSessionContextIndex(), context.getSession());
			getStatement().registerOutParameter(NAVIGATOR_INDEX, java.sql.Types.SQLXML);
			execute();

			InputStream stream = getInputStreamForXMLParam(NAVIGATOR_INDEX);
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	private void init(final CompositeContext context) {
		setProcName(sourceName);
		setContext(context);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s(?, ?)}";
	}

	@Override
	protected DataPanelElementType getElementType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

	@Override
	public void setSourceName(final String aSourceName) {
		sourceName = aSourceName;

	}

	@Override
	public InputStream getRawData(final CompositeContext aContext, final String aSourceName) {
		sourceName = aSourceName;
		return getRawData(aContext);
	}

	@Override
	protected int getSessionContextIndex() {
		return SESSION_CONTEXT_INDEX;
	}

}
