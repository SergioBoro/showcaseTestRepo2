package ru.curs.showcase.model.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.command.DataPanelElementCommand;

/**
 * Базовый класс для всех команд XForms.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип результата.
 */
public abstract class XFormContextCommand<T> extends DataPanelElementCommand<T> {
	@Override
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	public XFormContextCommand(final String aSessionId, final XFormContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aSessionId, aContext, aElInfo);
	}
}
