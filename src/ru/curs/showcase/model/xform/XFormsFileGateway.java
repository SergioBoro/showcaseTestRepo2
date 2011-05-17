package ru.curs.showcase.model.xform;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.TestFileExchangeException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

/**
 * Шлюз для работы с файлами данных XForms. Используется в отладочных целях.
 * 
 * @author den
 * 
 */
public final class XFormsFileGateway extends DataCheckGateway implements XFormsGateway {

	@Override
	public HTMLBasedElementRawData getInitialData(final CompositeContext context,
			final DataPanelElementInfo element) {
		check(element);
		DocumentBuilder db = XMLUtils.createBuilder();
		InputStream stream = null;
		Document doc = null;
		try {
			stream =
				AppProps.loadUserDataToStream(String.format("%s/%s.xml", AppProps.XFORMS_DIR,
						element.getProcName()));
			doc = db.parse(stream);
		} catch (Throwable e) {
			throw new TestFileExchangeException(element.getProcName(), e);
		}
		return new HTMLBasedElementRawData(doc, null, element, context);
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public CommandResult saveData(final CompositeContext context,
			final DataPanelElementInfo element, final String data) {
		check(element);
		XMLUtils.saveStringToXML(data, String.format("%s/%s/%s_updated.xml",
				AppProps.getUserDataCatalog(), AppProps.XFORMS_DIR, element.getProcName()));
		return CommandResult.newSuccessResult();
	}

	@Override
	public RequestResult handleSubmission(final String aProcName, final String aInputData) {
		// TODO сделать пример для файлов
		return null;
	}

	@Override
	public DataFile<ByteArrayOutputStream> downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data) {
		InputStreamDuplicator dup;
		try {
			dup = new InputStreamDuplicator(AppProps.loadResToStream(linkId));
		} catch (IOException e) {
			throw new TestFileExchangeException(linkId, e);
		}
		DataFile<ByteArrayOutputStream> file =
			new DataFile<ByteArrayOutputStream>(dup.getOutputStream(), linkId);
		return file;
	}

	@Override
	public void uploadFile(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo, final String aLinkId, final String aData,
			final DataFile<ByteArrayOutputStream> aFile) {
		// TODO в целях отладки - просто вывод данных о полученном файле
		System.out
				.println(String
						.format("Сохранен файл %s с контекстом %s из элемента %s, идентификатор ссылки %s, данные формы %s",
								aFile.getName(), aContext, aElementInfo, aLinkId, aData));

	}
}
