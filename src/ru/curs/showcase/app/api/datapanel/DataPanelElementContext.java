package ru.curs.showcase.app.api.datapanel;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Информация об элементе и контексте его создания.
 * 
 * @author den
 * 
 */
public class DataPanelElementContext implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2803616788574282075L;

	/**
	 * Контекст.
	 */
	private CompositeContext compositeContext;

	/**
	 * Название панели.
	 */
	private String panel;

	/**
	 * Идентификатор элемента.
	 */
	private String elementId;

	public CompositeContext getCompositeContext() {
		return compositeContext;
	}

	public DataPanelElementContext() {
		super();
	}

	public void setCompositeContext(final CompositeContext aContext) {
		compositeContext = aContext;
	}

	@Override
	public String toString() {
		return compositeContext + ExchangeConstants.LINE_SEPARATOR + "panel=" + panel
				+ ExchangeConstants.LINE_SEPARATOR + "elementId=" + elementId
				+ ExchangeConstants.LINE_SEPARATOR;
	}

	public DataPanelElementContext(final CompositeContext aContext, final DataPanelElementInfo dpei) {
		super();
		compositeContext = aContext;
		panel = dpei.getTab().getDataPanel().getId();
		elementId = dpei.getId();
	}

	public String getPanel() {
		return panel;
	}

	public void setPanel(final String aPanel) {
		panel = aPanel;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(final String aElementId) {
		elementId = aElementId;
	}
}
