package ru.curs.showcase.model;

import ru.curs.showcase.app.api.event.*;

/**
 * Шлюз-исполнитель для серверных действий.
 * 
 * @author den
 * 
 */
public interface ActivityGateway {
	/**
	 * Основной метод шлюза - выполнение действия.
	 * 
	 * @param context
	 *            - контекст.
	 * @param activity
	 *            - действие.
	 */
	void exec(CompositeContext context, ServerActivity activity);
}
