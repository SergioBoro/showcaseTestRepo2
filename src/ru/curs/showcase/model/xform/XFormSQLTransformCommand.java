package ru.curs.showcase.model.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда получения xforms.
 * 
 * @author den
 * 
 */
public final class XFormSQLTransformCommand extends XFormContextCommand<String> {

	private String decodedContent;

	public XFormSQLTransformCommand(final String aSessionId, final XFormContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aSessionId, aContext, aElInfo);
	}

	@Override
	protected void preProcess() throws GeneralException {
		super.preProcess();

		decodedContent = XMLUtils.xmlServiceSymbolsToNormal(getContext().getFormData());
	}

	@Override
	protected void mainProc() throws Exception {
		XFormGateway gateway = new XFormDBGateway();
		setResult(gateway.sqlTransform(getElementInfo().getProcName(), decodedContent));
	}
}
