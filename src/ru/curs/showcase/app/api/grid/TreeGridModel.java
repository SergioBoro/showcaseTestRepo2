package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

/**
 * Класс tree-грида из GXT с данными.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeGridModel extends LiveGridModel {

	private static final long serialVersionUID = 1409355931624821603L;

	private boolean hasChildren = false;

	private boolean expandedByGrid = false;

	public TreeGridModel() {
		super();
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(final boolean aHasChildren) {
		hasChildren = aHasChildren;
	}

	public boolean isExpandedByGrid() {
		return expandedByGrid;
	}

	public void setExpandedByGrid(final boolean aExpandedByGrid) {
		expandedByGrid = aExpandedByGrid;
	}

}
