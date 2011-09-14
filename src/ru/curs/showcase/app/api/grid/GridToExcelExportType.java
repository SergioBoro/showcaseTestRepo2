package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Типы экспорта в Excel.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public enum GridToExcelExportType implements SerializableElement {
	/**
	 * Только текущую страницу.
	 */
	CURRENTPAGE,
	/**
	 * Весь грид.
	 */
	ALL
}
