package ru.curs.showcase.app.api.grid;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Способ отображения значений в столбце.
 */
public enum ColumnValueDisplayMode implements SerializableElement {
	/**
	 * В одну строку.
	 */
	SINGLELINE,
	/**
	 * В несколько строк.
	 */
	MULTILINE,
	/**
	 * Автоподбор ширины по максимальной длине содержимого.
	 */
	AUTOFIT
}
