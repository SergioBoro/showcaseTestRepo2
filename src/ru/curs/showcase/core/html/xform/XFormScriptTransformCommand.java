package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.ConnectionFactory;
import ru.curs.showcase.util.exception.NotImplementedYetException;
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

		SourceSelector<HTMLAdvGateway> selector = new SourceSelector<HTMLAdvGateway>(procName) {
			@Override
			public HTMLAdvGateway getGateway() {
				switch (sourceType()) {
				case JYTHON:
					return new XFormJythonGateway();
				case SQL:
					switch (ConnectionFactory.getSQLServerType()) {
					case MSSQL:
						return new HtmlMSSQLExecGateway();
					case POSTGRESQL:
						return new HtmlPostgreSQLExecGateway();
					default:
						throw new NotImplementedYetException();
					}
				default:
					return new HtmlDBGateway();
				}
			}
		};

		HTMLAdvGateway gateway = selector.getGateway();
		setResult(gateway.scriptTransform(procName, getContext()));
	}
}
