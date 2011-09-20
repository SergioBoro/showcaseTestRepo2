package ru.curs.showcase.model.xform;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormXSLTransformCommand extends XFormContextCommand<String> {

	public XFormXSLTransformCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		Document doc = XMLUtils.stringToDocument(getContext().getFormData());
		setResult(XMLUtils.xsltTransform(doc, new DataPanelElementContext(getContext(),
				getElementInfo())));
	}
}
