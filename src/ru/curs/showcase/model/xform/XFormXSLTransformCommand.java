package ru.curs.showcase.model.xform;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormXSLTransformCommand extends XFormContextCommand<String> {

	public XFormXSLTransformCommand(final String aSessionId, final XFormContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aSessionId, aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(getContext().getFormData()));
		Document doc = XMLUtils.createBuilder().parse(is);
		setResult(XMLUtils.xsltTransform(doc, getElementInfo().getTransformName()));
	}
}
