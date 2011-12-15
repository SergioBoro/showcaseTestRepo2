package ru.curs.showcase.model.geomap;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.GeoMap;
import ru.curs.showcase.model.AdapterForJS;
import ru.curs.showcase.model.command.DataPanelElementCommand;
import ru.curs.showcase.model.grid.RecordSetElementGateway;
import ru.curs.showcase.model.sp.RecordSetElementRawData;

/**
 * Команда получения карты.
 * 
 * @author den
 * 
 */
public final class GeoMapGetCommand extends DataPanelElementCommand<GeoMap> {

	public GeoMapGetCommand(final CompositeContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GEOMAP;
	}

	@Override
	protected void mainProc() throws Exception {
		RecordSetElementGateway<CompositeContext> gateway = new GeoMapDBGateway();
		RecordSetElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		GeoMapFactory factory = new GeoMapFactory(raw);
		setResult(factory.build());
	}

	@Override
	protected void postProcess() {
		super.postProcess();

		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(getResult());
		getResult().setJavaDynamicData(null);
	}
}
