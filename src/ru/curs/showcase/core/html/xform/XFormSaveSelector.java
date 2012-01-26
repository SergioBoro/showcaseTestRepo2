package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.core.SourceSelector;

/**
 * Выбор источника для сохранения XForms.
 * 
 * @author den
 * 
 */
public class XFormSaveSelector extends SourceSelector<XFormGateway> {

	public XFormSaveSelector(final DataPanelElementProc proc) {
		super(proc.getName());
	}

	@Override
	public XFormGateway getGateway() {
		XFormGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new XFormJythonGateway();
			break;
		case FILE:
			res = new XFormFileGateway();
			break;
		default:
			res = new XFormDBGateway();
		}
		return res;
	}
}
