package ru.curs.showcase.model.html.xform;

import java.io.InputStream;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.html.XSLTransformationSelector;
import ru.curs.showcase.util.DataFile;
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
		XSLTransformationSelector selector =
			new XSLTransformationSelector(getContext(), getElementInfo());
		DataFile<InputStream> transform = selector.getData();
		Document doc = XMLUtils.stringToDocument(getContext().getFormData());
		setResult(XMLUtils.xsltTransform(doc, new DataPanelElementContext(getContext(),
				getElementInfo()), transform));
	}
}
