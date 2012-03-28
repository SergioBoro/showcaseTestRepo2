package ru.curs.showcase.core.html;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.UserdataUtils;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз к файлу для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsFileGateway implements ElementPartsGateway {
	private String sourceName;
	private SettingsFileType type;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		String file = String.format("%s/%s", type.getFileDir(), sourceName);
		try {
			return new DataFile<InputStream>(UserdataUtils.loadUserDataToStream(file), sourceName);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, sourceName, type);
		}
	}

	@Override
	public void setSource(final String aSourceName) {
		sourceName = aSourceName;

	}

	@Override
	public void setType(final SettingsFileType aType) {
		type = aType;
	}

}
