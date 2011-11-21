package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.html.*;

/**
 * Выбор источника для получения сырых данных для построения XForms.
 * 
 * @author den
 * 
 */
public class XFormSelector extends SourceSelector<HTMLGateway> {

	public XFormSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

	@Override
	public HTMLGateway getGateway() {
		HTMLGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new HTMLJythonGateway();
			break;
		case FILE:
			res = new HTMLFileGateway();
			break;
		default:
			res = new XFormDBGateway();
		}
		return res;
	}
}
