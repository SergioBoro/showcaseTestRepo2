package ru.curs.showcase.model.navigator;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SPCallHelper;

/**
 * Шлюз к хранимой процедуре в БД, возвращающей данные для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorDBGateway extends SPCallHelper implements NavigatorGateway {

	private static final int SESSION_CONTEXT_INDEX = 1;
	private static final int NAVIGATOR_INDEX = 2;

	private String sourceName;

	public String getSourceName() {
		return sourceName;
	}

	@Override
	public InputStream getRawData(final CompositeContext context) {
		setProcName(sourceName);
		try {
			prepareSQL();
			setSQLXMLParamByString(getSessionContextIndex(getTemplateIndex()),
					context.getSession());
			getStatement().registerOutParameter(NAVIGATOR_INDEX, java.sql.Types.SQLXML);
			getStatement().execute();

			InputStream stream = getStatement().getSQLXML(NAVIGATOR_INDEX).getBinaryStream();
			return stream;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
		return null;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{call %s(?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
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
	protected int getSessionContextIndex(final int index) {
		return SESSION_CONTEXT_INDEX;
	}

}
