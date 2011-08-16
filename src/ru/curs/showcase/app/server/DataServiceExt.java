package ru.curs.showcase.app.server;

import java.io.ByteArrayOutputStream;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.XFormsContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.frame.MainPageFrameType;
import ru.curs.showcase.util.*;

/**
 * Расширенный интерфейс DataService, который содержит функции, не вошедшие в
 * GWT RPC интерфейс по причине невозможности их работы через GWT RPC.
 * 
 * @author den
 * 
 */
public interface DataServiceExt {
	/**
	 * Функция создания файла Excel по данным грида.
	 * 
	 * @param exportType
	 *            - тип экспорта.
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @param settings
	 *            - настройки грида.
	 * @param cs
	 *            - набор столбцов.
	 * @return - файл.
	 * @throws GeneralException
	 */
	ExcelFile generateExcelFromGrid(GridToExcelExportType exportType, GridContext context,
			DataPanelElementInfo element, ColumnSet cs) throws GeneralException;

	/**
	 * Выполняет xforms SQL submission - т.е. вызывает хранимую процедуру
	 * передавая ей данные из xforms и возвращает результат.
	 * 
	 * @param procName
	 *            - имя процедуры.
	 * @param content
	 *            - некие данные из формы.
	 * @return - результат выполнения submission.
	 * @throws GeneralException
	 * 
	 * @param userDataId
	 *            - идентификатор userdata.
	 */
	String handleSQLSubmission(String procName, String content, String userDataId)
			throws GeneralException;

	/**
	 * Выполняет xforms XSLT submission - т.е. вызывает XSL преобразование
	 * передавая ему данные из xforms и возвращает результат.
	 * 
	 * @param xsltFile
	 *            - имя файла с XSL преобразованием.
	 * @param content
	 *            - некие данные из формы.
	 * @return - результат выполнения submission.
	 * @param userDataId
	 *            - идентификатор userdata.
	 * @throws GeneralException
	 */
	String handleXSLTSubmission(String xsltFile, String content, String userDataId)
			throws GeneralException;

	/**
	 * Возвращает файл для скачивания.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - ссылка на файл.
	 * @return - файл.
	 * @throws GeneralException
	 */
	DataFile<ByteArrayOutputStream> getDownloadFile(XFormsContext context,
			DataPanelElementInfo elementInfo, String linkId) throws GeneralException;

	/**
	 * Загружает файл в хранилище.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - ссылка на файл.
	 * @param file
	 *            - файл.
	 * @throws GeneralException
	 */
	void uploadFile(XFormsContext context, DataPanelElementInfo elementInfo, String linkId,
			DataFile<ByteArrayOutputStream> file) throws GeneralException;

	/**
	 * Получает код фрейма главной страницы.
	 * 
	 * @param context
	 *            - контекст.
	 * @param type
	 *            - тип фрейма.
	 * @throws GeneralException
	 */
	String getMainPageFrame(final CompositeContext context, final MainPageFrameType type)
			throws GeneralException;
}
