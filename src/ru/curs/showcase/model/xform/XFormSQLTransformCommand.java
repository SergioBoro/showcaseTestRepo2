package ru.curs.showcase.model.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormSQLTransformCommand extends XFormContextCommand<String> {

	public XFormSQLTransformCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		String decodedContent = XMLUtils.xmlServiceSymbolsToNormal(getContext().getFormData());
		getContext().setFormData(decodedContent);
		XFormGateway gateway = new XFormDBGateway();
		setResult(gateway.sqlTransform(getElementInfo().getProcName(), getContext()));
	}
}
