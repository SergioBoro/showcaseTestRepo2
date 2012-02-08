package ru.curs.showcase.app.server;

import java.io.*;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Инициализатор приложения в рабочем режиме - при запуске Tomcat. Не должен
 * выполняться при запуске модульных тестов. Содержит главную функцию
 * initialize.
 * 
 * @author den
 * 
 */
public final class ProductionModeInitializer {

	private static final String SHOWCASE_ROOTPATH_USERDATA_PARAM = "showcase.rootpath.userdata";

	private static final String USER_DATA_INFO =
		"Добавлен userdata на основе rootpath из context.xml с идентификатором '%s' и путем '%s'";
	private static final String FILE_COPY_ERROR = "Ошибка копирования файла при старте Tomcat: %s";
	private static final String COPY_USERDATA_DIRS_PARAM = "copy.userdata.dirs";
	private static final String USER_DATA_DIR_NOT_FOUND_ERROR =
		"Каталог с пользовательскими данными с именем '%s' не найден. " + "Исправьте параметр "
				+ COPY_USERDATA_DIRS_PARAM + " в файле настроек приложения app.properties.";
	private static final String NOT_ALL_FILES_COPIED_ERROR =
		"В процессе запуска сервера не все файлы были корректно скопированы. "
				+ "Showcase может работать неверно.";
	private static final String APP_PROPS_READ_ERROR = "Ошибка чтения файла настроек приложения";
	private static final String COPY_USERDATA_ON_STARTUP_PARAM = "copy.userdata.on.startup";
	private static final String GET_USERDATA_PATH_ERROR =
		"Невозможно получить путь к каталогу с пользовательскими данными";
	private static final String SHOWCASE_DATA_GRID_CSS = "../../" + AppProps.SOLUTIONS_DIR
			+ "/%s/css/solutionGrid.css";
	public static final String WIDTH_PROP = "width";
	public static final String HEADER_GAP_SELECTOR = ".webmain-SmartGrid .headerGap";
	private static final String CSS_READ = "Из CSS файла '" + SHOWCASE_DATA_GRID_CSS
			+ "' cчитано значение " + HEADER_GAP_SELECTOR + " ." + WIDTH_PROP + " - %s";

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductionModeInitializer.class);

	private ProductionModeInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 * 
	 * @param aServletContext
	 *            - ServletContextEvent.
	 */
	public static void initialize(final ServletContext aServletContext) {
		initClassPath(aServletContext);
		initUserDatas(aServletContext);
		readCSSs();
		JMXBeanRegistrator.register();
	}

	public static void initUserDatas(final ServletContext aServletContext) {
		readServletContext(aServletContext);
		AppInitializer.readDefaultUserDatas();
		AppProps.checkUserdatas();
		copyUserDatas(aServletContext);
	}

	private static void initClassPath(final ServletContext aServletContext) {
		String path = aServletContext.getRealPath("index.jsp");
		path = path.replaceAll("\\\\", "/");
		path = path.substring(0, path.lastIndexOf("/"));
		AppInfoSingleton.getAppInfo().setWebAppPath(path);
	}

	private static void readServletContext(final ServletContext sc) {
		Enumeration<?> en = sc.getInitParameterNames();
		while (en.hasMoreElements()) {
			String name = en.nextElement().toString();
			if (SHOWCASE_ROOTPATH_USERDATA_PARAM.equalsIgnoreCase(name)) {
				String rootpath = sc.getInitParameter(name);

				rootpath = rootpath.replaceAll("\\\\", "/");

				File dir = new File(rootpath);
				if (!dir.exists()) {
					throw new NoSuchRootPathUserDataException(rootpath);
				}
				AppInfoSingleton.getAppInfo().setUserdataRoot(rootpath);
				String value;
				for (String id : dir.list()) {
					if (!AppInitializer.DIR_SVN.equalsIgnoreCase(id)) {
						value = rootpath + "/" + id;
						if (!new File(value).isDirectory()) {
							continue;
						}
						AppInfoSingleton.getAppInfo().addUserData(id, value);
						LOGGER.info(String.format(USER_DATA_INFO, id, value));
					}
				}
			}
		}

		AppInfoSingleton.getAppInfo().setServletContainerVersion(sc.getServerInfo());

	}

	private static void readCSSs() {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			readCSS(userdataId);
		}

	}

	private static void readCSS(final String userdataId) {
		CSSPropReader reader = new CSSPropReader();
		String width = null;
		try {
			width =
				reader.read(String.format(SHOWCASE_DATA_GRID_CSS, userdataId),
						HEADER_GAP_SELECTOR, WIDTH_PROP);
		} catch (CSSReadException e) {
			LOGGER.error(e.getLocalizedMessage());
			return;
		}
		if (width != null) {
			LOGGER.info(String.format(CSS_READ, userdataId, width));
			int value = TextUtils.getIntSizeValue(width);
			if (value > 0) {
				value--;
			}

			UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
			if (us != null) {
				us.setGridColumnGapWidth(value);

			}
		}
	}

	private static void copyUserDatas(final ServletContext aServletContext) {
		if (checkForCopyUserData(ExchangeConstants.DEFAULT_USERDATA)) {
			copyUserData(aServletContext, ExchangeConstants.DEFAULT_USERDATA);
		}

		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			if (!(ExchangeConstants.DEFAULT_USERDATA.equals(userdataId))) {
				if (checkForCopyUserData(userdataId)) {
					copyUserData(aServletContext, userdataId);
				}
			}
		}

	}

	private static boolean checkForCopyUserData(final String userdataId) {
		try {
			String value =
				AppProps.getOptionalValueByName(COPY_USERDATA_ON_STARTUP_PARAM, userdataId);
			if (value != null) {
				return Boolean.parseBoolean(value);
			}
		} catch (SettingsFileOpenException e1) {
			LOGGER.error(APP_PROPS_READ_ERROR);
			return false;
		}
		return false;
	}

	private static void
			copyUserData(final ServletContext aServletContext, final String userdataId) {
		String userDataCatalog = "";
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us == null) {
			LOGGER.error(GET_USERDATA_PATH_ERROR);
			return;
		}
		userDataCatalog = us.getPath();

		String dirsForCopyStr;
		try {
			dirsForCopyStr = AppProps.getOptionalValueByName(COPY_USERDATA_DIRS_PARAM, userdataId);
		} catch (SettingsFileOpenException e) {
			LOGGER.error(GET_USERDATA_PATH_ERROR);
			return;
		}
		if (dirsForCopyStr == null) {
			return;
		}
		String[] dirsForCopy = dirsForCopyStr.split(":");
		Boolean isAllFilesCopied = true;
		for (int i = 0; i < dirsForCopy.length; i++) {
			File dir = new File(userDataCatalog + "/" + dirsForCopy[i]);
			if (!dir.exists()) {
				LOGGER.error(String.format(USER_DATA_DIR_NOT_FOUND_ERROR, dirsForCopy[i]));
				continue;
			}
			isAllFilesCopied =
				isAllFilesCopied
						&& copyUserDataDir(aServletContext, userDataCatalog, dirsForCopy[i],
								userdataId);
		}

		if (!isAllFilesCopied) {
			LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
		}
	}

	private static Boolean copyUserDataDir(final ServletContext aServletContext,
			final String userDataCatalog, final String dirName, final String userdataId) {
		Boolean isAllFilesCopied = true;
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog + "/" + dirName, new RegexFilenameFilter(
					"^[.].*", false));
		try {
			fprocessor.process(new CopyFileAction(aServletContext.getRealPath("/"
					+ AppProps.SOLUTIONS_DIR + "/" + userdataId + "/" + dirName)));
		} catch (IOException e) {
			isAllFilesCopied = false;
			LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
		}
		return isAllFilesCopied;
	}

}
