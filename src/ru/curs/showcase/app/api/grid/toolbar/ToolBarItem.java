package ru.curs.showcase.app.api.grid.toolbar;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.event.Action;

/**
 * Элемент панели инструментов.
 * 
 * @author bogatov
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ToolBarItem extends BaseToolBarItem {
	private static final long serialVersionUID = 1L;
	private Action action;

	public ToolBarItem() {
		super();
	}

	public Action getAction() {
		return action;
	}

	public void setAction(final Action oAction) {
		this.action = oAction;
	}
}
