package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import org.slf4j.*;

import ru.curs.showcase.runtime.AppInfoSingleton;
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

	private static final String SHOWCASE_ROOTPATH_USERDATA_PARAM = "rootpath.userdata";

	/**
	 * PATH_PROPERTIES_ERROR.
	 */
	private static final String PATH_PROPERTIES_ERROR = "Ошибка чтения файла "
			+ FileUtils.PATH_PROPERTIES;

	/**
	 * USER_DATA_INFO.
	 */
	private static final String USER_DATA_INFO = "Добавлен userdata на основе rootpath из "
			+ FileUtils.PATH_PROPERTIES + " с идентификатором '%s' и путем '%s'";

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppAndSessionEventsListener.class);

	private AppInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 */
	public static void initialize() {
		XMLUtils.setupSchemaFactory();
		XMLUtils.setupTransformer();
		JMXMBeanRegistrator.register();
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
			InputStreamReader reader =
				new InputStreamReader(FileUtils.loadResToStream(FileUtils.PATH_PROPERTIES),
						TextUtils.DEF_ENCODING);
			try {
				paths.load(reader);
			} finally {
				reader.close();
			}

			Enumeration<?> en = paths.keys();
			while (en.hasMoreElements()) {
				String name = en.nextElement().toString();
				if (SHOWCASE_ROOTPATH_USERDATA_PARAM.equalsIgnoreCase(name)) {
					String rootpath = paths.getProperty(name);
					File dir = new File(rootpath);
					String value;
					for (String id : dir.list()) {
						if (!ProductionModeInitializer.DIR_SVN.equalsIgnoreCase(id)) {
							value = rootpath + "\\" + id;
							AppInfoSingleton.getAppInfo().addUserData(id, value);
							LOGGER.info(String.format(USER_DATA_INFO, id, value));
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error(PATH_PROPERTIES_ERROR);
		}
	}
}
