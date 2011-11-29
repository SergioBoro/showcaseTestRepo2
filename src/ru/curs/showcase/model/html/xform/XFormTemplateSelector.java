package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.html.*;

/**
 * Класс для выбора источника данных для шаблона XForms.
 * 
 * @author den
 * 
 */
public class XFormTemplateSelector extends SourceSelector<ElementSettingsGateway> {

	public XFormTemplateSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getTemplateName());
	}

	@Override
	public ElementSettingsGateway getGateway() {
		ElementSettingsGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new ElementPartsJythonGateway();
			break;
		case FILE:
			res = new ElementPartsFileGateway();
			break;
		default:
			res = new ElementPartsDBGateway();
		}
		return res;
	}

}
