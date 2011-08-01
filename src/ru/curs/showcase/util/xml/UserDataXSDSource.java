package ru.curs.showcase.util.xml;

import java.io.File;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;

/**
 * Источник схем из userdata.
 * 
 * @author den
 * 
 */
public final class UserDataXSDSource implements XSDSource {

	@Override
	public File getSchema(final String aFileName) {
		String xsdFullFileName =
			String.format("%s/%s/%s", AppProps.getUserDataCatalog(), AppProps.SCHEMASDIR,
					aFileName);
		File file = new File(xsdFullFileName);
		if (!file.exists()) {
			throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
		}
		return file;
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.USER;
	}

}
