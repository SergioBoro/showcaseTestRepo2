package ru.curs.showcase.runtime;

import java.io.*;
import java.util.Properties;

import org.slf4j.MDC;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Получает настройки приложения приложения из файлов properties в каталоге
 * пользовательских данных (userdatas).
 * 
 */
public final class UserDataUtils {

	public static final String GENERAL_RES_ROOT = "general";

	public static final String IMAGES_IN_GRID_DIR = "images.in.grid.dir";

	public static final String CURRENT_USERDATA_TEMPLATE = "${userdata.dir}";

	public static final String DEF_FOOTER_HEIGTH = "50px";

	public static final String DEF_HEADER_HEIGTH = "50px";

	public static final String HEADER_HEIGHT_PROP = "header.height";

	public static final String CSS_PROC_NAME_PROP = "scc.proc.name";

	public static final String FOOTER_HEIGHT_PROP = "footer.height";

	/**
	 * Каталог на работающем сервере с решениями (сейчас туда копируются
	 * userdata при старте сервера).
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

	public static final String XSLTTRANSFORMSFORGRIDDIR = "xslttransformsforgrid";

	public static final String SCHEMASDIR = "schemas";

	public static final String SCRIPTSDIR = "scripts";

	public static final String GRIDDATAXSL = "GridData.xsl";

	private static final String NAVIGATOR_ICONS_DIR_NAME = "navigator.icons.dir.name";

	private static final String DIR_IN_SOLUTIONS = SOLUTIONS_DIR + "/%s/%s";

	private static final String WEB_CONSOLE_ADD_TEXT_FILES_PARAM = "web.console.add.text.files";

	private static String generalPropFile;

	public static void setGeneralPropFile(final String aGeneralPropFile) {
		generalPropFile = aGeneralPropFile;
	}

	private UserDataUtils() {
		throw new UnsupportedOperationException();
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
	static InputStream loadUserDataToStream(final String fileName, final String userdataId)
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
	public static String getRequiredProp(final String propName) {
		String result = getOverridenProp(propName);
		if (result == null) {
			throw new SettingsFileRequiredPropException(getCurrentPropFile(), propName,
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
	public static String getOptionalProp(final String propName) {
		return propReadFunc(propName, null);
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
	public static String getOptionalProp(final String propName, final String userdataId) {
		return propReadFunc(propName, userdataId);
	}

	private static String propReadFunc(final String propName, final String aUserdataId) {
		try {
			String userdataId = aUserdataId;
			if (propName.trim().contains(AUTHSERVERURL_PART)) {
				userdataId = ExchangeConstants.DEFAULT_USERDATA;
			}

			String result = getProperties(userdataId).getProperty(propName);
			if (result != null) {
				result = result.trim();
				result = correctPathToSolutionResources(propName, result);
			}
			return result;
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}
	}

	private static String
			correctPathToSolutionResources(final String propName, final String source) {
		String result = source;
		if (NAVIGATOR_ICONS_DIR_NAME.equals(propName) || IMAGES_IN_GRID_DIR.equals(propName)) {
			String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
			result = String.format(DIR_IN_SOLUTIONS, userdataId, source);
		}
		return result;
	}

	private static Properties getProperties(final String aUserdataId) throws IOException {
		String userdataId;
		if (aUserdataId == null) {
			userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		} else {
			userdataId = aUserdataId;
		}

		Properties prop = new Properties();

		InputStream is = loadUserDataToStream(getCurrentPropFile(), userdataId);
		try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
			prop.load(reader);
		}

		return prop;
	}

	public static void checkAppPropsExists(final String aUserdataId) {
		String fileName = getUserDataCatalog(aUserdataId) + File.separator + PROPFILENAME;
		File propsFile = new File(fileName);
		if (!propsFile.exists()) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.APP_PROPERTIES);
		}
	}

	/**
	 * Возвращает идентификатор текущей userdata.
	 * 
	 * @return - идентификатор текущей userdata.
	 */
	public static String getUserDataId() {
		String userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();

		if (userdataId == null) {
			userdataId = ExchangeConstants.DEFAULT_USERDATA;
		}

		return userdataId;
	}

