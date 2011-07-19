package ru.curs.showcase.app.api.grid;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Типы экспорта в Excel.
 * 
 * @author den
 * 
 */
public enum GridToExcelExportType implements SerializableElement {
	/**
	 * Только текущую страницу.
	 */
	CURRENTPAGE,
	/**
	 * Весь грид.
	 */
	ALL
}
