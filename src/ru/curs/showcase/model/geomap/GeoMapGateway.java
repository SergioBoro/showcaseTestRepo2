package ru.curs.showcase.model.geomap;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз для получения "сырых" данных для построения карты.
 * 
 * @author den
 * 
 */
public interface GeoMapGateway {
	/**
	 * Основной метод, возвращающий данные для построения карты. Данные
	 * возвращаются в виде фабрики, уже готовой к построению графика.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - фабрика.
	 */
	ElementRawData getRawData(CompositeContext context, DataPanelElementInfo element);
}
