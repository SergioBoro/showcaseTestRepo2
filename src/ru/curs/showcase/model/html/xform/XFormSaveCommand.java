package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.xml.UserXMLTransformer;

/**
 * Команда сохранения xforms.
 * 
 * @author den
 * 
 */
public final class XFormSaveCommand extends XFormContextCommand<Void> {

	public XFormSaveCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		UserXMLTransformer transformer =
			new UserXMLTransformer(getContext().getFormData(), getElementInfo().getSaveProc(),
					new DataPanelElementContext(getContext(), getElementInfo()));
		transformer.checkAndTransform();
		XFormGateway gateway = new XFormDBGateway();
		gateway.saveData(getContext(), getElementInfo(), transformer.getStringResult());
	}
}