package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Реализация шлюза к БД для получения данных типа WebText.
 * 
 * @author den
 * 
 */
public class WebTextDBGateway extends HTMLBasedSPCallHelper implements WebTextGateway {

	private static final int DATA_INDEX = 6;
	private static final int OUTPUT_INDEX = 7;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{call %s(?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	public int getOutSettingsParam() {
		return OUTPUT_INDEX;
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.WEBTEXT;
	}

	@Override
	public int getDataParam(final int index) {
		return DATA_INDEX;
	}
}
