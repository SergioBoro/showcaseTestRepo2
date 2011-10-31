package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из грида.
 * 
 */
public class GridFileDownloadHandler extends AbstractDownloadHandler {

	/**
	 * Ссылка на файл.
	 */
	private String linkId;

	@Override
	protected void processFiles() {
		// TODO Auto-generated method stub

	}

	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected Class<? extends CompositeContext> getContextClass() {
		return GridContext.class;
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = getRequest().getParameter("linkId");
	}

	public String getLinkId() {
		return linkId;
	}

}
