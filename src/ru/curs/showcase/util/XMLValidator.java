package ru.curs.showcase.util;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import ru.curs.showcase.exception.*;

/**
 * Валидатор схем, позволяющий настроить месторасположение схем и тип исходных
 * данных.
 * 
 * @author den
 * 
 */
public class XMLValidator {
	/**
	 * Стандартное сообщение об ошибке проверки XML.
	 */
	private static final String WRONG_DOC = "Документ не соответствует схеме: ";
	/**
	 * Стандартное расширенное сообщение об ошибке проверки XML.
	 */
	private static final String WRONG_DOC_EXT = "Документ не соответствует схеме %s: ";

	/**
	 * Источник для схем.
	 */
	private XSDSource xsdSource;

	/**
	 * Функция проверки XML (Template Method).
	 * 
	 * @param schema
	 *            - имя файла схемы.
	 * @param source
	 *            - источник XML данных.
	 */
	public void validate(final String schema, final XMLSource source) {
		try {
			Validator validator = createValidator(schema);
			validator.validate(createSource(source));
		} catch (SettingsFileOpenException e) {
			throw e;
		} catch (Exception e) {
			throw new XSDValidateException(getValidateError(schema) + e.getMessage());
		}
	}

	private Source createSource(final XMLSource aSource) throws SAXException {
		return aSource.getExtractor().extract(aSource); // TODO некрасиво
	}

	private Validator createValidator(final String schema) throws SAXException {
		SchemaFactory schemaFactory = XMLUtils.createSchemaFactory();
		File file = getFileForSchema(schema);
		// передавать InputStream и URL нельзя, т.к. в этом случае парсер не
		// находит вложенных схем!
		Schema schemaXSD = schemaFactory.newSchema(file);
		Validator validator = schemaXSD.newValidator();
		return validator;
	}

	private File getFileForSchema(final String aSchema) {
		return xsdSource.getSchema(aSchema);
	}

	public XMLValidator(final XSDSource aXsdSource) {
		super();
		xsdSource = aXsdSource;
	}

	private String getValidateError(final String xsdFileName) {
		if (xsdFileName != null) {
			return String.format(WRONG_DOC_EXT, xsdFileName);
		} else {
			return WRONG_DOC;
		}
	}

	public XSDSource getXsdSource() {
		return xsdSource;
	}

	public void setXsdSource(final XSDSource aXsdSource) {
		xsdSource = aXsdSource;
	}
}
