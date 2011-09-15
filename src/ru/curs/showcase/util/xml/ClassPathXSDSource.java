package ru.curs.showcase.util.xml;

import java.io.File;
import java.net.URL;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Источник схем в classpath.
 * 
 * @author den
 * 
 */
public class ClassPathXSDSource implements XSDSource {

	@Override
	public File getSchema(final String aFileName) {
		String xsdFullFileName = String.format("%s/%s", AppProps.SCHEMASDIR, aFileName);

		// самый простой способ получить путь к ресурсу в classpath в виде
		// строки
		URL xsdURL = AppProps.getResURL(xsdFullFileName);
		if (xsdURL == null) {
			throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
		}
		xsdFullFileName = xsdURL.getFile();
		// AppProps.getResURL меняет пробелы на их код, что не нужно - это будет
		// сделано при создании StreamSource
		xsdFullFileName = xsdFullFileName.replace("%20", " ");
		// создание объекта типа File позволяет работает с путями файловой
		// системы, содержащими русские символы
		File file = new File(xsdFullFileName);
		return file;
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.SOLUTION;
	}
}
