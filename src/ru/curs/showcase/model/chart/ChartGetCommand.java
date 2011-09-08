package ru.curs.showcase.model.chart;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Команда получения графика.
 * 
 * @author den
 * 
 */
public final class ChartGetCommand extends DataPanelElementCommand<Chart> {

	public ChartGetCommand(final String aSessionId, final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aSessionId, aContext, aElInfo);
	}

	@Override
	protected void postProcess() {
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(getResult());
	}

	@Override
	protected void mainProc() throws Exception {
		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		ChartDBFactory factory = new ChartDBFactory(raw);
		setResult(factory.build());
	}

}
