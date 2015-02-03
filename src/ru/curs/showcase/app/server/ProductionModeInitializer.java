package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.ServletContext;

import org.activiti.engine.*;
import org.slf4j.*;

import ru.curs.showcase.activiti.EventHandlerForActiviti;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.CSSReadException;

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

	private static final String FILE_COPY_ERROR = "Ошибка копирования файла при старте Tomcat: %s";

	private static final String COPY_USERDATA_DIRS_PARAM = "js:css:resources";

	private static final String USER_DATA_DIR_NOT_FOUND_ERROR =
		"Каталог с пользовательскими данными с именем '%s' не найден. ";
	private static final String NOT_ALL_FILES_COPIED_ERROR =
		"В процессе запуска сервера не все файлы были корректно скопированы. "
				+ "Showcase может работать неверно.";

	private static final String GET_USERDATA_PATH_ERROR =
		"Невозможно получить путь к каталогу с пользовательскими данными";
	private static final String SHOWCASE_DATA_GRID_CSS = "/" + UserDataUtils.SOLUTIONS_DIR
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
		// initActiviti();
	}

	public static void initActiviti() {
		if (AppInfoSingleton.getAppInfo().isEnableActiviti()) {
			// log4jConfPath = "/../log4j.properties"
			// PropertyConfigurator.configure(log4jConfPath) # эти две строки
			// нужны для внутреннего логгинга Activiti

			// создаём движок с коннектом к встроенной БД

			// ProcessEngineConfiguration conf =
			// ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();

			String databaseType =
				UserDataUtils.getGeneralOptionalProp("activiti.database.databaseType");

			String jdbcUrl = UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcUrl");

			String jdbcDriver =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcDriver");

			String jdbcUsername =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcUsername");

			String jdbcPassword =
				UserDataUtils.getGeneralOptionalProp("activiti.database.jdbcPassword");

			String history = UserDataUtils.getGeneralOptionalProp("activiti.history.level");

			String databaseTablePrefix =
				UserDataUtils.getGeneralOptionalProp("activiti.database.table.prefix");

			String tablePrefixIsSchema =
				UserDataUtils.getGeneralOptionalProp("activiti.table.prefix.is.schema");

			ProcessEngineConfiguration conf =
				ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
			conf.setDatabaseType(databaseType);
			// conf.setJdbcUrl("jdbc:sqlserver://172.16.1.26\\SQL2008;databasename=activitydimatest");
			conf.setJdbcUrl(jdbcUrl);
			conf.setJdbcDriver(jdbcDriver);
			conf.setJdbcUsername(jdbcUsername);
			conf.setJdbcPassword(jdbcPassword);
			conf.setHistory(history);

			if (databaseTablePrefix != null && !("".equals(databaseTablePrefix))) {
				conf.setDatabaseTablePrefix(databaseTablePrefix);

				if (tablePrefixIsSchema != null && !("".equals(tablePrefixIsSchema))) {
					Boolean b = false;
					try {
						b = Boolean.parseBoolean(tablePrefixIsSchema);
					} catch (Exception e) {
						LOGGER.error("The value of property could not be convert to Boolean.");
					}
					conf.setTablePrefixIsSchema(b);
				}
			}

			ProcessEngine processEngine = conf.buildProcessEngine();
			AppInfoSingleton.getAppInfo().setActivitiProcessEngine(processEngine);
			RuntimeService rs = processEngine.getRuntimeService();
			rs.addEventListener(new EventHandlerForActiviti());
		}

	}

	public static void initUserDatas(final ServletContext aServletContext) {

		AppInfoSingleton.getAppInfo().setSolutionsDirRoot(
				aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR));

		AppInitializer.checkUserDataDir(
				aServletContext.getInitParameter(SHOWCASE_ROOTPATH_USERDATA_PARAM), "context.xml");
		AppInitializer.finishUserdataSetupAndCheckLoggingOverride();
		UserDataUtils.checkUserdatas();
		copyUserDatas(aServletContext);
		AppInfoSingleton.getAppInfo().setServletContainerVersion(aServletContext.getServerInfo());
	}

	private static void initClassPath(final ServletContext aServletContext) {
		String path = aServletContext.getRealPath("index.jsp");
		path = path.replaceAll("\\\\", "/");
		path = path.substring(0, path.lastIndexOf('/'));
		AppInfoSingleton.getAppInfo().setWebAppPath(path);
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
				reader.read(
						AppInfoSingleton.getAppInfo().getWebAppPath()
								+ String.format(SHOWCASE_DATA_GRID_CSS, userdataId),
						HEADER_GAP_SELECTOR, WIDTH_PROP);
		} catch (CSSReadException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(e.getLocalizedMessage());
			}
			return;
		}
		if (width != null) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
				LOGGER.info(String.format(CSS_READ, userdataId, width));
			}
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
		File solutionsDir =
			new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR));
		deleteDirectory(solutionsDir);
		copyGeneralResources(aServletContext);
		copyDefaultUserData(aServletContext);
		copyOtherUserDatas(aServletContext);
		copyClientExtLib(aServletContext);
		copyLoginJsp(aServletContext);
	}

	private static void copyOtherUserDatas(final ServletContext aServletContext) {
		for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
			if (!(ExchangeConstants.DEFAULT_USERDATA.equals(userdataId))) {
				copyUserData(aServletContext, userdataId);
			}
		}
	}

	private static void copyDefaultUserData(final ServletContext aServletContext) {
		copyUserData(aServletContext, ExchangeConstants.DEFAULT_USERDATA);
	}

	private static void copyGeneralResources(final ServletContext aServletContext) {
		File generalResRoot =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
					+ UserDataUtils.GENERAL_RES_ROOT);
		Boolean isAllFilesCopied = true;
		if (generalResRoot.exists()) {
			for (String userdataId : AppInfoSingleton.getAppInfo().getUserdatas().keySet()) {
				File generalDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ "general"));
				File userDataDir =
					new File(aServletContext.getRealPath("/" + UserDataUtils.SOLUTIONS_DIR + "/"
							+ userdataId));
				userDataDir.mkdir();

				isAllFilesCopied =
					copyGeneralDir(aServletContext, generalResRoot, userDataDir, generalDir);
			}
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void copyClientExtLib(final ServletContext aServletContext) {
		Boolean isAllFilesCopied = true;
		File generalResRoot =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
					+ UserDataUtils.GENERAL_RES_ROOT + "/js");

		String[] files = null;
		if (generalResRoot.exists()) {
			files = generalResRoot.list();
		}

		BatchFileProcessor fprocessor = null;
		if (files != null) {
			for (String s : files) {
				if ("clientextlib".equals(s)) {
					fprocessor =
						new BatchFileProcessor(generalResRoot.getAbsolutePath() + "/clientextlib",
								new RegexFilenameFilter("^[.].*", false));
				}
			}
		}

		if (fprocessor != null) {
			try {
				fprocessor.process(new CopyFileAction(aServletContext.getRealPath("/" + "js")));
			} catch (IOException e) {
				isAllFilesCopied = false;
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void copyLoginJsp(final ServletContext aServletContext) {
		Boolean isFileCopied = true;
		File generalResRoot = new File(AppInfoSingleton.getAppInfo().getUserdataRoot());
		File solutions = new File(AppInfoSingleton.getAppInfo().getSolutionsDirRoot());

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(generalResRoot.getAbsolutePath(), new RegexFilenameFilter(
					"^[.].*", false));

		if (fprocessor != null) {
			try {
				fprocessor.processForLoginJsp(new CopyFileAction(solutions.getParent()));
			} catch (IOException e) {
				isFileCopied = false;
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}

		if (!isFileCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static void
			copyUserData(final ServletContext aServletContext, final String userdataId) {
		String userDataCatalog = "";
		UserData us = AppInfoSingleton.getAppInfo().getUserData(userdataId);
		if (us == null) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(GET_USERDATA_PATH_ERROR);
			}
			return;
		}
		userDataCatalog = us.getPath();

		String dirsForCopyStr = COPY_USERDATA_DIRS_PARAM;
		String[] dirsForCopy = dirsForCopyStr.split(":");
		Boolean isAllFilesCopied = true;
		for (int i = 0; i < dirsForCopy.length; i++) {
			File dir = new File(userDataCatalog + "/" + dirsForCopy[i]);
			if (!dir.exists()) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelWarning()) {
					LOGGER.warn(String.format(USER_DATA_DIR_NOT_FOUND_ERROR, dirsForCopy[i]));
				}
				continue;
			}
			isAllFilesCopied =
				isAllFilesCopied
						&& copyUserDataDir(aServletContext, userDataCatalog, dirsForCopy[i],
								userdataId);
		}

		if (!isAllFilesCopied) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(NOT_ALL_FILES_COPIED_ERROR);
			}
		}
	}

	private static Boolean copyGeneralDir(final ServletContext aServletContext,
			final File generalResRoot, final File userDataDir, final File generalDir) {
		Boolean isAllFilesCopied = true;

		File[] files = generalResRoot.listFiles();

		BatchFileProcessor fprocessor =
			new BatchFileProcessor(generalResRoot.getAbsolutePath(), new RegexFilenameFilter(
					"^[.].*", false));

		BatchFileProcessor fprocessorForWebInf =
			new BatchFileProcessor(generalResRoot.getAbsolutePath() + "/WEB-INF",
					new RegexFilenameFilter("^[.].*", false));

		try {
			for (File f : files) {
				if ("WEB-INF".equals(f.getName())) {
					fprocessorForWebInf.processForWebInf(new CopyFileAction(aServletContext
							.getRealPath("/" + "WEB-INF")));
				} else if ("plugins".equals(f.getName()) || "libraries".equals(f.getName())) {
					fprocessor.processForPlugins(new CopyFileAction(generalDir.getAbsolutePath()));
				} else {
					fprocessor.processWithoutWebInf(new CopyFileAction(userDataDir
							.getAbsolutePath()));
				}
			}
		} catch (IOException e) {
			isAllFilesCopied = false;
			LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
		}

		return isAllFilesCopied;
	}

	private static Boolean copyUserDataDir(final ServletContext aServletContext,
			final String userDataCatalog, final String dirName, final String userdataId) {
		Boolean isAllFilesCopied = true;
		BatchFileProcessor fprocessor =
			new BatchFileProcessor(userDataCatalog + "/" + dirName, new RegexFilenameFilter(
					"^[.].*", false));
		try {
			fprocessor.process(new CopyFileAction(aServletContext.getRealPath("/"
					+ UserDataUtils.SOLUTIONS_DIR + "/" + userdataId + "/" + dirName)));
		} catch (IOException e) {
			isAllFilesCopied = false;
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error(String.format(FILE_COPY_ERROR, e.getMessage()));
			}
		}
		return isAllFilesCopied;
	}

	private static boolean deleteDirectory(final File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && files[i].listFiles().length > 0) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}
}
