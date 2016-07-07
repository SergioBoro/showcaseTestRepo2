package ru.curs.showcase.app.server;

import java.util.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.security.SecurityParamsFactory;
import ru.curs.showcase.util.exception.*;

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

		if (getProperty(UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL) == null) {
			throw new SettingsFileRequiredPropException(AppInfoSingleton.getAppInfo()
					.getUserdataRoot() + "/" + UserDataUtils.GENERALPROPFILENAME,
					UserDataUtils.RDBMS_PREFIX + UserDataUtils.CELESTA_CONNECTION_URL,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}

		if (getProperty(SecurityParamsFactory.AUTH_SERVER_URL_PARAM) == null) {
			throw new SettingsFileRequiredPropException(AppInfoSingleton.getAppInfo()
					.getUserdataRoot() + "/" + UserDataUtils.GENERALPROPFILENAME,
					SecurityParamsFactory.AUTH_SERVER_URL_PARAM,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
	}
}
