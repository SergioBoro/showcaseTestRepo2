package ru.curs.showcase.app.client.utils;

import java.util.*;

import ru.curs.showcase.app.api.ExchangeConstants;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Вспомогательный класс, содержащий форму для загрузки файлов на сервер с
 * помощью компоненты FileUpload.
 * 
 * @author den
 * 
 */
public final class UploadHelper extends RunServletByFormHelper {

	static final String SC_UPLOADER_CSS = "sc-uploader-comp";

	public UploadHelper() {
		super();
	}

	/**
	 * Массив компонент для загрузки файлов на сервер.
	 */
	private final Map<String, FileUpload> uploaders = new TreeMap<String, FileUpload>();

	/**
	 * Очищает настройки. Необходимо вызывать в начале каждого использования.
	 */
	@Override
	public void clear() {
		super.clear();
		uploaders.clear();
	}

	@Override
	protected void initFormProps() {
		super.initFormProps();
		setEncoding(FormPanel.ENCODING_MULTIPART);
	}

	/**
	 * Процедура выбора файла для закачивания на сервер.
	 * 
	 * @param linkId
	 *            - ссылка на файл.
	 * @param closeHandler
	 *            - обработчик выбора файла.
	 */
	public void runUpload(final String linkId, final ChangeHandler closeHandler) {
		FileUpload uploader = uploaders.get(linkId);
		if (uploader == null) {
			uploader = new FileUpload();
			uploader.setStylePrimaryName(SC_UPLOADER_CSS);
			uploader.setName(ExchangeConstants.FILE_DATA_PARAM_PREFIX + linkId);
			getPanel().add(uploader);
			uploaders.put(linkId, uploader);
			uploader.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(final ChangeEvent aEvent) {
					closeHandler.onChange(aEvent);
				}
			});
		}
		uploader.setVisible(true);
	}

	public Map<String, FileUpload> getUploaders() {
		return uploaders;
	}

	/**
	 * Скрывает все компоненты FileUpload.
	 */
	public void hide() {
		Iterator<FileUpload> iterator = uploaders.values().iterator();
		while (iterator.hasNext()) {
			iterator.next().setVisible(false);
		}

	}

	/**
	 * Определяет - выбран ли хотя бы один файл в компоненте.
	 * 
	 * @return - результат проверки.
	 */
	public boolean isFilesSelected() {
		Iterator<FileUpload> iterator = uploaders.values().iterator();
		while (iterator.hasNext()) {
			FileUpload current = iterator.next();
			if ((current.getFilename() != null) && (current.getFilename() != "")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void submit() {
		if (isFilesSelected()) {
			super.submit();
		}
	}
}
