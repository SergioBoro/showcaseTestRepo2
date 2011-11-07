/**
 * 
 */
package ru.curs.showcase.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import org.slf4j.*;

import ru.curs.showcase.util.exception.*;

/**
 * Класс, содержащий общие функции для работы с файлами.
 * 
 * @author anlug
 * 
 */
public final class FileUtils {

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
	 * Возвращает путь к classes. getResURL(".") использовать нельзя, т.к. он
	 * некорректно работает на Tomcat, возвращая путь к ${Tomcat}\lib.
	 */
	public static String getClassPath() {
		File tmp = new File(getResURL("ru").getPath());
		return tmp.getParent();
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
	 * Имя файла с настройками путей приложения. Пути рекомендуется задавать
	 * абсолютно, т.к. относительный путь отсчитывается либо от папки с eclipse,
	 * либо от папки с Tomcat и не является постоянным. При задании пути нужно
	 * использовать двойной обратный слэш в качестве разделителя.
	 */
	public static final String PATH_PROPERTIES = "general.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	private FileUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Процедура копирования файла.
	 * 
	 * @param source
	 *            - полный путь к файлу, который будет скопирован (с указанием
	 *            имени файла).
	 * @param destination
	 *            - полный путь места, куда будет скопирован файл (без указания
	 *            имени файла)
	 * @return - возвращает результат копирования файла: true - в случае успеха,
	 *         false - в случае неудачи.
	 */
	public static boolean copyFile(final String source, final String destination) {
		try {
			File sourceFile = new File(source);
			BatchFileProcessor fprocessor =
				new BatchFileProcessor(sourceFile.getPath(), new RegexFilenameFilter(
						sourceFile.getName(), true));
			fprocessor.process(new CopyFileAction(destination));
			return true;
		} catch (FileNotFoundException ex) {
			LOGGER.error(ex.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

		return false;
	}

	/**
	 * Удаляет каталог с подкаталогами и файлами.
	 * 
	 * @param dir
	 *            - удаляемый каталог.
	 * @throws IOException
	 */
	public static void deleteDir(final String dir) throws IOException {
		BatchFileProcessor fprocessor = new BatchFileProcessor(dir, true);
		fprocessor.process(new DeleteFileAction());
	}

	public static String getGeneralOptionalParam(final String paramName) {
		Properties prop = new Properties();
		InputStreamReader reader = new InputStreamReader(loadResToStream(PATH_PROPERTIES));
		try {
			try {
				prop.load(reader);
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(PATH_PROPERTIES, SettingsFileType.PATH_PROPERTIES);
		}
		return prop.getProperty(paramName);
	}
}
