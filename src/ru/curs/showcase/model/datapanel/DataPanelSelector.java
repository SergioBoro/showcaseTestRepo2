package ru.curs.showcase.model.datapanel;

import ru.curs.showcase.app.api.event.DataPanelLink;
import ru.curs.showcase.model.SourceSelector;

/**
 * Класс для выбора источника инф. панели.
 * 
 * @author den
 * 
 */
public class DataPanelSelector extends SourceSelector<DataPanelGateway> {

	public DataPanelSelector(final DataPanelLink dpLink) {
		super();
		setSourceName(dpLink.getDataPanelId());
	}

	@Override
	public DataPanelGateway getGateway() {
		DataPanelGateway gateway;
		if (isFile()) {
			gateway = new DataPanelFileGateway();
		} else {
			gateway = new DataPanelDBGateway();
		}
		gateway.setSourceName(getSourceName());
		return gateway;
	}

	@Override
	protected String getFileExt() {
		return "xml";
	}

}
