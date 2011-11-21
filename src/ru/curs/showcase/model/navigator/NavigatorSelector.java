package ru.curs.showcase.model.navigator;

import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.runtime.AppProps;

/**
 * Селектор для источника данных о навигаторе.
 * 
 * @author den
 * 
 */
public class NavigatorSelector extends SourceSelector<NavigatorGateway> {
	private static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";

	public NavigatorSelector() {
		super(AppProps.getRequiredValueByName(NAVIGATOR_PROCNAME_PARAM));
	}

	@Override
	public NavigatorGateway getGateway() {
		NavigatorGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new NavigatorJythonGateway();
			break;
		case FILE:
			res = new NavigatorFileGateway();
			break;
		default:
			res = new NavigatorDBGateway();
		}
		res.setSourceName(getSourceName());
		return res;
	}
}
