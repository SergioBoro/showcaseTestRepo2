package ru.curs.showcase.core.html;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз для получения данных и настроек элемента из xml файла.
 * 
 * @author den
 * 
 */
public class HTMLFileGateway extends GeneralXMLHelper implements HTMLGateway {
	/**
	 * Каталог для данных в userdata.
	 */
	public static final String DATA_DIR = "data";

	private DataPanelElementInfo elementInfo;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
		Document doc = null;
		InputStream settings = null;
		String fileName =
			String.format("%s/%s/%s", DATA_DIR, elementInfo.getType().toString().toLowerCase(),
					elementInfo.getProcName());
		File file =
			new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + fileName);
		if (!file.exists()) {
			throw new SettingsFileOpenException(elementInfo.getProcName(), SettingsFileType.XM_DATA);
		}
		try {
			DocumentBuilder db = XMLUtils.createBuilder();
			InputStream stream = AppProps.loadUserDataToStream(fileName);
			doc = db.parse(stream);
		} catch (SAXException | IOException e) {
			throw new SettingsFileExchangeException(elementInfo.getProcName(), e,
					SettingsFileType.XM_DATA);
		}
		fileName =
			String.format("%s/%s/%s", DATA_DIR, elementInfo.getType().toString().toLowerCase(),
					getSettingsFileName());
		file = new File(AppInfoSingleton.getAppInfo().getCurUserData().getPath() + "/" + fileName);
		if (file.exists()) {
			try {
				settings = AppProps.loadUserDataToStream(fileName);
			} catch (IOException e) {
				throw new SettingsFileExchangeException(getSettingsFileName(), e,
						SettingsFileType.XM_DATA);
			}
		}
		return new HTMLBasedElementRawData(doc, settings, elementInfo, context);
	}

	private String getSettingsFileName() {
		return TextUtils.extractFileName(elementInfo.getProcName()) + ".settings.xml";
	}

}
