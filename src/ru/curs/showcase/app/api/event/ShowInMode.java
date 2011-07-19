package ru.curs.showcase.app.api.event;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Способ отображения элементов для данного действия.
 * 
 * @author den
 * 
 */
public enum ShowInMode implements SerializableElement {
	/**
	 * Внутри вкладки информационной панели (по умолчанию).
	 */
	PANEL,
	/**
	 * В модальном окне.
	 */
	MODAL_WINDOW,
	/**
	 * На отдельной вкладке браузера.
	 */
	BROWSER_TAB
}
