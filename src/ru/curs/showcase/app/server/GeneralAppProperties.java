package ru.curs.showcase.app.server;

import java.util.*;

import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Класс, содержащий в себе карту, представляющую список свойств из файла
 * generalapp.properties.
 * 
 * @author s.borodanev
 */

public class GeneralAppProperties {

	private final Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getMap() {
		return map;
	}

	public String getProperty(String key) {
		return map.get(key);
	}

	public void initialize() {
		Properties props = UserDataUtils.getGeneralProperties();
		String key = "";
		String value = "";
		for (Object k : props.keySet()) {
			key = (String) k;
			value = props.getProperty(key);
			getMap().put(key, value);
		}
	}
}
