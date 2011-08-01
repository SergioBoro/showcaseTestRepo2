package ru.curs.showcase.model.event;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.*;

/**
 * Класс шлюза-исполнителя вызовов SQL хранимых процедур.
 * 
 * @author den
 * 
 */
public class ActivityDBGateway extends SPCallHelper implements ActivityGateway {

	@Override
	public void exec(final Activity activity) {
		try {
			try {
				setContext(activity.getContext());
				setProcName(activity.getName());

				prepareStatementWithErrorMes();
				setupGeneralParameters();
				getStatement().execute();
				checkErrorCode();
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.NON_DP_ELEMENT;
	}

}
