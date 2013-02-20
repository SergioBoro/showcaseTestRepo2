package ru.curs.showcase.core.plugin;

import ru.beta2.extra.gwt.ui.plugin.RequestData;

/**
 * Шлюз для получения данных, необходимых для компонента plugin.
 * 
 * @author bogatov
 * 
 */
public interface GetDataPluginGateway {
	/**
	 * Получения данных для компонента.
	 * 
	 * @param request
	 *            - параметры запроса.
	 * @return - данные.
	 */
	ResultPluginData getData(RequestData request) throws Exception;

}
