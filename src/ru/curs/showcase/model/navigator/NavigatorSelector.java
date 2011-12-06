package ru.curs.showcase.model.navigator;

import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.datapanel.PrimaryElementsFileGateway;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Селектор для источника данных о навигаторе.
 * 
 * @author den
 * 
 */
public class NavigatorSelector extends SourceSelector<PrimaryElementsGateway> {
	private static final String NAVIGATOR_PROCNAME_PARAM = "navigator.proc.name";

	public NavigatorSelector() {
		super(AppProps.getRequiredValueByName(NAVIGATOR_PROCNAME_PARAM));
	}

	@Override
	public PrimaryElementsGateway getGateway() {
		PrimaryElementsGateway res;
		switch (sourceType()) {
		case JYTHON:
			res = new PrimaryElementsJythonGateway();
			break;
		case FILE:
			res = new PrimaryElementsFileGateway(SettingsFileType.NAVIGATOR);
			break;
		default:
			res = new NavigatorDBGateway();
		}
		res.setSourceName(getSourceName());
		return res;
	}
}
