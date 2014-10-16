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
	public static final String SCRIPTS_JYTHON_PATH = "scripts/jython";

	private static final JythonIterpretatorFactory INSTANCE = new JythonIterpretatorFactory();

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
		return INSTANCE;
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
		File genScriptDir = new File(getGeneralScriptDir());
		if (genScriptDir.exists()) {
			state.path.append(new PyString(getGeneralScriptDir()));
		}
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

		return new PythonInterpreter(null, state);
	}

	public static String getUserDataScriptDir() {
		return AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/"
				+ SCRIPTS_JYTHON_PATH;
	}

	public static String getGeneralScriptDir() {
		return AppInfoSingleton.getAppInfo().getUserdataRoot() + "/"
				+ UserDataUtils.GENERAL_RES_ROOT + "/" + SCRIPTS_JYTHON_PATH;
	}

	public String getLibJythonDir() {
		return AppInfoSingleton.getAppInfo().getWebAppPath() + libDir;
	}

	@Override
	protected Pool<PythonInterpreter> getLock() {
		return INSTANCE;
	}
}
