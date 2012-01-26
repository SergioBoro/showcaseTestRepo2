package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Команда преобразования XForm с помощью произвольного скрипта. Является
 * альтернативой преобразованию XForm с помощью XSL.
 * 
 * @author den
 * 
 */
public final class XFormScriptTransformCommand extends XFormContextCommand<String> {

	public XFormScriptTransformCommand(final XFormContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		String decodedContent = XMLUtils.xmlServiceSymbolsToNormal(getContext().getFormData());
		getContext().setFormData(decodedContent);
		String procName = getElementInfo().getProcName();

		SourceSelector<XFormGateway> selector = new SourceSelector<XFormGateway>(procName) {

			@Override
			public XFormGateway getGateway() {
				if (sourceType() == SourceType.JYTHON) {
					return new XFormJythonGateway();
				}
				return new XFormDBGateway();
			}
		};

		XFormGateway gateway = selector.getGateway();
		setResult(gateway.scriptTransform(procName, getContext()));
	}
}
