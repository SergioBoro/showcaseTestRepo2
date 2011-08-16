package ru.curs.showcase.model.xform;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormsContext;
import ru.curs.showcase.model.HTMLBasedElementRawData;
import ru.curs.showcase.util.DataFile;

/**
 * Шлюз для получения данных, необходимых для построения элемента панели типа
 * XForms.
 * 
 * @author den
 * 
 */
public interface XFormsGateway {

	/**
	 * Основная функция получения данных. Возвращает начальные данные для XForms
	 * и настройки элемента, в частности события.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - xml данные.
	 */
	HTMLBasedElementRawData getRawData(CompositeContext context, DataPanelElementInfo element);

	/**
	 * Сохраняет данные, введенные в карточке на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @param data
	 *            - данные для сохранения.
	 * 
	 */
	void saveData(CompositeContext context, DataPanelElementInfo element, String data);

	/**
	 * Возвращает файл для XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - идентификатор ссылки на файл
	 * @return - файл.
	 */
	DataFile<ByteArrayOutputStream> downloadFile(XFormsContext context,
			DataPanelElementInfo elementInfo, String linkId);

	/**
	 * Загружает (или модифицирует) дополнительные данные через Submission.
	 * 
	 * @param procName
	 *            - идентификатор процедуры.
	 * @param inputData
	 *            - дополнительные данные, требуемые для загрузки (как правило в
	 *            формате XML).
	 * @return - требуемые данные (как правило в формате XML).
	 * 
	 */
	String handleSubmission(String procName, String inputData);

	/**
	 * Загружает файл в хранилище.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - описание элемента.
	 * @param linkId
	 *            - ссылка на файл.
	 * @param aFile
	 *            - файл.
	 * **/
	void uploadFile(XFormsContext context, DataPanelElementInfo elementInfo, String linkId,
			DataFile<InputStream> aFile);
}
