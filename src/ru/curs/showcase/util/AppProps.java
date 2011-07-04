package ru.curs.showcase.util;

import java.io.*;
import java.net.URL;
import java.util.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.SettingsFileType;

/**
 * Получает проперти приложения из файла properties.
 * 
 */
public final class AppProps {
	public static final String DEF_FOOTER_HEIGTH = "50px";

	public static final String DEF_HEADER_HEIGTH = "50px";

	public static final String HEADER_HEIGHT_PROP = "header.height";

	public static final String FOOTER_HEIGHT_PROP = "footer.height";

	/**
	 * Имя файла с настройками путей приложения. Пути рекомендуется задавать
	 * абсолютно, т.к. относительный путь отсчитывается либо от папки с eclipse,
	 * либо от папки с Tomcat и не является постоянным. При задании пути нужно
	 * использовать двойной обратный слэш в качестве разделителя.
	 */
	public static final String PATH_PROPERTIES = "path.properties";

	/**
	 * Каталог на сервере с решениями (сейчас туда копируются userdata при
	 * старте сервера).
	 */
	public static final String SOLUTIONS_DIR = "solutions";

	/**
	 * Часть названия параметров в app.properties, относящихся к системе
	 * аутентификации(authserver).
	 */
	public static final String AUTHSERVERURL_PART = "authserverurl";

	/**
	 * Имя файла с настройками приложения.
	 */
	public static final String PROPFILENAME = "app.properties";
	/**
	 * XSLTTRANSFORMSDIR.
	 */
	public static final String XSLTTRANSFORMSDIR = "xslttransforms";
	/**
	 * XSLTTRANSFORMSFORGRIDDIR.
	 */
	public static final String XSLTTRANSFORMSFORGRIDDIR = "xslttransformsforgrid";
	/**
	 * SCHEMASDIR.
	 */
	public static final String SCHEMASDIR = "schemas";
	/**
	 * GRIDDATAXSL.
	 */
	public static final String GRIDDATAXSL = "GridData.xsl";

	/**
	 * Каталог для шаблонов XForms и тестовых данных XForms.
	 */
	public static final String XFORMS_DIR = "xforms";

	/**
	 * NAVIGATOR_ICONS_DIRNAME.
	 */
	private static final String NAVIGATOR_ICONS_DIR_NAME = "navigator.icons.dir.name";
	/**
	 * IMAGES_IN_GRID_DIR.
	 */
	private static final String IMAGES_IN_GRID_DIR = "images.in.grid.dir";

	/**
	 * DIR_IN_SOLUTIONS.
	 */
	private static final String DIR_IN_SOLUTIONS = SOLUTIONS_DIR + "/%s/%s";

	/** Список Properties с настройками приложения. */
	private static HashMap<String, Properties> props = new HashMap<String, Properties>();

	private AppProps() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Универсальная функция загрузки внутренних ресурсов Web-приложения по
	 * относительному пути, используя Java ClassLoader (например, файлов
	 * конфигурации). Загрузка идет из папки classes.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу
	 * @return поток с файлом.
	 */
	public static InputStream loadResToStream(final String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream result = classLoader.getResourceAsStream(fileName);
		return result;
	}

	/**
	 * Универсальная функция получения URL внутренних ресурсов Web-приложения по
	 * относительному пути, используя Java ClassLoader (например, файлов
	 * конфигурации). Загрузка идет из папки classes.
	 * 
	 * @param fileName
	 *            - путь к загружаемому ресурсу
	 * @return URL ресурса
	 */
	public static URL getResURL(final String fileName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		URL result = classLoader.getResource(fileName);
		return result;
	}

	/**
	 * Функция загрузки пользовательских ресурсов Web-приложения, используя
	 * стандартный java IO API. Загрузка идет из каталога текущей userdata.
	 * 
	 * @param fileName
	 *            - путь к загружаемому файлу в каталоге userdata
	 * @return поток с файлом.
	 * @throws IOException
	 */
	public static InputStream loadUserDataToStream(final String fileName) throws IOException {
		FileInputStream result =
			new FileInputStream(getUserDataCatalog() + File.separator + fileName);
		return result;
	}

