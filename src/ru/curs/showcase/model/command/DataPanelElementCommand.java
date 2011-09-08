package ru.curs.showcase.model.command;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Базовый класс команды для работы с элементом инф. панели - его загрузкой или
 * обработкой.
 * 
 * @author den
 * 
 * @param <T>
 *            - класс результата.
 */
public abstract class DataPanelElementCommand<T> extends ServiceLayerCommand<T> {

	private final DataPanelElementInfo elementInfo;

	public DataPanelElementCommand(final String aSessionId, final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		super(aSessionId, aContext);
		elementInfo = aElementInfo;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}
}