	/**
	 * Возвращает каталог с данными пользователя из текущей userdata.
	 * 
	 * @return - каталог.
	 */
	public static String getUserDataCatalog() {
		return getUserDataCatalog(null);
	}

	/**
	 * Возвращает каталог с данными пользователя из userdata с идентификатором
	 * userdataId.
	 * 
	 * @param userdataId
	 *            идентификатор userdata
	 * @return - каталог.
	 */
	private static String getUserDataCatalog(final String aUserdataId) {
		String userDataCatalog = null;
		String userdataId = aUserdataId;
		if (userdataId == null) {
			userdataId = AppInfoSingleton.getAppInfo().getCurUserDataId();
		}
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us != null) {
			userDataCatalog = us.getPath();
		} else {
			throw new NoSuchUserDataException(userdataId);
		}

		return userDataCatalog;
	}

	/**
	 * Функция по подстановке стандартных параметров приложения вместо их
	 * шаблонов.
	 * 
	 * @param source
	 *            - исходный текст.
	 */
	public static String replaceVariables(final String source) {
		String value =
			source.replace("${" + IMAGES_IN_GRID_DIR + "}", getRequiredProp(IMAGES_IN_GRID_DIR));
		value =
			value.replace(CURRENT_USERDATA_TEMPLATE, String.format("solutions/%s",
					AppInfoSingleton.getAppInfo().getCurUserDataId()));
		return value;
	}

	public static String getGeneralOptionalProp(final String paramName) {
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream(getGeneralPropFile());
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				prop.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(getGeneralPropFile(),
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return prop.getProperty(paramName);
	}

	private static String getGeneralPropFile() {
		if (generalPropFile == null) {
			return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/" + PROPFILENAME;
		}
		return generalPropFile;
	}

	public static String getGeneralRequiredProp(final String propName) {
		String value = getGeneralOptionalProp(propName);
		if (value == null) {
			throw new SettingsFileRequiredPropException(PROPFILENAME, propName,
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return value;
	}

	public static String getGeoMapKey(final String engine, final String host) {
		String realHost = host;
		final String localhost = "localhost";
		if ("127.0.0.1".equals(host)) {
			realHost = localhost;
		}
		String userdataRoot = AppInfoSingleton.getAppInfo().getUserdataRoot();
		String path = String.format("%s/geomap.key.%s.properties", userdataRoot, realHost);
		Properties props = new Properties();

		FileInputStream is;
		try {
			is = new FileInputStream(path);
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				props.load(reader);
			} catch (IOException e) {
				throw new SettingsFileOpenException(e, path, SettingsFileType.GEOMAP_KEYS);
			}
		} catch (FileNotFoundException e) {
			if (localhost.equals(realHost)) {
				return "";
			} else {
				return getGeoMapKey(engine, localhost);
			}
		}
		return props.getProperty(engine, "");

	}

	public static void checkUserdatas() {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			checkAppPropsExists(userdataId);
		}
	}

	public static Boolean isTextFile(final Object obj) {
		DataFile<?> file = (DataFile<?>) obj;
		String fromAppProps = getGeneralOptionalProp(WEB_CONSOLE_ADD_TEXT_FILES_PARAM);
		if (fromAppProps != null) {
			String[] userTextExtensions = fromAppProps.split(":");
			for (String ext : userTextExtensions) {
				if (file.getName().endsWith(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getCurrentPropFile() {
		String overrided = MDC.get(CommandContext.PROP_FILE_TAG);
		if ((overrided != null) && (!overrided.isEmpty())) {
			return overrided;
		}
		return PROPFILENAME;
	}

	private static String getOverridenProp(final String aPropName) {
		String overrided = MDC.get(aPropName);
		if ((overrided != null) && (!overrided.isEmpty())) {
			return overrided;
		}
		return propReadFunc(aPropName, null);
	}
}
