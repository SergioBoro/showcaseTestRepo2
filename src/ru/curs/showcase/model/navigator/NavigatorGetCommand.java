package ru.curs.showcase.model.navigator;

import java.io.InputStream;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.model.command.ServiceLayerCommand;

/**
 * Команда получения навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorGetCommand extends ServiceLayerCommand<Navigator> {

	public NavigatorGetCommand(final CompositeContext aContext) {
		super(aContext);
	}

	@Override
	protected void mainProc() {
		NavigatorSelector selector = new NavigatorSelector();

		try (NavigatorGateway gw = selector.getGateway()) {
			InputStream xml = gw.getRawData(getContext());
			NavigatorFactory factory = new NavigatorFactory(getContext());
			setResult(factory.fromStream(xml));
		}
	}
}
