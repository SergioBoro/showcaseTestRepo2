package ru.curs.showcase.core.jython;

import java.io.*;
import java.util.regex.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.slf4j.*;

import ru.curs.showcase.core.*;
import ru.curs.showcase.runtime.JythonIterpretatorFactory;
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
	public static final String JYTHON_MARKER = "jython";
	private static final String JYTHON_ERROR =
		"При вызове Jython процедуры '%s' произошла ошибка: %s";
	protected static final String RESULT_FORMAT_ERROR =
		"Из Jython процедуры данные или настройки переданы в неверном формате";
	private static final String UNKNOWN_CLASS_ERROR =
		"Jython процедура вернула объект неизвестного типа";
	private static final String NO_RESULT_ERROR = "Jython процедура не вернула данные";

	private T result;
	private final Class<T> resultType;

	private static final Logger LOGGER = LoggerFactory.getLogger(JythonQuery.class);

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
	public final void runTemplateMethod() {
		PythonInterpreter interpreter = JythonIterpretatorFactory.getInstance().acquire();
		try {
			String parent = getJythonProcName().replaceAll("([.]\\w+)$", "");
			parent = parent.replace('/', '.');
			String className = TextUtils.extractFileName(getJythonProcName());
			File script =
				new File(JythonIterpretatorFactory.getUserDataScriptDir() + "\\\\"
						+ getJythonProcName());
			if (!script.exists()) {
				throw new SettingsFileOpenException(getJythonProcName(), SettingsFileType.JYTHON);
			}
			String cmd =
				String.format(
						"from org.python.core import codecs; codecs.setDefaultEncoding('utf-8'); from %s import %s",
						parent, className);
			try {

				interpreter.setOut(new Writer() {

					@Override
					public void write(final char[] data, final int offset, final int count)
							throws IOException {
						String value = String.valueOf(data, offset, count);
						if (!value.trim().isEmpty()) {
							Marker marker = MarkerFactory.getDetachedMarker(JYTHON_MARKER);
							LOGGER.info(marker, value);
						}
					}

					@Override
					public void flush() throws IOException {
						// ничего не делаем
					}

					@Override
					public void close() throws IOException {
						// ничего не делаем
					}
				});
				LOGGER.info("Инициализировал");
				interpreter.exec(cmd);

				PyObject pyClass = interpreter.get(className);
				PyObject pyObj = pyClass.__call__();
				proc = (JythonProc) pyObj.__tojava__(JythonProc.class);

				analyzeReturn(execute());
			} catch (PyException e) {

				JythonWrongClassException exc =
					JythonWrongClassException.checkForImportError(e.toString(), className);
				if (exc != null) {
					throw exc;
				}

				String error = handleJythonException(e.value.toString());
				throw new JythonException(String.format(JYTHON_ERROR, getJythonProcName(), error),
						e);
			}
		} finally {
			JythonIterpretatorFactory.getInstance().release(interpreter);
		}
		checkErrors();
	}

	private String handleJythonException(final String value) {
		String error = StringEscapeUtils.unescapeJava(value);
		Pattern regex = Pattern.compile("^Exception\\(u'(.+)*',\\)$", Pattern.MULTILINE);
		Matcher regexMatcher = regex.matcher(error);
		if (regexMatcher.matches()) {
			return regexMatcher.group(1);
		}
		return error;
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
