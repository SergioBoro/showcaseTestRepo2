package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

/**
 * Информация о фильтре.
 * 
 */
@XmlRootElement(name = "gridListOfValuesInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class GridListOfValuesInfo extends GridFilterInfo {
	private static final long serialVersionUID = -6770197715468479208L;

	private String currentColumn = null;

	public String getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(final String aCurrentColumn) {
		currentColumn = aCurrentColumn;
	}

}
