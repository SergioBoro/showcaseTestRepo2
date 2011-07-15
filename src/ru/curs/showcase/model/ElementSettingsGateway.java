package ru.curs.showcase.model;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза для получения настроек элемента инф. панели.
 * 
 * @author den
 * 
 */
public interface ElementSettingsGateway {
	/**
	 * Основной метод для получения настроек.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 */
	ElementRawData getRawData(CompositeContext context, DataPanelElementInfo elementInfo);
}
