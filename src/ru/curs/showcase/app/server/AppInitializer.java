package ru.curs.showcase.app.server;

import java.io.*;
import java.util.Properties;

import org.python.util.PythonInterpreter;
import org.slf4j.*;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Инициализатор приложения. Содержит главную функцию initialize, которая должна
 * быть вызвана и при старте TomCat, и при запуске модульных тестов.
 * 
 * @author den
 * 
 */
public final class AppInitializer {

	public static final String DIR_SVN = ".svn";

	private static final String PATH_PROPERTIES_ERROR = "Ошибка чтения файла "
			+ FileUtils.PATH_PROPERTIES;

	private static final String USER_DATA_INFO = "Добавлен userdata на основе rootpath из "
			+ FileUtils.PATH_PROPERTIES + " с идентификатором '%s' и путем '%s'";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	public static void readDefaultUserDatas() {
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			readPathProperties();
		}
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
		AppInfoSingleton.getAppInfo().setWebAppPath(file.getAbsolutePath() + "\\WebContent");
	}

	/**
	 * Инициализация Jython. Нельзя инициализировать Py.getSystemState() здесь,
	 * т.к. для конкретного интерпретатора все равно будет использоваться свой
	 * SystemState
	 */
	private static void jythonInit() {
		Properties newProps = new Properties();
		newProps.put("python.cachedir", "..\\tmp");
		PythonInterpreter.initialize(System.getProperties(), newProps, new String[0]);
	}

	/**
	 * Считывает userdata's из FileUtils.PATH_PROPERTIES. Вызывается 1.В
	 * ProductionMode, если в context.xml нет userdata 2.В режиме модульных
	 * тестов
	 * 
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static void readPathProperties() {
		Properties paths = new Properties();
		try {
			InputStream is = FileUtils.loadResToStream(FileUtils.PATH_PROPERTIES);
			try (InputStreamReader reader = new InputStreamReader(is, TextUtils.DEF_ENCODING)) {
				paths.load(reader);
			}

			String rootpath = FileUtils.getTestUserdataRoot();
			if (rootpath != null) {
				File dir = new File(rootpath);
				if (!dir.exists()) {
					throw new NoSuchRootPathUserDataException(rootpath);
				}
				AppInfoSingleton.getAppInfo().setUserdataRoot(rootpath);
				String value;
				for (String id : dir.list()) {
					if (!DIR_SVN.equalsIgnoreCase(id)) {
						value = rootpath + "\\" + id;
						AppInfoSingleton.getAppInfo().addUserData(id, value);
						LOGGER.info(String.format(USER_DATA_INFO, id, value));
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error(PATH_PROPERTIES_ERROR);
		}
	}
}
