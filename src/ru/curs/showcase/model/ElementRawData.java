/**
 * 
 */
package ru.curs.showcase.model;

import java.io.InputStream;
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
public class ElementRawData {
	/**
	 * Список событий и действие по умолчанию для WebText в формате XML.
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
	 */
	private final ElementSPCallHelper spCallHelper;

	public ElementSPCallHelper getSpCallHelper() {
		return spCallHelper;
	}

	public ElementRawData(final InputStream props, final DataPanelElementInfo aElementInfo,
			final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = props;
		spCallHelper = null;
	}

	public ElementRawData(final ElementSPCallHelper aSPCallHelper,
			final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = null;
		spCallHelper = aSPCallHelper;
	}

	public ElementRawData(final DataPanelElementInfo aElementInfo, final CompositeContext aContext) {
		elementInfo = aElementInfo;
		callContext = aContext;
		settings = null;
		spCallHelper = null;
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
			settings = spCallHelper.getValidatedSettings();
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
	public void releaseResources() {
		spCallHelper.releaseResources();
	}
}
