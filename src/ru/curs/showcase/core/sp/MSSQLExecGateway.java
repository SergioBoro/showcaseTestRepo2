package ru.curs.showcase.core.sp;

import java.io.IOException;
import java.sql.SQLException;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Базовый шлюз для выполнения sql файлов в MSSQL СУБД. Выполнение происходит
 * при помощи процедуры sp_executesql. 2 первых параметра процедуры: это
 * содержимое sql файла и список параметров. 2 последних: код возврата и
 * сообщение об ошибке.
 * 
 * @author den
 * 
 */
public abstract class MSSQLExecGateway extends SPQuery {

	public static final String SCRIPTS_SQL_DIR = "scripts/sql/";

	public MSSQLExecGateway() {
		super();
	}

	protected String getParamsDeclaration() {
		return "@return int output, @error_mes varchar(MAX) output";
	}

	@Override
	protected int getReturnParamIndex() {
		return getParamCount() - 1;
	}

	@Override
	protected int getErrorMesIndex(final int aIndex) {
		return getParamCount();
	}

	@Override
	protected void prepareSQL() throws SQLException {
		if (getConn() == null) {
			setConn(ConnectionFactory.getInstance().acquire());
		}
		setStatement(getConn().prepareCall(getSqlText()));
		try {
			setStringParam(1,
					TextUtils.streamToString(UserDataUtils.loadUserDataToStream(getFileName())));
		} catch (IOException e) {
			throw new SettingsFileOpenException(getFileName(), SettingsFileType.SQL);
		}
		setStringParam(2, getParamsDeclaration());
		addErrorMesParams();
	}

	protected String getFileName() {
		return SCRIPTS_SQL_DIR + getProcName();
	}

	protected void addErrorMesParams() throws SQLException {
		getStatement().registerOutParameter(getParamCount() - 1, java.sql.Types.INTEGER);
		getStatement().registerOutParameter(getParamCount(), java.sql.Types.VARCHAR);
	}

	protected int getParamCount() {
		return getParamsDeclaration().split(",").length + 2;
	}

	@Override
	protected String getSqlTemplate(final int aIndex) {
		String template = "{call sp_executesql (?, ?, %s ?, ?)}";
		String specialParams = "";
		for (int i = 0; i < getParamCount() - 2 - 2; i++) {
			specialParams += "?, ";
		}
		return String.format(template, specialParams);
	}

	@Override
	protected String getSqlText() {
		return getSqlTemplate(0);
	}

}