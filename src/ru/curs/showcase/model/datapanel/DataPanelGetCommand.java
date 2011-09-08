package ru.curs.showcase.model.datapanel;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.model.command.ServiceLayerCommand;
import ru.curs.showcase.util.DataFile;

/**
 * Команда получения инф. панели.
 * 
 * @author den
 * 
 */
public final class DataPanelGetCommand extends ServiceLayerCommand<DataPanel> {

	private final Action action;

	public DataPanelGetCommand(final String aSessionId, final Action aAction) {
		super(aSessionId, aAction.getContext());
		action = aAction;
	}

	@Override
	protected void mainProc() {
		DataPanelSelector selector = new DataPanelSelector(action.getDataPanelLink());
		DataPanelGateway gateway = selector.getGateway();
		try {
			DataFile<InputStream> file = gateway.getRawData(action.getContext());
			DataPanelFactory factory = new DataPanelFactory();
			setResult(factory.fromStream(file));
		} finally {
			gateway.releaseResources();
		}
	}
}
