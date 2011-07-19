package ru.curs.showcase.app.api.event;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Тип действия, которое необходимо произвести с навигатором.
 * 
 * @author den
 * 
 */
public enum NavigatorActionType implements SerializableElement {
	/**
	 * Ничего не делать.
	 */
	DO_NOTHING,
	/**
	 * Сменить активный элемент.
	 */
	CHANGE_NODE,
	/**
	 * Сменить активный элемент и выполнить связанное с ним действие.
	 */
	CHANGE_NODE_AND_DO_ACTION
}
