package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для получения сырых данных для построения грида.
 * 
 * @author den
 * 
 */
public class GridSelector extends SourceSelector<GridGateway> {

	public GridSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

	@Override
	public GridGateway getGateway() {
		GridGateway res;
		switch (sourceType()) {
		case SQL:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				res = new GridMSSQLExecGateway();
			} else {
				throw new NotImplementedYetException();
			}
			break;
		case SP:
			res = new GridDBGateway();
			break;
		default:
			throw new NotImplementedYetException();
		}
		return res;
	}
}
