package ru.curs.showcase.core.jython;

import java.util.List;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.UserMessage;

import com.ziclix.python.sql.PyConnection;

/**
 * Единый интерфейс для всех (!) Jython процедур. Каждая конкретная процедура на
 * Jython может реализовывать только те функции, которые нужно. Jython версии
 * (проверено на версии 2.5.2) это позволяет. Единый интерфейс введен для
 * упрощения - чтобы в Jython файле нужно было импортировать только один модуль.
 * 
 * @author den
 * 
 */
public interface JythonProc {
	/**
	 * Выполнить серверное действие.
	 * 
	 * @param context
	 *            - контекст.
	 * @return - сообщение об ошибке в случае, если она произошла или None (null
	 *         в Java) в противном случае.
	 */
	UserMessage execute(AbstractCompositeContext context);

	/**
	 * Возвращает сырые данные для HTML элемента в XML формате.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @param elementId
	 *            - идентификатор создаваемого элемента.
	 * @return - объект переноса данных Jython, включающий в себя данные и
	 *         настройки элемента в виде двух строк или объект с информацией для
	 *         пользователя в случае ошибки.
	 */
	Object getRawData(AbstractCompositeContext context, String elementId);

	/**
	 * Возвращает сырые данные для компонента grid в случае задания одной
	 * процедурой.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @param elementId
	 *            - идентификатор создаваемого элемента.
	 * @param sortcols
	 *            - список столбцов для которых выполняется сортировка.
	 * @return - объект переноса данных Jython, включающий в себя данные и
	 *         настройки элемента в виде двух строк или объект с информацией для
	 *         пользователя в случае ошибки.
	 */
	Object getRawData(AbstractCompositeContext context, String elementId, List<Column> sortcols);

	/**
	 * Возвращает сырые данные для компонента grid в случае задания двумя
	 * процедурами.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @param elementId
	 *            - идентификатор создаваемого элемента.
	 * @param sortcols
	 *            - список столбцов для которых выполняется сортировка.
	 * @param firstrecord
	 *            - первая запись.
	 * @param pagesize
	 *            - количество возвращаемых записей.
	 * @return - объект переноса данных Jython, включающий в себя данные и
	 *         настройки элемента в виде двух строк или объект с информацией для
	 *         пользователя в случае ошибки.
	 */
	Object getRawData(AbstractCompositeContext context, String elementId, List<Column> sortcols,
			int firstrecord, int pagesize);

	/**
	 * Сохраняет данные (на данный момент, только для XForm).
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @param elementId
	 *            - идентификатор создаваемого элемента.
	 * @param data
	 *            - данные для сохранения.
	 * @return - сообщение об ошибке в случае, если она произошла или None (null
	 *         в Java) в противном случае.
	 */
	UserMessage save(AbstractCompositeContext context, String elementId, String data);

	/**
	 * Функция получения данных для элемента, требующего RecordSet. Все
	 * временные таблицы, которые использует результирующий запрос, должны
	 * создаваться в переданном соединении.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @param elementId
	 *            - идентификатор создаваемого элемента.
	 * @return - объект переноса данных Jython, включающий в себя данные и
	 *         настройки элемента в виде строки запроса для получения данных и
	 *         строки с метаданными. В случае ошибки может быть возвращен объект
	 *         с информацией для пользователя UserMessage.
	 * @param conn
	 *            - соединение с БД.
	 */
	Object getRawData(AbstractCompositeContext context, String elementId, PyConnection conn);

	/**
	 * Возвращает сырые данные для навигатора и инф. панели в XML формате.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @return - объект переноса данных Jython.
	 */
	Object getRawData(AbstractCompositeContext context);

	/**
	 * На основе запроса - строки в формате XML - выполняет какие-либо действия
	 * на сервере или преобразует входные данные. Возвращает XML данные.
	 * Передать ошибку можно двумя способами: исключением в Jython коде или
	 * специальными тэгами в тексте результата.
	 * 
	 * @param request
	 *            - XML текст запроса.
	 * @return - XML текст с результатом запроса.
	 */
	Object handle(String request);

	/**
	 * Процедура трансформации данных. Получает на вход строку и возвращает
	 * строку.
	 * 
	 * @param aContext
	 *            - контекст. Заполнено только поле session.
	 * @param aData
	 *            - данные, как правило, в формате XML.
	 * @return - преобразованные данные.
	 */
	Object transform(CompositeContext aContext, String aData);

	/**
	 * Процедура пост-обработки данных для элементов информационной панели.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param elementId
	 *            - идентификатор элемента.
	 * @param aData
	 *            - исходные данные.
	 * @return - массив строк в JythonDTO, являющихся параметрами элемента или
	 *         UserMessage в случае ошибки.
	 */
	Object postProcess(CompositeContext aContext, String elementId, String aData);

	/**
	 * Получить данные для селектора.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param aAttributes
	 *            - атрибуты запроса.
	 * @return - объект класса ResultSelectorData, содержащий как данные так и -
	 *         кол-во записей.
	 */
	Object getSelectorData(CompositeContext aContext, DataSelectorAttributes aAttributes);

	/**
	 * Получить InputStream для загрузки файла.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param attributes
	 *            - атрибуты запроса
	 * @return объект класса JythonDownloadResultForGrid
	 */
	<T extends InputAttributes> JythonDownloadResult getInputStream(
			AbstractCompositeContext aContext, T attributes);

	/**
	 * Загрузить файл на сервер.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param attributes
	 *            - атрибуты запроса
	 * @return в случае ошибки объект JythonErrorResult
	 */
	<T extends InputAttributes> JythonErrorResult doUpload(AbstractCompositeContext aContext,
			T attributes);

	/**
	 * Получить данные для gkfubyf.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param aAttributes
	 *            - атрибуты запроса.
	 * @return - объект класса
	 *         {@link ru.curs.showcase.core.plugin.ResultPluginData}, содержащий
	 *         данные.
	 */
	Object getPluginData(CompositeContext aContext, PluginAttributes aAttributes);
}
