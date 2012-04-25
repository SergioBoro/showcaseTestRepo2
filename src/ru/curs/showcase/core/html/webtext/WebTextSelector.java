package ru.curs.showcase.core.html.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор шлюза для элемента в зависимости от текущей конфигурации: например,
 * типа процедуры, определяемого по суффиксу.
 * 
 * @author den
 * 
 */
public class WebTextSelector extends SourceSelector<HTMLGateway> {

	public WebTextSelector(final DataPanelElementInfo aElInfo) {
		super(aElInfo.getProcName());
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
				res = new HTMLBasedElementMSSQLExecGateway();
			} else {
				throw new NotImplementedYetException();
			}
			break;
		case FILE:
			res = new HTMLFileGateway();
			break;
		default:
			res = new WebTextDBGateway();
		}
		return res;
	}
}
