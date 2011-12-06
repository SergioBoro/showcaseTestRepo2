package ru.curs.showcase.model.datapanel;

import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.navigator.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника инф. панели.
 * 
 * @author den
 * 
 */
public class DataPanelSelector extends SourceSelector<PrimaryElementsGateway> {

	public DataPanelSelector(final DataPanelLink dpLink) {
		super(dpLink.getDataPanelId());
	}

	@Override
	public PrimaryElementsGateway getGateway() {
		PrimaryElementsGateway gateway;
		switch (sourceType()) {
		case JYTHON:
			gateway = new PrimaryElementsJythonGateway();
			break;
		case FILE:
			gateway = new PrimaryElementsFileGateway(SettingsFileType.DATAPANEL);
			break;
		default:
			gateway = new DataPanelDBGateway();
		}
		gateway.setSourceName(getSourceName());
		return gateway;
	}
}
