package ru.curs.showcase.core.primelements.datapanel;

import java.io.InputStream;

import org.ehcache.Cache;

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
		Cache<String, DataPanel> cache = AppInfoSingleton.getAppInfo().getDataPanelCache();
		try (PrimElementsGateway gateway = selector.getGateway()) {
			if (gateway instanceof PrimElementsFileGateway) {
				// DataPanel dPan = null;
				// try {
				// dPan =
				// AppInfoSingleton
				// .getAppInfo()
				// .getCache()
				// .get(UserDataUtils.getUserDataCatalog()
				// + "/"
				// + String.format("%s/%s", SettingsFileType.DATAPANEL
				// .getFileDir(), ((PrimElementsFileGateway) gateway)
				// .getSourceName()), new Callable<DataPanel>() {
				// @Override
				// public DataPanel call() throws ExecutionException {
				// DataFile<InputStream> file =
				// gateway.getRawData(action.getContext());
				// DataPanelFactory factory = new DataPanelFactory();
				// return factory.fromStream(file);
				// }
				// });
				// } catch (ExecutionException e) {
				// e.printStackTrace();
				// }
				// setResult(dPan);
				String key =
					UserDataUtils.getUserDataCatalog()
							+ "/"
							+ String.format("%s/%s", SettingsFileType.DATAPANEL.getFileDir(),
									((PrimElementsFileGateway) gateway).getSourceName());

				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				DataPanel value = factory.fromStream(file);

				if (action.getDataPanelLink().getDataPanelCaching())
					cache.put(key, value);

				setResult(value);
			} else if (gateway instanceof DataPanelDBGateway
					|| gateway instanceof DataPanelMSSQLExecGateway
					|| gateway instanceof DataPanelPostgreSQLExecGateway) {
				String key = "";
				if (gateway instanceof DataPanelMSSQLExecGateway)
					key =
						((DataPanelMSSQLExecGateway) gateway).getProcName()
								+ action.getContext().toString();
				else if (gateway instanceof DataPanelPostgreSQLExecGateway)
					key =
						((DataPanelPostgreSQLExecGateway) gateway).getProcName()
								+ action.getContext().toString();
				else
					key =
						((DataPanelDBGateway) gateway).getProcName()
								+ action.getContext().toString();

				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				DataPanel value = factory.fromStream(file);

				if (action.getDataPanelLink().getDataPanelCaching())
					cache.put(key, value);

				setResult(value);
			} else if (gateway instanceof DataPanelCelestaGateway) {
				String key =
					UserDataUtils.getUserDataCatalog() + "/"
							+ String.format("%s/%s", "score", selector.getSourceName())
							+ action.getContext().toString();

				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				DataPanel value = factory.fromStream(file);

				if (action.getDataPanelLink().getDataPanelCaching())
					cache.put(key, value);

				setResult(value);
			} else if (gateway instanceof PrimElementsJythonGateway) {
				String key =
					UserDataUtils.getUserDataCatalog()
							+ "/"
							+ String.format("%s/%s/%s", "scripts",
									SettingsFileType.JYTHON.getFileDir(), selector.getSourceName())
							+ action.getContext().toString();

				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				DataPanel value = factory.fromStream(file);

				if (action.getDataPanelLink().getDataPanelCaching())
					cache.put(key, value);

				setResult(value);
			} else {
				DataFile<InputStream> file = gateway.getRawData(action.getContext());
				DataPanelFactory factory = new DataPanelFactory();
				setResult(factory.fromStream(file));
			}
		}
	}
}
