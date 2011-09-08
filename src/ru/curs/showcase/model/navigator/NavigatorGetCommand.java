package ru.curs.showcase.model.navigator;

import java.io.InputStream;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.model.ServiceLayerCommand;

/**
 * Команда получения навигатора.
 * 
 * @author den
 * 
 */
public final class NavigatorGetCommand extends ServiceLayerCommand<Navigator> {

	public NavigatorGetCommand(final String aSessionId, final CompositeContext aContext) {
		super(aSessionId, aContext);
	}

	@Override
	protected void mainProc() {
		NavigatorSelector selector = new NavigatorSelector();
		NavigatorGateway gw = selector.getGateway();
		try {
			InputStream xml = gw.getRawData(getContext());
			NavigatorFactory factory = new NavigatorFactory(getContext());
			setResult(factory.fromStream(xml));
		} finally {
			gw.releaseResources();
		}
	}
}
