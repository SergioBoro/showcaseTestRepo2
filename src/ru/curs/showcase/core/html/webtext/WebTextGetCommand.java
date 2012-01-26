package ru.curs.showcase.core.html.webtext;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.core.command.DataPanelElementCommand;
import ru.curs.showcase.core.html.*;

/**
 * Команда получения вебтекста.
 * 
 * @author den
 * 
 */
public final class WebTextGetCommand extends DataPanelElementCommand<WebText> {

	public WebTextGetCommand(final CompositeContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		WebTextSelector selector = new WebTextSelector(getElementInfo());
		HTMLGateway wtgateway = selector.getGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getContext(), getElementInfo());
		WebTextFactory builder = new WebTextFactory(rawWT);
		setResult(builder.build());
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.WEBTEXT;
	}

}
