package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.*;
import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для работы с файлами данных XForms. Используется в отладочных целях.
 * TODO пока сделан просто вывод данных о полученном файле или submission в
 * консоль и upload файла из classes по его имени.
 * 
 * @author den
 * 
 */
public final class XFormFileGateway extends DataCheckGateway implements XFormGateway {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(XFormFileGateway.class);
	/**
	 * Тестовый каталог для данных, сохраняемых через шлюз.
	 */
	public static final String TMP_TEST_DATA_DIR = "tmp/tmp.test.data";

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo element) {
		check(element);
		DocumentBuilder db = XMLUtils.createBuilder();
		Document doc;
		try {
			InputStream stream =
				AppProps.loadUserDataToStream(String.format("%s/%s.xml", AppProps.XFORMS_DIR,
						element.getProcName()));
			doc = db.parse(stream);
		} catch (Exception e) {
			throw new TestFileExchangeException(element.getProcName(), e);
		}
		return new HTMLBasedElementRawData(doc, null, element, context);
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo element,
			final String data) {
		check(element);
		String fileName =
			String.format(TMP_TEST_DATA_DIR + "/%s_updated.xml", element.getProcName());
		try {
			XMLUtils.stringToXMLFile(data, fileName);
		} catch (Exception e) {
			throw new TestFileExchangeException(fileName, e);
		}
	}

	@Override
	public String sqlTransform(final String aProcName, final XFormContext aInputData) {
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
			dup = new StreamConvertor(AppProps.loadResToStream(linkId));
		} catch (IOException e) {
			throw new TestFileExchangeException(linkId, e);
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
