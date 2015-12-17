package ru.curs.showcase.app.api.grid;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Типы виртуального столбца.
 * 
 */
public enum VirtualColumnType implements SerializableElement {

	/**
	 * Набор столбцов.
	 */
	COLUMN_SET,
	/**
	 * Сложный заголовок.
	 */
	COLUMN_HEADER,
	/**
	 * Обыкновенный столбец.
	 */
	COLUMN_REAL;
}
