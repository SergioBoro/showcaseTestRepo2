package ru.curs.showcase.model.event;

import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementType;
import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.Description;

/**
 * Класс шлюза-исполнителя вызовов SQL хранимых процедур.
 * 
 * @author den
 * 
 */
@Description(process = "Вызов хранимых процедур на сервере SQL")
public class ActivityDBGateway extends SPCallHelper implements ActivityGateway {

	private static final int MAIN_CONTEXT_INDEX = 2;
	private static final int ADD_CONTEXT_INDEX = 3;
	private static final int FILTER_INDEX = 4;
	private static final int SESSION_CONTEXT_INDEX = 5;
	private static final int ERROR_MES_INDEX = 6;

	@Override
	public void exec(final Activity activity) {
		try {
			try {
				setContext(activity.getContext());
				setProcName(activity.getName());

				prepareStatementWithErrorMes();
				setupGeneralParameters();
				execute();
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
		return "{? = call %s(?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getElementType() {
		return DataPanelElementType.NON_DP_ELEMENT;
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

}
