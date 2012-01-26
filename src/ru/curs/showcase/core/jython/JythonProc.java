package ru.curs.showcase.core.jython;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.AbstractCompositeContext;

import com.ziclix.python.sql.PyConnection;

/**
 * Единый интерфейс для всех (!) Jython процедур. Каждая конкретная процедура на
 * Jython может реализовывать только те функции, которые нужно. Jython версии
 * (проверено на версии 2.5.2) это позволяет.
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
	String handle(String request);
}
