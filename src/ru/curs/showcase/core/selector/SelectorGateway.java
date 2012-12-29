package ru.curs.showcase.core.selector;

import ru.beta2.extra.gwt.ui.selector.api.DataRequest;

/**
 * Шлюз для получения данных, необходимых для компонента селектора.
 * 
 * @author bogatov
 * 
 */
public interface SelectorGateway {
	/**
	 * Получения данных для компонента.
	 * 
	 * @param req
	 *            - параметры запроса.
	 * @return - данные.
	 */
	ResultSelectorData getData(DataRequest req) throws Exception;

}
