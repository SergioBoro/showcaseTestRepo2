package ru.curs.showcase.model.jython;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.AbstractCompositeContext;

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
	 *         настройки элемента в виде двух строк, а также объект с
	 *         информацией для пользователя в случае ошибки. Если ошибки не было
	 *         - userMessage должен быть None (null в Java).
	 */
	JythonDTO getRawData(AbstractCompositeContext context, String elementId);

	/**
	 * Возвращает сырые данные для навигатора и инф. панели в XML формате.
	 * 
	 * @param context
	 *            - контекст вызова.
	 * @return - объект переноса данных Jython.
	 */
	JythonDTO getRawData(AbstractCompositeContext context);

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
