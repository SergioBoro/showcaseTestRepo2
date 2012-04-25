package ru.curs.showcase.core.html.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.Description;

/**
 * Реализация шлюза к БД для получения данных типа WebText.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для вебтекста или UI плагина из БД")
public class WebTextDBGateway extends HTMLBasedElementQuery implements HTMLGateway {

	private static final int DATA_INDEX = 7;
	private static final int OUTPUT_INDEX = 8;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	protected String getSqlTemplate(final int index) {
		return "{? = call %s (?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	public int getOutSettingsParam() {
		return OUTPUT_INDEX;
	}

	@Override
	public int getDataParam() {
		return DATA_INDEX;
	}
}
