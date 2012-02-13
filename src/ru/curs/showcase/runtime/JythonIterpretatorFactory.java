package ru.curs.showcase.runtime;

import java.io.File;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import ru.curs.showcase.util.exception.ServerLogicError;

/**
 * Пул интерпретаторов Jython.
 * 
 * @author den
 * 
 */
public final class JythonIterpretatorFactory extends PoolByUserdata<PythonInterpreter> {
	private static final String PYTHON_SCRIPTS_DIR_NOT_FOUND =
		"Каталог со стандартными python скриптами '%s' не найден";
	public static final String LIB_JYTHON_PATH = "/WEB-INF/libJython";
	public static final String SCRIPTS_JYTHON_PATH = "scripts\\\\jython";

	private static JythonIterpretatorFactory instance;

	private String libDir = LIB_JYTHON_PATH;

	public void setLibDir(final String aLibDir) {
		libDir = aLibDir;
	}

	public void resetLibDir() {
		libDir = LIB_JYTHON_PATH;
	}

	private JythonIterpretatorFactory() {
		super();
	}

	public static JythonIterpretatorFactory getInstance() {
		if (instance == null) {
			instance = new JythonIterpretatorFactory();
		}
		return instance;
	}

	@Override
	protected void cleanReusable(final PythonInterpreter aReusable) {
		super.cleanReusable(aReusable);
		aReusable.cleanup();
	}

	@Override
	protected PythonInterpreter createReusableItem() {
		PySystemState state = new PySystemState();
		state.path.append(new PyString(getUserDataScriptDir()));
		File jythonLibPath = new File(AppInfoSingleton.getAppInfo().getWebAppPath() + libDir);
		if (!jythonLibPath.exists()) {
			throw new ServerLogicError(String.format(PYTHON_SCRIPTS_DIR_NOT_FOUND, libDir));
		}
		state.path.append(new PyString(jythonLibPath.getAbsolutePath()));

		jythonLibPath =
			new File(AppInfoSingleton.getAppInfo().getWebAppPath() + libDir + "/site-packages");
		if (jythonLibPath.exists()) {
			state.path.append(new PyString(jythonLibPath.getAbsolutePath()));
		}

		PythonInterpreter interpreter = new PythonInterpreter(null, state);
		return interpreter;
	}

	public static String getUserDataScriptDir() {
		return AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "\\\\"
				+ SCRIPTS_JYTHON_PATH;
	}

	@Override
	protected Pool<PythonInterpreter> getLock() {
		return JythonIterpretatorFactory.getInstance();
	}
}
