package ru.curs.showcase.app.api.datapanel;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Тип хранимой процедуры для элемента информационной панели.
 * 
 * @author den
 * 
 */
public enum DataPanelElementProcType implements SerializableElement {
	/**
	 * Процедура для сохранения.
	 */
	SAVE,
	/**
	 * Процедура для загрузки данных посредством Submission.
	 */
	SUBMISSION,
	/**
	 * Процедура для загрузки файла с сервера клиенту.
	 */
	DOWNLOAD,
	/**
	 * Процедура для загрузки файла на сервер.
	 */
	UPLOAD,
	/**
	 * Имя процедуры, возвращающей метаданные без данных для элемента.
	 * Процедура, заданная в атрибуте элемента, в этом случае будет загружать
	 * только данные.
	 */
	METADATA
}
