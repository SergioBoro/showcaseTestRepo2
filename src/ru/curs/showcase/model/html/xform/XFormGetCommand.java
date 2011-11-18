package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.model.html.*;

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
		XFormSelector selector = new XFormSelector(getElementInfo());
		HTMLGateway gateway = selector.getGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		if (getContext().getFormData() != null) {
			raw.setData(getContext().getFormData());
		}
		XFormFactory factory = new XFormFactory(raw);
		setResult(factory.build());
	}
}
