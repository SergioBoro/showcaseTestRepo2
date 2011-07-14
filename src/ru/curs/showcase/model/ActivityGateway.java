package ru.curs.showcase.model;

import ru.curs.showcase.app.api.event.Activity;

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
	 * @param activity
	 *            - действие.
	 */
	void exec(Activity activity);
}
