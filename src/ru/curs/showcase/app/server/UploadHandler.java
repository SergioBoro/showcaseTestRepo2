package ru.curs.showcase.app.server;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.*;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralServerException;
import ru.curs.showcase.model.DataFile;
import ru.curs.showcase.util.StreamConvertor;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из XForms.
 * 
 * @author den
 * 
 */
public final class UploadHandler extends AbstractFilesHandler {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);

	/**
	 * Данные, введенные пользователем.
	 */
	private String data;

	/**
	 * Файлы, закаченные пользователем. Ключом является ссылка на файл (linkId).
	 */
	private final Map<String, DataFile<ByteArrayOutputStream>> files =
		new TreeMap<String, DataFile<ByteArrayOutputStream>>();

	@Override
	protected void processFiles() throws GeneralServerException {
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl();
		Iterator<String> iterator = files.keySet().iterator();
		while (iterator.hasNext()) {
			String linkId = iterator.next();
			serviceLayer.uploadFile(getContext(), getElementInfo(), linkId, data,
					files.get(linkId));
		}
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iterator = upload.getItemIterator(getRequest());

		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();
			String name = item.getFieldName();
			InputStream input = item.openStream();
			ByteArrayOutputStream out = StreamConvertor.inputToOutputStream(input);

			if (item.isFormField()) {
				String paramValue = URLDecoder.decode(out.toString(), "UTF-8");
				if (name.equals("data")) {
					data = out.toString();
					LOGGER.debug("Данные формы при загрузке файла:" + data);
				} else if (CompositeContext.class.getName().equals(name)) {
					setContext(((CompositeContext) deserializeObject(paramValue)));
				} else if (DataPanelElementInfo.class.getName().equals(name)) {
					setElementInfo(((DataPanelElementInfo) deserializeObject(paramValue)));
				}
			} else {
				String fileName = item.getName();
				if (fileName != null) {
					File file = new File(fileName);
					fileName = file.getName();
				}
				String linkId = name.replace(ExchangeConstants.FILE_DATA_PARAM_PREFIX, "");
				files.put(linkId, new DataFile<ByteArrayOutputStream>(out, fileName));
				LOGGER.debug("Получен файл '" + fileName + "' размером " + out.size() + " байт");
			}
		}
	}

	public Map<String, DataFile<ByteArrayOutputStream>> getFiles() {
		return files;
	}

	/**
	 * Возвращает новый экземпляр обработчика.
	 * 
	 * @return - новый экземпляр.
	 */
	public static AbstractFilesHandler newInstance() {
		return new UploadHandler();
	}

	public String getData() {
		return data;
	}

	@Override
	protected void fillResponse() throws IOException {
		getResponse().setStatus(HttpServletResponse.SC_OK);
		getResponse().getWriter().close();
	}

}
