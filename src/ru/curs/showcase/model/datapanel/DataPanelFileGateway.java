package ru.curs.showcase.model.datapanel;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз для получения данных об информационных панелях из файловой системы.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для информационной панели из файла")
public class DataPanelFileGateway implements DataPanelGateway {
	/**
	 * Название параметра в файле настроек приложение, содержащего путь к
	 * каталогу с файлами информационных панелей.
	 */
	public static final String DP_STORAGE_PARAM_NAME = "datapanelstorage";

	private InputStream stream;

	private String dataPanelId;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final String aDataPanelId) {
		setSourceName(aDataPanelId);
		return getRawData(context);
	}

	@Override
	public void setSourceName(final String aDataPanelId) {
		dataPanelId = aDataPanelId;
	}

	public String getSourceName() {
		return dataPanelId;
	}

	@Override
	public void close() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, dataPanelId, SettingsFileType.DATAPANEL);
		}
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext) {
		try {
			stream =
				AppProps.loadUserDataToStream(String.format("%s/%s", DP_STORAGE_PARAM_NAME,
						dataPanelId));
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, dataPanelId, SettingsFileType.DATAPANEL);
		}
		if (stream == null) {
			throw new SettingsFileOpenException(dataPanelId, SettingsFileType.DATAPANEL);
		}
		return new DataFile<InputStream>(stream, dataPanelId);
	}
}
