package ru.curs.showcase.model.sp;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.DBConnectException;

/**
 * Базовый класс, содержащий необработанные XML данные и метаданные элемента
 * инф.панели.
 * 
 * @author den
 * 
 */
public class RecordSetElementRawData extends ElementRawData implements Closeable {
	/**
	 * Вспомогательный модуль для получения необходимых данных из БД.
	 * Используется при необходимости считывания нескольких блоков данных в
	 * определенном порядке.
	 */
	private final ElementSPQuery spQuery;

	private final PreparedStatement[] statement;

	private int statementIndex = 0;

	public RecordSetElementRawData(final InputStream props,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext,
			final PreparedStatement[] aStatement) {
		super(aElementInfo, aContext, props);
		spQuery = null;
		statement = aStatement;
	}

	public RecordSetElementRawData(final ElementSPQuery aSPQuery,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		super(aElementInfo, aContext);
		spQuery = aSPQuery;
		statement = null;
	}

	public RecordSetElementRawData(final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		super(aElementInfo, aContext);
		spQuery = null;
		statement = null;
	}

	/**
	 * Функция принудительно освобождает ресурсы, используемые шлюзом для
	 * получения данных. Должна быть вызвана после работы фабрики по построению
	 * навигатора.
	 * 
	 */
	@Override
	public void close() {
		if (statement != null) {
			try {
				statement[0].getConnection().close();
			} catch (SQLException e) {
				throw new DBConnectException(e);
			}
		} else {
			spQuery.close();
		}
	}

	public void checkErrorCode() {
		if (spQuery != null) {
			spQuery.checkErrorCode();
		}
	}

	public PreparedStatement getStatement() {
		if (statement != null) {
			return statement[0];
		}
		return spQuery.getStatement();
	}

	private boolean hasResultSet() throws SQLException {
		if (statement != null) {
			return statementIndex < statement.length;
		}
		if (statementIndex > 0) {
			return spQuery.getStatement().getMoreResults();
		}
		return true;
	}

	public ResultSet nextResultSet() {
		try {
			if (!hasResultSet()) {
				return null;
			}
			if (statement != null) {
				return statement[statementIndex++].getResultSet();
			}
			statementIndex++;
			return spQuery.getStatement().getResultSet();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	/**
	 * Подготавливает настройки элемента.
	 * 
	 */
	public void prepareSettings() {
		if (getSettings() != null) {
			return;
		}
		try {
			setSettings(spQuery.getValidatedSettings());
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}
}