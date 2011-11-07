package ru.curs.showcase.model.event;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.JythonException;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.*;

/**
 * Шлюз для работы с Jython Server Activity.
 * 
 * @author den
 * 
 */
public class ActivityJythonGateway implements ActivityGateway {

	public static final String LIB_JYTHON_PATH = "../libJython";
	private static final String JYTHON_ERROR =
		"При вызове Jython Server Activity '%s' произошла ошибка";
	public static final String SCRIPTS_JYTHON_PATH = "scripts\\\\jython";

	@Override
	public void exec(final Activity act) {
		PyDictionary dict = new PyDictionary();
		PySystemState state = new PySystemState();
		state.path.append(new PyString(AppInfoSingleton.getAppInfo().getCurUserData().getPath()
				+ "\\\\" + SCRIPTS_JYTHON_PATH));
		state.path.append(new PyString(FileUtils.getClassPath() + LIB_JYTHON_PATH));
		String className = TextUtils.extractFileName(act.getName());
		PythonInterpreter interpreter = new PythonInterpreter(dict, state);

		String cmd = String.format("from %1$s import %1$s\n", className);
		interpreter.exec(cmd);

		PyObject pyClass = interpreter.get(className);
		PyObject pyObj = pyClass.__call__();
		JythonProc proc = (JythonProc) pyObj.__tojava__(JythonProc.class);

		try {
			proc.execute(act.getContext());
		} catch (PyException e) {
			throw new JythonException(String.format(JYTHON_ERROR, act.getName()), e);
		}

	}
}
