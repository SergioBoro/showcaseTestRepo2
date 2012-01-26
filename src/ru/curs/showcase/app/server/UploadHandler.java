package ru.curs.showcase.app.server;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.xform.XFormUploadCommand;
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
	 * Описание отдельного файла при закачке - linkId + содержимое и имя файла.
	 * 
	 * @author den
	 * 
	 */
	private class UploadingFile {
		private final String linkId;
		private final OutputStreamDataFile file;

		public UploadingFile(final String aLinkId, final OutputStreamDataFile aFile) {
			super();
			linkId = aLinkId;
			file = aFile;
		}
	}

	/**
	 * Файлы, закаченные пользователем.
	 */
	private final List<UploadingFile> files = new ArrayList<>();

	@Override
	protected void processFiles() {
		for (UploadingFile item : files) {
			XFormUploadCommand command =
				new XFormUploadCommand(getContext(), getElementInfo(), item.linkId, item.file);
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
				String paramValue = out.toString();
				if (XFormContext.class.getName().equals(name)) {
					setContext((XFormContext) deserializeObject(paramValue));
				} else if (DataPanelElementInfo.class.getName().equals(name)) {
					setElementInfo((DataPanelElementInfo) deserializeObject(paramValue));
				}
			} else {
				String fileName = item.getName();
				fileName = TextUtils.extractFileNameWithExt(fileName);

				String linkId = name.replace(ExchangeConstants.FILE_DATA_PARAM_PREFIX, "");
				files.add(new UploadingFile(linkId, new OutputStreamDataFile(out, fileName)));
			}
		}
	}

	@Override
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	@Override
	protected void fillResponse() throws IOException {
		getResponse().setStatus(HttpServletResponse.SC_OK);
		getResponse().getWriter().close();
	}

}
