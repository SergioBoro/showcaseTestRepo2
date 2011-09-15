package ru.curs.showcase.model.frame;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * Файловый шлюз для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
public class MainPageFrameFileGateway implements MainPageFrameGateway {

	private String fileName;

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		setSourceName(frameSource);
		return getRawData(context);
	}

	@Override
	public String getRawData(final CompositeContext aContext) {
		String filepath = String.format("%s/%s", "html", fileName);
		try {
			InputStream is = AppProps.loadUserDataToStream(filepath);
			return TextUtils.streamToString(is);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, filepath, SettingsFileType.FRAME);
		}
	}

	@Override
	public void setSourceName(final String aName) {
		fileName = aName;
	}

	public String getSourceName() {
		return fileName;
	}

}
