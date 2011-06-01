package ru.curs.showcase.exception;

import ru.curs.showcase.app.api.services.ExceptionType;
import ru.curs.showcase.model.SettingsFileType;

/**
 * Исключение, генерируемое при отсутствии в файле настроек приложения
 * необходимого свойства.
 * 
 * @author den
 * 
 */
public class SettingsFileRequiredPropException extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2886682990651933862L;

	/**
	 * Текст ошибки.
	 */
	private static final String ERROR_MES = " %s '%s' не содержит требуемого параметра '%s'";

	/**
	 * Имя файла настроек.
	 */
	private final String fileName;

	/**
	 * Имя отсутствующего свойства.
	 */
	private final String propName;

	/**
	 * Тип файла настроек.
	 */
	private final SettingsFileType fileType;

	public String getPropName() {
		return propName;
	}

	public String getFileName() {
		return fileName;
	}

	public SettingsFileRequiredPropException(final String aFileName, final String aPropName,
			final SettingsFileType aType) {
		super(ExceptionType.SOLUTION, String.format(ERROR_MES, aType.getName(), aFileName,
				aPropName));
		fileName = aFileName;
		propName = aPropName;
		fileType = aType;
	}

	public SettingsFileType getFileType() {
		return fileType;
	}
}
