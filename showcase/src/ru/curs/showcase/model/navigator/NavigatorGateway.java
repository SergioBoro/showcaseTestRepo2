package ru.curs.showcase.model.navigator;

import java.io.InputStream;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза данных для навигатора. На основе данного интерфейса может
 * быть сделаны реализация, возвращающая данные из БД или тестовая заглушка.
 * 
 * @author den
 * 
 */
public interface NavigatorGateway {

	/**
	 * Функция возврата данных для навигатора для конкретного пользователя.
	 * 
	 * @param user
	 *            - пользователь.
	 * @return - данные в формате xml.
	 * @param aContext
	 *            - контекст.
	 */
	InputStream getXMLForUser(final String user, CompositeContext aContext);

	/**
	 * Функция возврата данных для навигатора для пользователя по умолчанию.
	 * 
	 * @param aContext
	 *            - контекст.
	 * 
	 * @return - данные в формате xml.
	 * 
	 */
	InputStream getXMLByDefault(CompositeContext aContext);

	/**
	 * Функция принудительно освобождает ресурсы, используемые шлюзом для
	 * получения данных. Должна быть вызвана после работы фабрики по построению
	 * навигатора.
	 * 
	 */
	void releaseResources();
}