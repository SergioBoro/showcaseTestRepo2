package ru.curs.showcase.runtime;

import ru.curs.showcase.app.api.ExceptionType;
import ru.curs.showcase.util.exception.BaseException;

/**
 * Исключение, генерируемое в случае отсутствия rootpath userdata.
 * 
 */
public class NoRootPathUserDataException extends BaseException {
	private static final long serialVersionUID = 7395563849883634373L;

	private static final String ERROR_MES =
		"Корневой каталог пользовательских данных(userdata) не задан в context.xml или path.properties, либо он пуст";

	public NoRootPathUserDataException() {
		super(ExceptionType.USER, ERROR_MES);
	}

}
