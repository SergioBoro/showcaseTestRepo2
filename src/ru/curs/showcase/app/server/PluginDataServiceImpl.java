package ru.curs.showcase.app.server;

import org.slf4j.*;

import ru.beta2.extra.gwt.ui.plugin.*;
import ru.curs.showcase.core.plugin.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Реализация сервисного интерфейса получения данных.
 * 
 * @author bogatov
 * 
 */
public class PluginDataServiceImpl extends RemoteServiceServlet implements PluginDataService {
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE_ERROR =
		"При получении данных для плагина возникла ошибка: ";
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginDataServiceImpl.class);

	@Override
	public ResponceData getPluginData(final RequestData oRequest) {
		ResponceData responceData = new ResponceData();
		try {
			GetDataPluginCommand command = new GetDataPluginCommand(oRequest);
			ResultPluginData result = command.execute();
			responceData.setJsonData(result.getData());
		} catch (Exception e) {
			LOGGER.error(MESSAGE_ERROR + e.getMessage());
		}
		return responceData;
	}

}
