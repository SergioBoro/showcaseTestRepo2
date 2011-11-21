package ru.curs.showcase.model;

import java.io.File;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Абстрактный класс, содержащий базовые (шаблонные) процедуры для работы с
 * Jython. Аналог SPQuery.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип результата.
 */
public abstract class JythonQuery<T> {
	private static final String PYTHON_SCRIPTS_DIR_NOT_FOUND =
		"Каталог с python скриптами не найден";
	public static final String LIB_JYTHON_PATH = "/WEB-INF/libJython";
	private static final String JYTHON_ERROR =
		"При вызове Jython Server Activity '%s' произошла ошибка";
	public static final String SCRIPTS_JYTHON_PATH = "scripts\\\\jython";
	protected static final String RESULT_FORMAT_ERROR =
		"Из Jython процедуры данные или настройки переданы в неверном формате";

	private T result;
	private JythonProc proc;

	protected abstract void execute();

	protected abstract String getJythonProcName();

	protected abstract UserMessage getUserMessage();

	/**
	 * Функция вначале инициализирует пути к пользовательским скриптам и
	 * скриптам библиотеки Python. После чего импортирует нужный класс, получает
	 * его экземпляр, конвертирует его в класс Java и вызывает метод execute.
	 * Задавать путь к скриптам Python для объекта Py.getSystemState() нельзя,
	 * т.к. он переопределяется для каждого экземпляра интерпретатора.
	 */
	protected final void runTemplateMethod() {
		PythonInterpreter interpreter = createInterpretator();
		String parent = getJythonProcName().replaceAll("([.]\\w+)$", "");
		parent = parent.replace('/', '.');
		String className = TextUtils.extractFileName(getJythonProcName());
		File script = new File(getUserDataScriptDir() + "\\\\" + getJythonProcName());
		if (!script.exists()) {
			throw new SettingsFileOpenException(getJythonProcName(), SettingsFileType.JYTHON);
		}
		String cmd = String.format("from %s import %s", parent, className);
		try {
			interpreter.exec(cmd);

			PyObject pyClass = interpreter.get(className);
			PyObject pyObj = pyClass.__call__();
			proc = (JythonProc) pyObj.__tojava__(JythonProc.class);

			execute();
		} catch (PyException e) {
			throw new JythonException(String.format(JYTHON_ERROR, getJythonProcName()), e);
		}
		checkErrors();
	}

	private PythonInterpreter createInterpretator() {
		PySystemState state = new PySystemState();
		state.path.append(new PyString(getUserDataScriptDir()));
		File jythonLibPath =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + LIB_JYTHON_PATH);
		if (!jythonLibPath.exists()) {
			throw new ServerLogicError(PYTHON_SCRIPTS_DIR_NOT_FOUND);
		}
		state.path.append(new PyString(jythonLibPath.getAbsolutePath()));

		PythonInterpreter interpreter = new PythonInterpreter(null, state);
		return interpreter;
	}

	private String getUserDataScriptDir() {
		return AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\\\"
				+ SCRIPTS_JYTHON_PATH;
	}

	protected T getResult() {
		return result;
	}

	protected void setResult(final T aResult) {
		result = aResult;
	}

	protected JythonProc getProc() {
		return proc;
	}

	protected void checkErrors() {
		if (getUserMessage() != null) {
			UserMessageFactory factory = new UserMessageFactory();
			throw new ValidateException(factory.build(getUserMessage()));
		}
	}

}
