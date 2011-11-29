package ru.curs.showcase.model.chart;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.command.DataPanelElementCommand;

/**
 * Команда получения графика.
 * 
 * @author den
 * 
 */
public final class ChartGetCommand extends DataPanelElementCommand<Chart> {

	public ChartGetCommand(final CompositeContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@Override
	protected void postProcess() {
		AdapterForJS adapter = new AdapterForJS();
		adapter.adapt(getResult());
		getResult().setJavaDynamicData(null);
	}

	@Override
	protected void mainProc() throws Exception {
		ChartGateway gateway = new ChartDBGateway();
		ElementRawData raw = gateway.getRawData(getContext(), getElementInfo());
		ChartDBFactory factory = new ChartDBFactory(raw);
		setResult(factory.build());
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.CHART;
	}

}
