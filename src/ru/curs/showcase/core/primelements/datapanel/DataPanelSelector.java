package ru.curs.showcase.core.primelements.datapanel;

import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника инф. панели.
 * 
 * @author den
 * 
 */
public class DataPanelSelector extends SourceSelector<PrimElementsGateway> {

	public DataPanelSelector(final DataPanelLink dpLink) {
		super(dpLink.getDataPanelId().getString());
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
