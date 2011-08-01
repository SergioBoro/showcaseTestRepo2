package ru.curs.showcase.app.server;

import java.io.*;
import java.util.Enumeration;

import javax.servlet.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;

/**
 * Инициализатор приложения в рабочем режиме - при запуске Tomcat. Не должен
 * выполняться при запуске модульных тестов. Содержит главную функцию
 * initialize.
 * 
 * @author den
 * 
 */
public final class ProductionModeInitializer {

	private static final String SHOWCASE_USER_DATA_PARAM = "showcase.user.data";
	private static final String USER_DATA_INFO =
		"Добавлен userdata из context.xml с идентификатором '%s' и путем '%s'";
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

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductionModeInitializer.class);

	private ProductionModeInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 * 
	 * @param arg0
	 *            - ServletContextEvent.
	 */
	public static void initialize(final ServletContextEvent arg0) {
		readServletContext(arg0); // считываем пути к userdata'м вначале
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			AppInitializer.readPathProperties();
		}

		copyUserDatas(arg0);

		readCSSs();

		LOGGER.info("Сформирован массив UserData:");
		for (String id : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			LOGGER.info(id + " | "
					+ AppInfoSingleton.getAppInfo().getUserdatas().get(id).getPath() + " | "
					+ AppInfoSingleton.getAppInfo().getUserdatas().get(id).getGridColumnGapWidth());
		}
	}

	private static void readServletContext(final ServletContextEvent arg0) {
		ServletContext sc = arg0.getServletContext();

		Enumeration<?> en = sc.getInitParameterNames();
		while (en.hasMoreElements()) {
			String name = en.nextElement().toString();
			if (name.toLowerCase().contains(SHOWCASE_USER_DATA_PARAM.toLowerCase())) {
				String id =
					name.substring(0,
							name.toLowerCase().indexOf(SHOWCASE_USER_DATA_PARAM.toLowerCase()))
							.trim();

				if ("".equals(id)) {
					id = ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT;
				} else {
					id = id.substring(0, id.length() - 1);
				}

				String value = sc.getInitParameter(name);
				AppInfoSingleton.getAppInfo().addUserData(id, value);
				LOGGER.info(String.format(USER_DATA_INFO, id, value));
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

	private static void copyUserDatas(final ServletContextEvent arg0) {
		if (checkForCopyUserData(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT)) {
			copyUserData(arg0, ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT);
		}

		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			if (!(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT.equals(userdataId))) {
				if (checkForCopyUserData(userdataId)) {
					copyUserData(arg0, userdataId);
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

	private static void copyUserData(final ServletContextEvent arg0, final String userdataId) {
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
						&& copyUserDataDir(arg0, userDataCatalog, dirsForCopy[i], userdataId);
		}

		if (!isAllFilesCopied) {
			LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
		}
	}

	private static Boolean copyUserDataDir(final ServletContextEvent arg0,
			final String userDataCatalog, final String dirName, final String userdataId) {
		Boolean isAllFilesCopied = true;
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog + "/" + dirName, new RegexFilenameFilter(
					"^[.].*", false));
		try {
			fprocessor.process(new CopyFileAction(arg0.getServletContext().getRealPath(
					"/" + AppProps.SOLUTIONS_DIR + "/" + userdataId + "/" + dirName)));
		} catch (IOException e) {
			isAllFilesCopied = false;
			LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
		}
		return isAllFilesCopied;
	}

}
