package ru.curs.showcase.app.server;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

import org.python.util.PythonInterpreter;
import org.slf4j.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.FileUtils;
import ru.curs.showcase.util.xml.XMLUtils;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Инициализатор приложения. Содержит главную функцию initialize, которая должна
 * быть вызвана и при старте TomCat, и при запуске модульных тестов.
 * 
 * @author den
 * 
 */
public final class AppInitializer {

	private static final String USER_DATA_INFO =
		"Добавлен userdata на основе rootpath из '%s' с идентификатором '%s' и путем '%s'";

	private static final String ENABLE_LOG_LEVEL_INFO = "enable.log.level.info";
	private static final String ENABLE_LOG_LEVEL_WARNING = "enable.log.level.warning";
	private static final String ENABLE_LOG_LEVEL_ERROR = "enable.log.level.error";

	private static final String ENABLE_ACTIVITI = "activiti.enable";

	private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);

	public static void finishUserdataSetupAndCheckLoggingOverride() {
		readDefaultUserDatas(FileUtils.GENERAL_PROPERTIES);
		initEnableLogLevels();
		checkAnyUserdataExists();
		setupUserdataLogging();
		setupActivitiUsing();
		AppInfoSingleton.getAppInfo().initWebConsole();
	}

	private static void setupActivitiUsing() {
		String value;
		boolean boolValue;

		value = UserDataUtils.getGeneralOptionalProp(ENABLE_ACTIVITI);
		if (value != null) {
			boolValue = Boolean.valueOf(value);
			AppInfoSingleton.getAppInfo().setEnableActiviti(boolValue);
		}

	}

	private static void initEnableLogLevels() {
		String value;
		boolean boolValue;

		value = UserDataUtils.getGeneralOptionalProp(ENABLE_LOG_LEVEL_INFO);
		if (value != null) {
			boolValue = Boolean.valueOf(value);
			AppInfoSingleton.getAppInfo().setEnableLogLevelInfo(boolValue);
		}

		value = UserDataUtils.getGeneralOptionalProp(ENABLE_LOG_LEVEL_WARNING);
		if (value != null) {
			boolValue = Boolean.valueOf(value);
			AppInfoSingleton.getAppInfo().setEnableLogLevelWarning(boolValue);
		}

		value = UserDataUtils.getGeneralOptionalProp(ENABLE_LOG_LEVEL_ERROR);
		if (value != null) {
			boolValue = Boolean.valueOf(value);
			AppInfoSingleton.getAppInfo().setEnableLogLevelError(boolValue);
		}
	}

	public static void setupUserdataLogging() {
		File logConf =
			new File(AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
					+ AppInfoSingleton.getAppInfo().getUserDataLogConfFile());
		if (!logConf.exists()) {
			return;
		}
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure(logConf.toURI().toURL());
		} catch (JoranException | MalformedURLException e) {
			if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
				LOGGER.error("Ошибка при включении пользовательской конфигурции логгера");
			}
		}
	}

	public static void readDefaultUserDatas(final String file) {
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			String rootpath = FileUtils.getTestUserdataRoot(file);
			checkUserDataDir(rootpath, file);
		}
	}

	private static void checkAnyUserdataExists() {
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			throw new NoUserDatasException();
		}
	}

	private AppInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 */
	public static void initialize() {
		initClassPath();
		XMLUtils.setupSchemaFactory();
		XMLUtils.setupTransformer();
		jythonInit();
	}

	private static void initClassPath() {
		File file = new File(".");
		AppInfoSingleton.getAppInfo().setWebAppPath(file.getAbsolutePath() + "/WebContent");
	}

	/**
	 * Инициализация Jython. Нельзя инициализировать Py.getSystemState() здесь,
	 * т.к. для конкретного интерпретатора все равно будет использоваться свой
	 * SystemState
	 */
	private static void jythonInit() {
		Properties newProps = new Properties();
		newProps.put("python.cachedir", "../tmp");
		PythonInterpreter.initialize(System.getProperties(), newProps, new String[0]);
	}

	public static void checkUserDataDir(final String path, final String file) {
		if (path != null) {
			String rootpath = path.replaceAll("\\\\", "/");
			File dir = new File(rootpath);
			if (!dir.exists()) {
				throw new NoSuchRootPathUserDataException(rootpath);
			}
			AppInfoSingleton.getAppInfo().setUserdataRoot(rootpath);
			String value;
			for (String id : dir.list()) {
				if (id.startsWith(".")) {
					continue;
				}
				if (UserDataUtils.GENERAL_RES_ROOT.equalsIgnoreCase(id)) {
					continue;
				}
				value = rootpath + "/" + id;
				if (!new File(value).isDirectory()) {
					continue;
				}
				AppInfoSingleton.getAppInfo().addUserData(id, value);
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelInfo()) {
					LOGGER.info(String.format(USER_DATA_INFO, file, id, value));
				}

			}
		}
	}
}
