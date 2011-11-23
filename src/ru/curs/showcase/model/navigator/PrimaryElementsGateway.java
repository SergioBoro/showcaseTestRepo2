package ru.curs.showcase.model.navigator;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Интерфейс шлюза данных для навигатора и инф. панели.
 * 
 * @author den
 * 
 */
public interface PrimaryElementsGateway extends Closeable {

	/**
	 * Базовая функция возврата данных.
	 * 
	 * @return - данные в формате xml.
	 * @param aContext
	 *            - контекст.
	 * @param sourceName
	 *            - имя источника - файла или процедуры.
	 */
	InputStream getRawData(CompositeContext aContext);

	/**
	 * Расширенная функция возврата данных.
	 */
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