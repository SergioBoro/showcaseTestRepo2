package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;

/**
 * 
 */

/**
 * @author anlug Класс реализующий функции обратного вызова из html текста
 *         (WebText).
 * 
 */
public final class WebTextPanelCallbacksEvents {

	private WebTextPanelCallbacksEvents() {

	}

	/**
	 * 
	 * Событие одинарного клика на графике (на Chart).
	 * 
	 * @param webTextId
	 *            - Id элемента WebText.
	 * @param linkId
	 *            - идентификатор ссылки на которой был совершен клик
	 */
	public static void webTextPanelClick(final String webTextId, final String linkId) {

		WebText wt = ((WebTextPanel) ActionExecuter.getElementPanelById(webTextId)).getWebText();

		List<HTMLEvent> events = wt.getEventManager().getEventForLink(linkId);
		Iterator<HTMLEvent> iterator = events.iterator();
		while (iterator.hasNext()) {
			HTMLEvent chev = iterator.next();
			AppCurrContext.getInstance().setCurrentAction(chev.getAction());
			ActionExecuter.execAction();
		}

	}
}
