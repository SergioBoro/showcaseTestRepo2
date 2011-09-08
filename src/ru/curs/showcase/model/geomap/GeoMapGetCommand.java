package ru.curs.showcase.model.geomap;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.command.DataPanelElementCommand;

/**
 * Команда получения карты.
 * 
 * @author den
 * 
 */
public final class GeoMapGetCommand extends DataPanelElementCommand<GeoMap> {

	public GeoMapGetCommand(final String aSessionId, final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aSessionId, aContext, aElInfo);
	}

	@Override
	protected void mainProc() throws Exception {
		GeoMapGateway gateway = new GeoMapDBGateway();
		ElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		GeoMapDBFactory factory = new GeoMapDBFactory(raw);
		setResult(factory.build());
	}

	@Override
	protected void postProcess() {
		super.postProcess();

		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(getResult());
	}
}
