package ru.curs.showcase.core.html;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для получения сырых данных для построения XForms.
 * 
 * @author den
 * 
 */
public class HtmlSelector extends SourceSelector<HTMLGateway> {

	public HtmlSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

	@Override
	public HTMLGateway getGateway() {
		HTMLGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new HTMLJythonGateway();
			break;
		case SQL:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				res = new HtmlMSSQLExecGateway();
			} else {
				throw new NotImplementedYetException();
			}
			break;
		case FILE:
			res = new HTMLFileGateway();
			break;
		default:
			res = new HtmlDBGateway();
		}
		return res;
	}
}
