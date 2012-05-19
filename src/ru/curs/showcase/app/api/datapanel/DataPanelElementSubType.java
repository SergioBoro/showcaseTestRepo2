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
	 * Грид из GXT. Paging.
	 */
	EXT_PAGING_GRID,
	/**
	 * Грид из GXT. Tree.
	 */
	EXT_TREE_GRID;

}
