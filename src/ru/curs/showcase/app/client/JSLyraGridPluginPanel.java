package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;

/**
 * Класс-адаптер панели с внешним плагином типа JSLyraGrid.
 */
public class JSLyraGridPluginPanel extends JSLiveGridPluginPanel {

	public JSLyraGridPluginPanel(final CompositeContext context, final DataPanelElementInfo element) {
		super(context, element);

	}

	public JSLyraGridPluginPanel(final DataPanelElementInfo element) {
		super(element);

	}

	@Override
	protected void resetGridSettingsToCurrent() {
		super.resetGridSettingsToCurrent();

		getLocalContext().setSubtype(DataPanelElementSubType.JS_LIVE_GRID);
	}

	@Override
	public GridContext getDetailedContext() {
		GridContext result = super.getDetailedContext();
		result.setSubtype(DataPanelElementSubType.JS_LIVE_GRID);
		return result;
	}
}