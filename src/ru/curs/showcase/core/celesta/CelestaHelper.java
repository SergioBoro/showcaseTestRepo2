package ru.curs.showcase.core.celesta;

import org.python.core.PyObject;

import ru.curs.celesta.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.SessionUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Класс помощник работы с Celesta.
 * 
 * @author bogatov
 * 
 * @param <T>
 *            тип возвращаемого результата.
 */

public class CelestaHelper<T> {
	private static final int LENGTH_GENERAL_PARAMS = 4;
	private static final int MAIN_CONTEXT_INDEX = 0;
	private static final int ADD_CONTEXT_INDEX = 1;
	private static final int FILTER_CONTEXT_INDEX = 2;
	private static final int SESSION_CONTEXT_INDEX = 3;

	private final CompositeContext contex;
	private final Class<T> resultType;

	/**
	 * 
	 * @param oContex
	 *            контекст.
	 * @param aResultType
	 *            тип возвращаемого результата.
	 */
	public CelestaHelper(final CompositeContext oContex, final Class<T> aResultType) {
		super();
		this.contex = oContex;
		this.resultType = aResultType;
	}

	/**
	 * Выполнить celesta jython скрипт.
	 * 
	 * @param sProcName
	 *            - имя процедуры, полученной от элемента управления
	 * @return результат выполнения скрипта
	 */
	public T runPython(final String sProcName, final Object... additionalParams) {
		Object[] params = mergeAddAndGeneralParameters(this.contex, additionalParams);
		String userSID = SessionUtils.getCurrentUserSID();
		String procName = CelestaUtils.getRealProcName(sProcName);
		PyObject result;
		try {
			result = Celesta.getInstance().runPython(userSID, procName, params);
		} catch (CelestaException ex) {
			throw new CelestaWorkerException("Error run celesta jython script", ex);
		}
		if (result == null) {
			return null;
		}
		Object obj = result.__tojava__(resultType);
		if (obj == null) {
			return null;
		}
		if (obj.getClass().isAssignableFrom(resultType)) {
			return resultType.cast(obj);
		} else {
			throw new CelestaWorkerException("Result is not instance of "
					+ this.resultType.getName());
		}

	}

	protected Object[] mergeAddAndGeneralParameters(final CompositeContext context,
			final Object[] additionalParams) {
		Object[] resultParams;
		if (additionalParams != null && additionalParams.length > 0) {
			resultParams = new Object[additionalParams.length + LENGTH_GENERAL_PARAMS];
			System.arraycopy(additionalParams, 0, resultParams, LENGTH_GENERAL_PARAMS,
					additionalParams.length);
		} else {
			resultParams = new Object[LENGTH_GENERAL_PARAMS];
		}
		resultParams[MAIN_CONTEXT_INDEX] = context.getMain();
		resultParams[ADD_CONTEXT_INDEX] = context.getAdditional();
		resultParams[FILTER_CONTEXT_INDEX] = XMLUtils.convertXmlToJson(context.getFilter());
		resultParams[SESSION_CONTEXT_INDEX] = XMLUtils.convertXmlToJson(context.getSession());
		return resultParams;
	}
}
