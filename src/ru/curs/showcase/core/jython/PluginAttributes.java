package ru.curs.showcase.core.jython;

import java.util.Map;

/**
 * Входные атрибуты получения данных для плагина.
 * 
 * @author bogatov
 * 
 */
public class PluginAttributes {
	private Map<String, String> paramMap;

	/**
	 * Получить параметры.
	 * 
	 * @return Map<String, Object> где key - имя параметра, value - значение
	 *         параметра
	 */
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(final Map<String, String> mParamMap) {
		this.paramMap = mParamMap;
	}

}
