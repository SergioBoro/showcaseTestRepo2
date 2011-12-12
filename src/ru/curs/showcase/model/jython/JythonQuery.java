package ru.curs.showcase.model.jython;

import java.io.File;
import java.util.regex.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.model.*;
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
		"При вызове Jython процедуры '%s' произошла ошибка: %s";
	public static final String SCRIPTS_JYTHON_PATH = "scripts\\\\jython";
	protected static final String RESULT_FORMAT_ERROR =
		"Из Jython процедуры данные или настройки переданы в неверном формате";
	private static final String UNKNOWN_CLASS_ERROR =
		"Jython процедура вернула объект неизвестного типа";
	private static final String NO_RESULT_ERROR = "Jython процедура не вернула данные";

	private T result;
	private final Class<T> resultType;

	protected JythonQuery(final Class<T> aResultType) {
		super();
		resultType = aResultType;
	}

	private JythonProc proc;
	private UserMessage userMessage;

	protected abstract Object execute();

	protected abstract String getJythonProcName();

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

			analyzeReturn(execute());
		} catch (PyException e) {
			String error = StringEscapeUtils.unescapeJava(e.value.toString());
			Pattern regex = Pattern.compile("^Exception\\(u'(.+)*',\\)$", Pattern.MULTILINE);
			Matcher regexMatcher = regex.matcher(error);
			if (regexMatcher.matches()) {
				error = regexMatcher.group(1);
			}

			throw new JythonException(String.format(JYTHON_ERROR, getJythonProcName(), error), e);
		}
		checkErrors();
	}

	@SuppressWarnings("unchecked")
	private void analyzeReturn(final Object ret) {
		if (ret != null) {
			if (ret.getClass() == resultType) {
				result = (T) ret;
			} else if (ret.getClass() == UserMessage.class) {
				userMessage = (UserMessage) ret;
			} else {
				throw new JythonException(UNKNOWN_CLASS_ERROR);
			}
		}
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

		jythonLibPath =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + LIB_JYTHON_PATH
					+ "/site-packages");
		if (jythonLibPath.exists()) {
			state.path.append(new PyString(jythonLibPath.getAbsolutePath()));
		}

		PythonInterpreter interpreter = new PythonInterpreter(null, state);
		return interpreter;
	}

	private String getUserDataScriptDir() {
		return AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\\\"
				+ SCRIPTS_JYTHON_PATH;
	}

	public T getResult() {
		return result;
	}

	public void setResult(final T aResult) {
		result = aResult;
	}

	protected JythonProc getProc() {
		return proc;
	}

	private void checkErrors() {
		if (getUserMessage() != null) {
			UserMessageFactory factory = new UserMessageFactory();
			throw new ValidateException(factory.build(getUserMessage()));
		} else if ((getResult() == null) && (waitningResult())) {
			throw new JythonException(NO_RESULT_ERROR);
		}
	}

	private boolean waitningResult() {
		return resultType != Void.class;
	}

	public final UserMessage getUserMessage() {
		return userMessage;
	}

}
