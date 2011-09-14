package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.command.DataPanelElementCommand;

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
		WebTextGateway wtgateway = new WebTextDBGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getContext(), getElementInfo());
		WebTextFactory builder = new WebTextFactory(rawWT);
		setResult(builder.build());
	}

}
