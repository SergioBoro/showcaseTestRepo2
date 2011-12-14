package ru.curs.showcase.model.sp;

import java.sql.SQLException;

import ru.curs.showcase.model.command.ExternalCommandGateway;
import ru.curs.showcase.util.Description;

/**
 * Шлюз к БД для выполнения внешней команды (например, из веб-сервисов).
 * 
 * @author den
 * 
 */
@Description(process = "Выполнение внешней команды")
public class DBExternalCommandGateway extends SPQuery implements ExternalCommandGateway {
	private static final int ERROR_MES_PARAM = 4;
	private static final int OUTPUTDATA_PARAM = 3;

	@Override
	public String handle(final String aRequest, final String aSource) {
		setProcName(aSource);
		String out = null;
		try (SPQuery query = this) {
			try {
				prepareStatementWithErrorMes();
				setStringParam(2, aRequest);
				getStatement().registerOutParameter(OUTPUTDATA_PARAM, java.sql.Types.VARCHAR);
				execute();
				out = getStatement().getString(OUTPUTDATA_PARAM);
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		}
		return out;
	}

	@Override
	protected String getSqlTemplate(final int aIndex) {
		return "{? = call %s(?, ?, ?)}";
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		return ERROR_MES_PARAM;
	}

}
