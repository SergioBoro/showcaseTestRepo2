package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.GeneralException;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из XForms.
 * 
 * @author den
 * 
 */
public final class DownloadHandler extends AbstractDownloadHandler {
	/**
	 * Ссылка на файл.
	 */
	private String linkId;

	@Override
	protected void processFiles() throws GeneralException {
		ServiceLayerDataServiceImpl serviceLayer =
			new ServiceLayerDataServiceImpl(getRequest().getSession().getId());
		setOutputFile(serviceLayer.getDownloadFile(getContext(), getElementInfo(), linkId));
	}

	@Override
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return XFormContext.class;
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = decodeParamValue(getRequest().getParameter("linkId"));
	}

	public String getLinkId() {
		return linkId;
	}

}
