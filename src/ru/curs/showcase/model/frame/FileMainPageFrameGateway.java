package ru.curs.showcase.model.frame;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.SettingsFileOpenException;
import ru.curs.showcase.model.SettingsFileType;
import ru.curs.showcase.util.*;

/**
 * Файловый шлюз для получения фреймов главной страницы.
 * 
 * @author den
 * 
 */
public class FileMainPageFrameGateway implements MainPageFrameGateway {

	@Override
	public String getRawData(final CompositeContext context, final String frameSource) {
		String filepath = String.format("%s/%s", "html", frameSource);
		try {
			InputStream is = AppProps.loadUserDataToStream(filepath);
			return TextUtils.streamToString(is);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, filepath, SettingsFileType.FRAME);
		}
	}

}
