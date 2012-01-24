package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementProc;
import ru.curs.showcase.model.SourceSelector;

/**
 * Выбор источника для получения сырых данных для построения XForms.
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
