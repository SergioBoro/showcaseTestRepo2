package ru.curs.showcase.model.navigator;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Файловый шлюз для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorFileGateway implements NavigatorGateway {
	/**
	 * Каталог внутри userdata, в котором содержатся описания навигатора.
	 */
	public static final String NAVIGATORSTORAGE = "navigatorstorage";

	private InputStream stream;

	private String fileName;

	@Override
	public InputStream getRawData(final CompositeContext aContext) {
		try {
			stream = AppProps.loadUserDataToStream(NAVIGATORSTORAGE + "\\" + fileName);
			return stream;
		} catch (IOException e) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.NAVIGATOR);
		}
	}

	@Override
	public void releaseResources() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.NAVIGATOR);
		}
	}

	@Override
	public void setSourceName(final String aSourceName) {
		fileName = aSourceName;
	}

	public String getSourceName() {
		return fileName;
	}

	@Override
	public InputStream getRawData(final CompositeContext aContext, final String aSourceName) {
		fileName = aSourceName;
		return getRawData(aContext);
	}

}
