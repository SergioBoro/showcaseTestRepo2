package ru.curs.showcase.core.primelements.datapanel;

import java.io.InputStream;
import java.util.concurrent.*;

import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Команда получения инф. панели.
 * 
 * @author den
 * 
 */
public final class DataPanelGetCommand extends ServiceLayerCommand<DataPanel> {

	private final Action action;

	@InputParam
	public Action getAction() {
		return action;
	}

	public DataPanelGetCommand(final Action aAction) {
		super(aAction.getContext());
		action = aAction;
	}

	@Override
	protected void mainProc() {
		DataPanelSelector selector = new DataPanelSelector(action.getDataPanelLink());
		try (PrimElementsGateway gateway = selector.getGateway()) {
			if (gateway instanceof PrimElementsFileGateway) {
				DataPanel dPan = null;
				try {
					dPan =
						AppInfoSingleton
								.getAppInfo()
								.getCache()
								.get(UserDataUtils.getUserDataCatalog()
										+ "/"
										+ String.format("%s/%s", SettingsFileType.DATAPANEL
												.getFileDir(), ((PrimElementsFileGateway) gateway)
												.getSourceName()), new Callable<DataPanel>() {
									@Override
									public DataPanel call() throws ExecutionException {
										DataFile<InputStream> file =
											gateway.getRawData(action.getContext());
										DataPanelFactory factory = new DataPanelFactory();
										return factory.fromStream(file);
									}
								});
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				setResult(dPan);
			} else {
				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				setResult(factory.fromStream(file));
			}
		}
	}
}
