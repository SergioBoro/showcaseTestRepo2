package ru.curs.showcase.app.server;

/**
 * Интерфейс для мониторинга состояния Showcase через JMX.
 * 
 * @author den
 * 
 */
public interface JMXMonitorBean {
	/**
	 * Установка уровня отладки.
	 * 
	 * @param level
	 *            - уровень.
	 */
	void setDebugLevel(String level);

	/**
	 * Получить число пользовательских сессий.
	 * 
	 * @return - число сессий.
	 */
	String getSessionCount();

	/**
	 * Очистка списка сессий.
	 */
	void clearSessions();
}
