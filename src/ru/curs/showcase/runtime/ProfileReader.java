package ru.curs.showcase.runtime;

import java.io.InputStreamReader;
import java.util.Properties;

import ru.curs.showcase.util.exception.*;

/**
 * Базовый класс для чтения профайлов настроек.
 * 
 * @author den
 * 
 */
public abstract class ProfileReader {

	public ProfileReader(final String aProfile) {
		super();
		profileName = generateProfileName(aProfile);
	}

	public void init() {
		try {
			InputStreamReader reader =
				new InputStreamReader(AppProps.loadUserDataToStream(profileName));
			try {
				props.load(reader);
			} finally {
				reader.close();
			}
		} catch (Exception e) {
			throw new SettingsFileOpenException(e, profileName, getSettingsType());
		}
	}

	protected abstract SettingsFileType getSettingsType();

	private String generateProfileName(final String aProfile) {
		if (!getProfileCatalog().isEmpty()) {
			return getProfileCatalog() + "/" + aProfile;
		} else {
			return aProfile;
		}
	}

	protected String getProfileCatalog() {
		return "";
	}

	/**
	 * Properties-файл с настройками.
	 */
	private final Properties props = new Properties();
	/**
	 * Путь к профайлу с настройками.
	 */
	private final String profileName;

	protected Properties getProps() {
		return props;
	}

	protected String getProfileName() {
		return profileName;
	}

	/**
	 * Получает строковое значение параметра по его имени.
	 * 
	 * @param propName
	 *            Название настройки
	 * 
	 * @return Значение настройки
	 * 
	 */
	public String getStringValue(final String propName) {

		String result = props.getProperty(propName);
		if (result != null) {
			result = result.trim();
		}
		return result;

	}

	/**
	 * Стандартная функция для чтения Integer значения из файла настроек.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Integer getIntValue(final String paramName) {
		Integer result = null;
		String value = null;
		value = getStringValue(paramName);
		if (value != null) {
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SettingsFilePropValueFormatException(e, profileName, paramName,
						getSettingsType());
			}
		}
		return result;
	}

	/**
	 * Стандартная функция для чтения Boolean значения из файла настроек.
	 * 
	 * @param paramName
	 *            - имя параметра в файле.
	 * @return - значение параметра.
	 */
	public Boolean getBoolValue(final String paramName) {
		Boolean result = null;
		String value = null;
		value = getStringValue(paramName);
		if (value != null) {
			if (!(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
				throw new SettingsFilePropValueFormatException(profileName, paramName,
						getSettingsType());
			}
			result = Boolean.valueOf(value);
		}
		return result;
	}

	/**
	 * Определение того, что в значении параметра содержится true.
	 * 
	 * @param paramName
	 *            - имя параметра.
	 */
	public boolean isTrueValue(final String paramName) {
		Boolean value = getBoolValue(paramName);
		return (value != null) && value;
	}

}