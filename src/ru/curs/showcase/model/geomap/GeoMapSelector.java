package ru.curs.showcase.model.geomap;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.grid.RecordSetElementGateway;
import ru.curs.showcase.model.jython.RecordSetElementJythonGateway;
import ru.curs.showcase.util.exception.NotImplementedYetException;

/**
 * Класс для выбора шлюза по имени источника для графиков.
 * 
 * @author den
 * 
 */
public class GeoMapSelector extends SourceSelector<RecordSetElementGateway<CompositeContext>> {

	@Override
	public RecordSetElementGateway<CompositeContext> getGateway() {
		RecordSetElementGateway<CompositeContext> gateway = null;
		switch (sourceType()) {
		case JYTHON:
			gateway = new RecordSetElementJythonGateway();
			break;
		case FILE:
			throw new NotImplementedYetException();
		default:
			gateway = new GeoMapDBGateway();
			break;
		}
		return gateway;
	}

	public GeoMapSelector(final DataPanelElementInfo elInfo) {
		super(elInfo.getProcName());
	}

}
