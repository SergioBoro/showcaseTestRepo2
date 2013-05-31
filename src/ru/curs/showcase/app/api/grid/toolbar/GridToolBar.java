package ru.curs.showcase.app.api.grid.toolbar;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Панель инструментов грида.
 * 
 * @author bogatov
 * 
 */
@XmlRootElement
@XmlSeeAlso({ ToolBarItem.class, ToolBarGroup.class })
@XmlAccessorType(XmlAccessType.FIELD)
public class GridToolBar implements SerializableElement {
	private static final long serialVersionUID = 1L;
	private List<AbstractToolBarItem> items = new ArrayList<AbstractToolBarItem>();

	public GridToolBar() {
		super();
	}

	public void add(final AbstractToolBarItem item) {
		items.add(item);
	}

	/**
	 * Возвращает элемены панели инструментов.
	 * 
	 * @return элементы типа {@link ToolBarItem} или {@link ToolBarGroup} или
	 *         {@link ToolBarSeparator}
	 */
	public Collection<AbstractToolBarItem> getItems() {
		return items;
	}
}
