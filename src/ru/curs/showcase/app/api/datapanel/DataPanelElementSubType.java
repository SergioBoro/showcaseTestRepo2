package ru.curs.showcase.app.api.datapanel;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Возможные подтипы элементов информационной панели (пока только для гридов).
 * 
 */
public enum DataPanelElementSubType implements SerializableElement {

	/**
	 * Существующий грид.
	 */
	PAGING_GRID,
	/**
	 * Грид из GXT. Live.
	 */
	EXT_LIVE_GRID,
	/**
	 * Грид из GXT. Page.
	 */
	EXT_PAGE_GRID,
	/**
	 * Грид из GXT. Tree.
	 */
	EXT_TREE_GRID,

	/**
	 * JS-Грид. Live.
	 */
	JS_LIVE_GRID,
	/**
	 * JS-Грид. Page.
	 */
	JS_PAGE_GRID,
	/**
	 * JS-Грид. Tree.
	 */
	JS_TREE_GRID;

	public boolean isJSGrid() {
		return (this == JS_LIVE_GRID) || (this == JS_PAGE_GRID) || (this == JS_TREE_GRID);
	}

}
