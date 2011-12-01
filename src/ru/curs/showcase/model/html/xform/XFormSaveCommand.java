package ru.curs.showcase.model.html.xform;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.html.XSLTransformationSelector;
import ru.curs.showcase.util.DataFile;
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
		DataPanelElementProc proc = getElementInfo().getSaveProc();
		XSLTransformationSelector selector =
			new XSLTransformationSelector(getContext(), getElementInfo(), proc);
		DataFile<InputStream> transform = selector.getData();
		UserXMLTransformer transformer =
			new UserXMLTransformer(getContext().getFormData(), proc, transform);
		transformer.checkAndTransform();
		XFormGateway gateway = new XFormDBGateway();
		gateway.saveData(getContext(), getElementInfo(), transformer.getStringResult());
	}
}
