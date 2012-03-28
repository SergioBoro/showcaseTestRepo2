package ru.curs.showcase.util.xml;

import java.io.File;

import javax.xml.validation.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.runtime.UserdataUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Источник схем из userdata.
 * 
 * @author den
 * 
 */
public final class UserDataXSDSource implements XSDSource {

	@Override
	public Schema getSchema(final String sourceName) throws SAXException {
		String xsdFullFileName =
			String.format("%s/%s/%s", UserdataUtils.getUserDataCatalog(), UserdataUtils.SCHEMASDIR,
					sourceName);
		File file = new File(xsdFullFileName);
		if (!file.exists()) {
			throw new SettingsFileOpenException(xsdFullFileName, SettingsFileType.SCHEMA);
		}
		// передавать InputStream и URL нельзя, т.к. в этом случае парсер не
		// находит вложенных схем!
		return XMLUtils.createSchemaForFile(file);
	}

	@Override
	public ExceptionType getExceptionType() {
		return ExceptionType.USER;
	}

}
