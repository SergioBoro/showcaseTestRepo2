package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.xform.XFormUploadCommand;
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
	private final Map<String, OutputStreamDataFile> files =
		new TreeMap<String, OutputStreamDataFile>();

	@Override
	protected void processFiles() throws GeneralException {
		for (Map.Entry<String, OutputStreamDataFile> entry : files.entrySet()) {
			XFormUploadCommand command =
				new XFormUploadCommand(getContext(), getElementInfo(), entry.getKey(),
						entry.getValue());
			command.execute();
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
				if (XFormContext.class.getName().equals(name)) {
					setContext((XFormContext) deserializeObject(paramValue));
				} else if (DataPanelElementInfo.class.getName().equals(name)) {
					setElementInfo((DataPanelElementInfo) deserializeObject(paramValue));
				}
			} else {
				String fileName = item.getName();
				fileName = ServletUtils.checkAndRecodeURLParam(fileName);
				fileName = TextUtils.extractFileNameWithExt(fileName);

				String linkId = name.replace(ExchangeConstants.FILE_DATA_PARAM_PREFIX, "");
				files.put(linkId, new OutputStreamDataFile(out, fileName));
			}
		}
	}

	@Override
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	public Map<String, OutputStreamDataFile> getFiles() {
		return files;
	}

	@Override
	protected void fillResponse() throws IOException {
		getResponse().setStatus(HttpServletResponse.SC_OK);
		getResponse().getWriter().close();
	}

}
