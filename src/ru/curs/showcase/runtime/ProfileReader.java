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
public class ProfileReader {
	/**
	 * Properties-файл с настройками.
	 */
	private final Properties props = new Properties();
	/**
	 * Путь к профайлу с настройками.
	 */
	private final String profileName;

	private final SettingsFileType settingsFileType;

	public ProfileReader(final String aProfile, final SettingsFileType aSettingsFileType) {
		super();
		settingsFileType = aSettingsFileType;
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

	protected String getProfileCatalog() {
		return getSettingsType().getFileDir();
	}

	protected SettingsFileType getSettingsType() {
		return settingsFileType;
	}

	private String generateProfileName(final String aProfile) {
		if (!getProfileCatalog().isEmpty()) {
			return getProfileCatalog() + "/" + aProfile;
		} else {
			return aProfile;
		}
	}

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
			if (!("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))) {
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