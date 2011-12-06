package ru.curs.showcase.model.primelements.datapanel;

import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.primelements.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника инф. панели.
 * 
 * @author den
 * 
 */
public class DataPanelSelector extends SourceSelector<PrimElementsGateway> {

	public DataPanelSelector(final DataPanelLink dpLink) {
		super(dpLink.getDataPanelId());
	}

	@Override
	public PrimElementsGateway getGateway() {
		PrimElementsGateway gateway;
		switch (sourceType()) {
		case JYTHON:
			gateway = new PrimElementsJythonGateway();
			break;
		case FILE:
			gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
			break;
		default:
			gateway = new DataPanelDBGateway();
		}
		gateway.setSourceName(getSourceName());
		return gateway;
	}
}