package ru.curs.showcase.model.chart;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз для получения данных для графика из БД.
 * 
 * @author den
 * 
 */
public class ChartDBGateway extends CompBasedElementSPCallHelper implements ChartGateway {

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public String getOutSettingsParam() {
		return "chartsettings";
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "exec [dbo].[%s] ?, ?, ?, ?, ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.CHART;
	}
}
