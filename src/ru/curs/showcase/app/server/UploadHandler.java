package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormsContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.util.*;

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
	 * Файлы, закаченные пользователем. Ключом является ссылка на файл (linkId).
	 */
	private final Map<String, DataFile<ByteArrayOutputStream>> files =
		new TreeMap<String, DataFile<ByteArrayOutputStream>>();

	@Override
	protected void processFiles() throws GeneralException {
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(getRequest().getSession().getId());
		for (Map.Entry<String, DataFile<ByteArrayOutputStream>> entry : files.entrySet()) {
			serviceLayer.uploadFile(getContext(), getElementInfo(), entry.getKey(),
					entry.getValue());
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
			// несмотря на то, что нам нужен InputStream - его приходится
			// преобразовывать в OutputStream - т.к. чтение из InputStream
			// возможно только в данном цикле
			ByteArrayOutputStream out = StreamConvertor.inputToOutputStream(input);

			if (item.isFormField()) {
				String paramValue = decodeParamValue(out.toString());
				if (XFormsContext.class.getName().equals(name)) {
					setContext((XFormsContext) deserializeObject(paramValue));
				} else if (DataPanelElementInfo.class.getName().equals(name)) {
					setElementInfo((DataPanelElementInfo) deserializeObject(paramValue));
				}
			} else {
				String fileName = item.getName();
				fileName = ServletUtils.checkAndRecodeURLParam(fileName);
				fileName = TextUtils.extractFileNameWithExt(fileName);

				String linkId = name.replace(ExchangeConstants.FILE_DATA_PARAM_PREFIX, "");
				files.put(linkId, new DataFile<ByteArrayOutputStream>(out, fileName));
			}
		}
	}

	@Override
	public XFormsContext getContext() {
		return (XFormsContext) super.getContext();
	}

	public Map<String, DataFile<ByteArrayOutputStream>> getFiles() {
		return files;
	}

	@Override
	protected void fillResponse() throws IOException {
		getResponse().setStatus(HttpServletResponse.SC_OK);
		getResponse().getWriter().close();
	}

}