	/**
	 * Функция загрузки пользовательских ресурсов Web-приложения, используя
	 * стандартный java IO API. Загрузка идет из каталога userdata с
	 * идентификатором userdataId.
	 * 
	 * @param fileName
	 *            путь к загружаемому файлу в каталоге userdata
	 * @param userdataId
	 *            идентификатор userdata
	 * @return поток с файлом.
	 * @throws IOException
	 */
	private static InputStream
			loadUserDataToStream(final String fileName, final String userdataId)
					throws IOException {
		FileInputStream result =
			new FileInputStream(getUserDataCatalog(userdataId) + File.separator + fileName);
		return result;
	}

	/**
	 * Получает значение обязательного параметра по его имени из текущей
	 * userdata.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getRequiredValueByName(final String propName) {
		String result = generalReadFunc(propName, null);
		if (result == null) {
			throw new SettingsFileRequiredPropException(PROPFILENAME, propName,
					SettingsFileType.APP_PROPERTIES);
		}
		return result;
	}

	/**
	 * Получает значение обязательного параметра по его имени из userdata с
	 * идентификатором userdataId.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @param userdataId
	 *            идентификатор userdata, из которого будет считан параметр
	 * @return - значение параметра.
	 */
	public static String getRequiredValueByName(final String propName, final String userdataId) {
		String result = generalReadFunc(propName, userdataId);
		if (result == null) {
			throw new SettingsFileRequiredPropException(PROPFILENAME, propName,
					SettingsFileType.APP_PROPERTIES);
		}
		return result;
	}

	/**
	 * Получает значение необязательного параметра по его имени из текущей
	 * userdata.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @return - значение параметра.
	 */
	public static String getOptionalValueByName(final String propName) {
		return generalReadFunc(propName, null);
	}

	/**
	 * Получает значение необязательного параметра по его имени из userdata с
	 * идентификатором userdataId.
	 * 
	 * @param propName
	 *            - имя параметра.
	 * @param userdataId
	 *            идентификатор userdata, из которого будет считан параметр
	 * @return - значение параметра.
	 */
	public static String getOptionalValueByName(final String propName, final String userdataId) {
		return generalReadFunc(propName, userdataId);
	}

	private static String generalReadFunc(final String propName, final String aUserdataId) {
		try {
			String userdataId = aUserdataId;
			if (propName.trim().contains(AUTHSERVERURL_PART)) {
				userdataId = ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT;
			}

			String result = getProperties(userdataId).getProperty(propName);
			if (result != null) {
				result = result.trim();

				if (NAVIGATOR_ICONS_DIR_NAME.equals(propName)
						|| IMAGES_IN_GRID_DIR.equals(propName)) {
					if (userdataId == null) {
						userdataId = AppInfoSingleton.getAppInfo().getCurrentUserDataId();
					}
					result = String.format(DIR_IN_SOLUTIONS, userdataId, result);
				}

			}
			return result;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, PROPFILENAME, SettingsFileType.APP_PROPERTIES);
		}
	}

	private static Properties getProperties(final String aUserdataId) throws IOException {
		String userdataId;
		if (aUserdataId == null) {
			userdataId = AppInfoSingleton.getAppInfo().getCurrentUserDataId();
		} else {
			userdataId = aUserdataId;
		}

		Properties prop = props.get(userdataId);
		if (prop == null) {
			prop = new Properties();
			prop.load(new InputStreamReader(loadUserDataToStream(PROPFILENAME, userdataId), "UTF8"));
			props.put(userdataId, prop);
		}
		return prop;
	}

	/**
	 * Возвращает идентификатор текущей userdata.
	 * 
	 * @return - идентификатор текущей userdata.
	 */
	public static String getUserDataId() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurrentUserDataId();

		if (userdataId == null) {
			userdataId = ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT;
		}

		return userdataId;
	}

	/**
	 * Возвращает каталог с данными пользователя из текущей userdata.
	 * 
	 * @return - каталог.
	 */
	public static String getUserDataCatalog() {
		String userDataCatalog = null;

		String userdataId = AppInfoSingleton.getAppInfo().getCurrentUserDataId();
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us != null) {
			userDataCatalog = us.getPath();
		}

		return userDataCatalog;

	}

	/**
	 * Возвращает каталог с данными пользователя из userdata с идентификатором
	 * userdataId.
	 * 
	 * @param userdataId
	 *            идентификатор userdata
	 * @return - каталог.
	 */
	private static String getUserDataCatalog(final String userdataId) {
		String userDataCatalog = null;

		if (userdataId == null) {
			userDataCatalog = getUserDataCatalog();
		} else {
			UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
			if (us != null) {
				userDataCatalog = us.getPath();
			}
		}

		return userDataCatalog;

	}
}
