package ru.curs.showcase.core.html.xform;

import java.io.*;

import javax.xml.transform.TransformerException;

import org.slf4j.*;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.HTMLFileGateway;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для работы с файлами данных XForms. Используется в отладочных целях.
 * TODO пока сделан просто вывод данных о полученном файле или submission в
 * консоль и upload файла из classes по его имени.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для XForm из файлов")
public final class XFormFileGateway extends HTMLFileGateway implements XFormGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(XFormFileGateway.class);
	/**
	 * Тестовый каталог для данных, сохраняемых через шлюз.
	 */
	public static final String TMP_TEST_DATA_DIR = "tmp/tmp.test.data";

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo element,
			final String data) {
		String fileName =
			String.format(TMP_TEST_DATA_DIR + "/%s_updated.xml", element.getProcName());
		try {
			XMLUtils.stringToXMLFile(data, fileName);
		} catch (SAXException | IOException | TransformerException e) {
			throw new SettingsFileExchangeException(fileName, e, SettingsFileType.XML);
		}
	}

	@Override
	public String scriptTransform(final String aProcName, final XFormContext aInputData) {
		LOGGER.info(String.format(
				"Заглушка: выполнение Submission процедуры '%s' c данными формы  %s", aProcName,
				aInputData));
		return null;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final String linkId) {
		StreamConvertor dup;
		try {
			dup = new StreamConvertor(FileUtils.loadResToStream(linkId));
		} catch (IOException e) {
			throw new SettingsFileExchangeException(linkId, e, SettingsFileType.XML);
		}
		OutputStreamDataFile file = new OutputStreamDataFile(dup.getOutputStream(), linkId);
		file.setEncoding(TextUtils.JDBC_ENCODING);
		return file;
	}

	@Override
	public void uploadFile(final XFormContext aContext, final DataPanelElementInfo aElementInfo,
			final String aLinkId, final DataFile<InputStream> aFile) {
		LOGGER.info(String
				.format("Заглушка: сохранение файла '%s' с контекстом %s из элемента %s, ссылка %s, данные формы %s",
						aFile.getName(), aContext, aElementInfo, aLinkId, aContext.getFormData()));

	}
}