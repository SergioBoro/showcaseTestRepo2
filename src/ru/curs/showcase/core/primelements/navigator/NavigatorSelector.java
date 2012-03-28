package ru.curs.showcase.core.primelements.navigator;

import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.runtime.UserdataUtils;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Селектор для источника данных о навигаторе.
 * 
 * @author den
 * 
 */
public class NavigatorSelector extends SourceSelector<PrimElementsGateway> {
	private static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";

	public NavigatorSelector() {
		super(UserdataUtils.getRequiredProp(NAVIGATOR_PROCNAME_PARAM));
	}

	@Override
	public PrimElementsGateway getGateway() {
		PrimElementsGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new PrimElementsJythonGateway();
			break;
		case FILE:
			res = new PrimElementsFileGateway(SettingsFileType.NAVIGATOR);
			break;
		default:
			res = new NavigatorDBGateway();
		}
		res.setSourceName(getSourceName());
		return res;
	}
}
