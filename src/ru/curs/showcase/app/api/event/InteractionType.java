package ru.curs.showcase.app.api.event;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Тип взаимодействия пользователя с элементом UI.
 * 
 * @author den
 * 
 */
public enum InteractionType implements SerializableElement {
	/**
	 * Ординарный клик.
	 */
	SINGLE_CLICK,
	/**
	 * Двойной клик.
	 */
	DOUBLE_CLICK,
	/**
	 * Нажатие правой кнопки мыши.
	 */
	RIGHT_CLICK,
	/**
	 * Нажатие средней кнопки мыши.
	 */
	MIDDLE_CLICK,
	/**
	 * Выделение элемента. Используется в частности для грида - запись
	 * выделяется с помощью специального столбца-селектора.
	 */
	SELECTION
}
