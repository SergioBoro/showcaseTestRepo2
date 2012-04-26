package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Выбор источника для сохранения XForms.
 * 
 * @author den
 * 
 */
public class XFormSaveSelector extends SourceSelector<HTMLAdvGateway> {

	public XFormSaveSelector(final DataPanelElementProc proc) {
		super(proc.getName());
	}

	@Override
	public HTMLAdvGateway getGateway() {
		HTMLAdvGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new XFormJythonGateway();
			break;
		case SQL:
			if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
				res = new HtmlMSSQLExecGateway();
			} else {
				throw new NotImplementedYetException();
			}
			break;
		case FILE:
			res = new XFormFileGateway();
			break;
		default:
			res = new HtmlDBGateway();
		}
		return res;
	}
}
