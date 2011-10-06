package ru.curs.showcase.app.api.element;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Позиция вспомогательной панели относительно элемента информационной панели.
 * 
 * @author den
 * 
 */
public enum ChildPosition implements SerializableElement {
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
