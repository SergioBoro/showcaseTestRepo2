package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;
import ru.curs.showcase.model.SettingsFileType;

/**
 * Исключение, возникающее в случае неверного формата значений параметров в
 * файлах настроек приложения.
 * 
 * @author den
 * 
 */
public final class SettingsFilePropValueFormatException extends BaseException {

	/**
	 * Сообщение об ошибке.
	 */
	private static final String ERROR_MES =
		"%s '%s' содержит параметр '%s' c неверным форматом значения";
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -8355753684819986193L;

	/**
	 * Имя файла настроек.
	 */
	private final String fileName;

	/**
	 * Имя отсутствующего свойства.
	 */
	private final String propName;

	/**
	 * Тип файла.
	 */
	private final SettingsFileType fileType;

	public SettingsFilePropValueFormatException(final Throwable aCause, final String aFileName,
			final String aPropName, final SettingsFileType aType) {
		super(ExceptionType.SOLUTION, generateMessage(aFileName, aPropName, aType), aCause);
		fileName = aFileName;
		propName = aPropName;
		fileType = aType;
	}

	public SettingsFilePropValueFormatException(final String aFileName, final String aPropName,
			final SettingsFileType aType) {
		super(ExceptionType.SOLUTION, generateMessage(aFileName, aPropName, aType));
		fileName = aFileName;
		propName = aPropName;
		fileType = aType;
	}

	private static String generateMessage(final String aFileName, final String aPropName,
			final SettingsFileType aType) {
		return String.format(ERROR_MES, aType.getName(), aFileName, aPropName);
	}

	public String getFileName() {
		return fileName;
	}

	public String getPropName() {
		return propName;
	}

	public SettingsFileType getFileType() {
		return fileType;
	}
}
