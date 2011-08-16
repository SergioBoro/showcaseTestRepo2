package ru.curs.showcase.model.geomap;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз для получения данных для графика из БД.
 * 
 * @author den
 * 
 */
public class GeoMapDBGateway extends CompBasedElementSPCallHelper implements GeoMapGateway {

	private static final int OUT_SETTINGS_PARAM = 6;

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public int getOutSettingsParam() {
		return OUT_SETTINGS_PARAM;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{call %s(?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.GEOMAP;
	}
}
