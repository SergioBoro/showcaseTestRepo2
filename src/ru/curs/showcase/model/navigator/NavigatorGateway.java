package ru.curs.showcase.model.navigator;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза данных для навигатора. На основе данного интерфейса может
 * быть сделаны реализация, возвращающая данные из БД или тестовая заглушка.
 * 
 * @author den
 * 
 */
public interface NavigatorGateway extends Closeable {

	/**
	 * Функция возврата данных для навигатора.
	 * 
	 * @return - данные в формате xml.
	 * @param aContext
	 *            - контекст.
	 * @param sourceName
	 *            - имя источника - файла или процедуры.
	 */
	InputStream getRawData(CompositeContext aContext);

	InputStream getRawData(CompositeContext aCompositeContext, String aSourceName);

	/**
	 * В переопределенной версии убрано контролируемое исключение.
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	void close();

	void setSourceName(String aSourceName);
}