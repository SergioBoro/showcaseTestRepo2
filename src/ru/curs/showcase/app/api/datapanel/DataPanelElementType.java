package ru.curs.showcase.app.api.datapanel;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Возможные типы элементов информационной панели.
 * 
 * @author den
 * 
 */
public enum DataPanelElementType implements SerializableElement {
	/**
	 * Не элемент панели.
	 */
	NON_DP_ELEMENT,
	/**
	 * Грид.
	 */
	GRID,
	/**
	 * График.
	 */
	CHART,
	/**
	 * Текст с активными элементами.
	 */
	WEBTEXT,
	/**
	 * Карта.
	 */
	GEOMAP,
	/**
	 * XForm - форма для редактирования или фильтрации.
	 */
	XFORMS;

	/**
	 * Возвращает имя схемы для проверки общих настроек элемента.
	 */
	public String getSettingsSchemaName() {
		return name().toLowerCase() + "Settings.xsd";
	}

	/**
	 * Возвращает имя схемы для проверки настроек отдельных групп значения
	 * элемента (строк или столбцов таблицы).
	 */
	public String getPropsSchemaName() {
		return name().toLowerCase() + "Properties.xsd";
	}
}
