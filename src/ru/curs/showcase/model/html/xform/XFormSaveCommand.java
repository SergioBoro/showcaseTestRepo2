package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;

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
		DataPanelElementProc proc = getElementInfo().getSaveProc();

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(getContext().getFormData(), proc, getContext(),
					getElementInfo());
		transformer.transform();

		XFormGateway gateway = new XFormDBGateway();
		gateway.saveData(getContext(), getElementInfo(), transformer.getStringResult());
	}
}
