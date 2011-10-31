package ru.curs.showcase.app.server;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.model.command.GridFileDownloadCommand;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс-обработчик на получение файла из БД с помощью хранимой процедуры.
 * Используется для скачивания файлов из грида.
 * 
 */
public class GridFileDownloadHandler extends AbstractDownloadHandler {

	/**
	 * Ссылка на хранимую процедуру получения файла.
	 */
	private String linkId;

	/**
	 * Идентификатор записи грида для скачивания файла.
	 */
	private String recordId;

	@Override
	protected void processFiles() {
		GridFileDownloadCommand command =
			new GridFileDownloadCommand(getContext(), getElementInfo(), linkId, recordId);
		setOutputFile(command.execute());
	}

	@Override
	protected void getParams() throws SerializationException, FileUploadException, IOException {
		super.getParams();
		linkId = getRequest().getParameter("linkId");
		recordId = getRequest().getParameter("recordId");
	}

	public String getLinkId() {
		return linkId;
	}

	public String getRecordId() {
		return recordId;
	}

}
