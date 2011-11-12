package ru.curs.showcase.model.event;

import java.io.File;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.JythonException;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.ServerLogicError;

/**
 * Шлюз для работы с Jython Server Activity.
 * 
 * @author den
 * 
 */
public class ActivityJythonGateway implements ActivityGateway {

	private static final String PYTHON_SCRIPTS_DIR_NOT_FOUND =
		"Каталог с python скриптами не найден";
	public static final String LIB_JYTHON_PATH = "/WEB-INF/libJython";
	private static final String JYTHON_ERROR =
		"При вызове Jython Server Activity '%s' произошла ошибка";
	public static final String SCRIPTS_JYTHON_PATH = "scripts\\\\jython";

	/**
	 * Функция вначале инициализирует пути к пользовательским скриптам и
	 * скриптам библиотеки Python. После чего импортирует нужный класс, получает
	 * его экземпляр, конвертирует его в класс Java и вызывает метод execute.
	 */
	@Override
	public void exec(final Activity act) {
		PySystemState state = new PySystemState();
		state.path.append(new PyString(AppInfoSingleton.getAppInfo().getCurUserData().getPath()
				+ "\\\\" + SCRIPTS_JYTHON_PATH));
		File jythonLibPath =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + LIB_JYTHON_PATH);
		if (!jythonLibPath.exists()) {
			throw new ServerLogicError(PYTHON_SCRIPTS_DIR_NOT_FOUND);
		}
		state.path.append(new PyString(jythonLibPath.getAbsolutePath()));

		String className = TextUtils.extractFileName(act.getName());
		PythonInterpreter interpreter = new PythonInterpreter(null, state);

		String cmd = String.format("from %1$s import %1$s\n", className);

		try {
			interpreter.exec(cmd);

			PyObject pyClass = interpreter.get(className);
			PyObject pyObj = pyClass.__call__();
			JythonProc proc = (JythonProc) pyObj.__tojava__(JythonProc.class);

			proc.execute(act.getContext());
		} catch (PyException e) {
			throw new JythonException(String.format(JYTHON_ERROR, act.getName()), e);
		}

	}
}
