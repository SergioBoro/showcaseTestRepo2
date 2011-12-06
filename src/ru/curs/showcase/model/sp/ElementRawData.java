package ru.curs.showcase.model.sp;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Базовый класс, содержащий необработанные XML данные и метаданные элемента
 * инф.панели.
 * 
 * @author den
 * 
 */
public class ElementRawData implements Closeable {
	/**
	 * Настройки элемента. Как правило, в формате XML.
	 */
	private InputStream settings;

	/**
	 * Описание элемента.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * Контекст, в котором был вызван элемент.
	 */
	private CompositeContext callContext;

	/**
	 * Вспомогательный модуль для получения необходимых данных из БД.
	 * Используется при необходимости считывания нескольких блоков данных в
	 * определенном порядке.
	 */
	private final ElementSPQuery spQuery;

	public ElementSPQuery getSpQuery() {
		return spQuery;
	}

	public ElementRawData(final InputStream props, final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = props;
		spQuery = null;
	}

	public ElementRawData(final ElementSPQuery aSPQuery, final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = null;
		spQuery = aSPQuery;
	}

	public ElementRawData(final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = null;
		spQuery = null;
	}

	public InputStream getSettings() {
		return settings;
	}

	public void setSettings(final InputStream aSettings) {
		settings = aSettings;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}

	public CompositeContext getCallContext() {
		return callContext;
	}

	public void setCallContext(final CompositeContext aCallContext) {
		callContext = aCallContext;
	}

	/**
	 * Подготавливает настройки элемента.
	 * 
	 */
	public void prepareSettings() {
		try {
			settings = spQuery.getValidatedSettings();
		} catch (SQLException e) {
			throw new ResultSetHandleException(e);
		}
	}

	/**
	 * Функция принудительно освобождает ресурсы, используемые шлюзом для
	 * получения данных. Должна быть вызвана после работы фабрики по построению
	 * навигатора.
	 * 
	 */
	@Override
	public void close() {
		spQuery.close();
	}
}
