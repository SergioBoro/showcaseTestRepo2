package ru.curs.showcase.model.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.model.HTMLBasedElementRawData;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormGetCommand extends XFormContextCommand<XForm> {

	public XFormGetCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		XFormGateway gateway = new XFormDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		if (getContext().getFormData() != null) {
			raw.setData(getContext().getFormData());
		}
		XFormFactory factory = new XFormFactory(raw);
		setResult(factory.build());
	}
}
