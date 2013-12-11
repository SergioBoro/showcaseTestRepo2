package ru.curs.showcase.runtime;

import java.io.*;
import java.util.Properties;
import java.util.regex.Pattern;

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

	public static final String INDEX_WELCOME_TAB_CAPTION = "index.welcometabcaption";

	private static final String NAVIGATOR_ICONS_DIR_NAME = "navigator.icons.dir.name";

	private static final String DIR_IN_SOLUTIONS = SOLUTIONS_DIR + "/%s/%s";

	private static final String WEB_CONSOLE_ADD_TEXT_FILES_PARAM = "web.console.add.text.files";

	private static String generalPropFile;

	private static final String INDEX_TITLE = "index.title";
	private static final String LOGIN_TITLE = "login.title";

	public static final String CELESTA_PREFIX = "celesta.";
	public static final String CELESTA_SCORE_PATH = "score.path";
	public static final String CELESTA_DATABASE_CLASSNAME = "database.classname";
	public static final String CELESTA_DATABASE_CONNECTION = "database.connection";
	public static final String CELESTA_PYLIB_PATH = "pylib.path";

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
		return getGeneralProperties().getProperty(paramName);
	}

	private static Properties getGeneralProperties() {
		Properties props = new Properties();
		try {
			InputStream is = new FileInputStream(getGeneralPropFile());
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				props.load(reader);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(getGeneralPropFile(),
					SettingsFileType.GENERAL_APP_PROPERTIES);
		}
		return props;
	}

	public static Properties getGeneralCelestaProperties() {
		Properties generalProps = getGeneralProperties();
		Properties celestaProps = new Properties();

		String scorePath = generalProps.getProperty(CELESTA_PREFIX + CELESTA_SCORE_PATH);
		// if (scorePath == null || scorePath.isEmpty()) {
		// return null;
		// }
		if (!(scorePath == null || scorePath.isEmpty())) {
			celestaProps.put(CELESTA_SCORE_PATH, scorePath);
		}

		String dbClassname = generalProps.getProperty(CELESTA_PREFIX + CELESTA_DATABASE_CLASSNAME);
		if (!(dbClassname == null || dbClassname.isEmpty())) {
			celestaProps.put(CELESTA_DATABASE_CLASSNAME, dbClassname);
		}

		String dbConnection =
			generalProps.getProperty(CELESTA_PREFIX + CELESTA_DATABASE_CONNECTION);
		if (!(dbConnection == null || dbConnection.isEmpty())) {
			celestaProps.put(CELESTA_DATABASE_CONNECTION, dbConnection);
		}
		String pyLibPath = generalProps.getProperty(CELESTA_PREFIX + CELESTA_PYLIB_PATH);
		if (pyLibPath == null || pyLibPath.isEmpty()) {
			pyLibPath = JythonIterpretatorFactory.getInstance().getLibJythonDir();
		}
		celestaProps.put(CELESTA_PYLIB_PATH, pyLibPath);

		return celestaProps;
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

		try {
			Properties props = getGeneralProperties();
			checkAppPropsForWrongSymbols("", props);

			for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
				props = getProperties(userdataId);
				checkAppPropsForWrongSymbols(userdataId, props);
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, getCurrentPropFile(),
					SettingsFileType.APP_PROPERTIES);
		}

		checkUserdataFilesNamesForWrongSymbols(AppInfoSingleton.getAppInfo().getUserdataRoot()
				+ "/" + GENERAL_RES_ROOT);

		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			checkUserdataFilesNamesForWrongSymbols(AppInfoSingleton.getAppInfo()
					.getUserData(userdataId).getPath());
		}

	}

	private static void checkUserdataFilesNamesForWrongSymbols(final String userDataCatalog) {
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog, new RegexFilenameFilter("^[.].*", false));
		try {
			fprocessor.process(new CheckFileNameAction());
		} catch (IOException e) {
			throw new CheckFilesNameException(e.getMessage());
		}
	}

	private static void checkAppPropsForWrongSymbols(final String userdataId,
			final Properties props) {
		String prop;
		String value;
		for (Object opr : props.keySet()) {
			prop = (String) opr;
			if ("﻿#".equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (INDEX_TITLE.equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (INDEX_WELCOME_TAB_CAPTION.equalsIgnoreCase(prop.trim())) {
				continue;
			}
			if (LOGIN_TITLE.equalsIgnoreCase(prop.trim())) {
				continue;
			}

			value = props.getProperty(prop);
			if (checkValueForSpace(value.trim())) {
				if ("".equalsIgnoreCase(userdataId)) {
					throw new GeneralAppPropsValueContainsSpaceException(prop, value);
				} else {
					throw new AppPropsValueContainsSpaceException(userdataId, prop, value);
				}
			}
			if (checkValueForCyrillicSymbols(value.trim())) {
				if ("".equalsIgnoreCase(userdataId)) {
					throw new GeneralAppPropsValueContainsCyrillicSymbolException(prop, value);
				} else {
					throw new AppPropsValueContainsCyrillicSymbolException(userdataId, prop, value);
				}
			}
		}
	}

	public static boolean checkValueForSpace(final String value) {
		Pattern pSpace = Pattern.compile("\\s", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		return pSpace.matcher(value).find();
	}

	public static boolean checkValueForCyrillicSymbols(final String value) {
		// Pattern pCyr =
		// Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE +
		// Pattern.UNICODE_CASE);

		Pattern pCyr = Pattern.compile("[а-яё]", Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		return pCyr.matcher(value).find();
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
