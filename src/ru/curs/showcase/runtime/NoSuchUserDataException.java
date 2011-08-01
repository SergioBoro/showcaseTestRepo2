package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.BaseException;

/**
 * Исключение, генерируемое в случае обращения к несуществующей userdata.
 * 
 */
public class NoSuchUserDataException extends BaseException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6928633456458398538L;

	private static final String ERROR_MES =
		"Каталог пользовательских данных(userdata) с идентификатором '%s' не задан в context.xml или path.properties";

	/**
	 * Идентификатор userdata.
	 */
	private final String userDataId;

	public String getUserDataId() {
		return userDataId;
	}

	public NoSuchUserDataException(final String aUserDataId) {
		super(ExceptionType.USER, String.format(ERROR_MES, aUserDataId));
		userDataId = aUserDataId;

	}
}
