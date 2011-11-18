package ru.curs.showcase.model.html.xform;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.command.*;

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
	@InputParam
	public XFormContext getContext() {
		return (XFormContext) super.getContext();
	}

	public XFormContextCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}
}
