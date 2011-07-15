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
	public static final String OUTPUT_COLUMNNAME = "geomapsettings";

	@Override
	public ElementRawData getFactorySource(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "exec [dbo].[%s] ?, ?, ?, ?, ?, ?";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.GEOMAP;
	}
}
