package ru.curs.showcase.core.html.plugin;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.core.command.DataPanelElementCommand;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.html.webtext.WebTextSelector;

/**
 * Команда для создания UI плагина.
 * 
 * @author den
 * 
 */
public final class PluginCommand extends DataPanelElementCommand<Plugin> {

	public PluginCommand(final CompositeContext aContext, final DataPanelElementInfo aElementInfo) {
		super(aContext, aElementInfo);
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.PLUGIN;
	}

	@Override
	protected void mainProc() throws Exception {
		WebTextSelector selector = new WebTextSelector(getElementInfo());
		HTMLGateway wtgateway = selector.getGateway();
		HTMLBasedElementRawData rawWT = wtgateway.getRawData(getContext(), getElementInfo());
		PluginFactory factory = new PluginFactory(rawWT);
		setResult(factory.build());
	}

}
