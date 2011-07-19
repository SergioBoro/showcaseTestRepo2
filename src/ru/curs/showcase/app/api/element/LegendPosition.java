package ru.curs.showcase.app.api.element;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Позиция легенды графика относительно объекта информационной панели. Может
 * задаваться для графика и карты.
 * 
 * @author den
 * 
 */
public enum LegendPosition implements SerializableElement {
	/**
	 * Слева.
	 */
	LEFT,
	/**
	 * Справа.
	 */
	RIGHT,
	/**
	 * Сверху.
	 */
	TOP,
	/**
	 * Снизу.
	 */
	BOTTOM
}
