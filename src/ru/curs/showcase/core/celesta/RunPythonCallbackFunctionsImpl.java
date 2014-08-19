package ru.curs.showcase.core.celesta;

import org.python.core.PyObject;

import ru.curs.celesta.RunPythonCallbackFunctions;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.core.UserMessageFactory;

/**
 * Содержит функции обратного вызова при запуске питоновского скрипта.
 * 
 */
public class RunPythonCallbackFunctionsImpl implements RunPythonCallbackFunctions {

	@Override
	public boolean needRollbackTransaction(final PyObject pyObj) {
		boolean result = false;

		if (pyObj != null) {
			Object obj = pyObj.__tojava__(Object.class);
			if ((obj != null) && (obj instanceof UserMessage)) {
				UserMessage um = UserMessage.class.cast(obj);
				UserMessageFactory factory = new UserMessageFactory();
				um = factory.build(um);
				if (um.getType() == MessageType.ERROR) {
					result = true;
				}
			}
		}

		return result;
	}

}
