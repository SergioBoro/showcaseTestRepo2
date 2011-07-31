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
	private static final String OUTPUT_COLUMNNAME = "webtextsettings";
	private static final String DATA_COLUMNNAME = "webtextdata";

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.WEBTEXT;
	}

	@Override
	public String getDataParam() {
		return DATA_COLUMNNAME;
	}
}
