package ru.curs.showcase.app.api.datapanel;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.ExchangeConstants;
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

	/**
	 * Сохраняем ссылку на случай запроса доп. информации в процедуре обработки
	 * исключений.
	 */
	private DataPanelElementInfo elementInfo;

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
		String res = compositeContext.toString();
		if (elementId != null) {
			res =
				res + ExchangeConstants.LINE_SEPARATOR + "panel=" + panel
						+ ExchangeConstants.LINE_SEPARATOR + "elementId=" + elementId
						+ ExchangeConstants.LINE_SEPARATOR;
		}
		return res;
	}

	public DataPanelElementContext(final CompositeContext aContext, final DataPanelElementInfo dpei) {
		super();
		compositeContext = aContext;
		panel = dpei.getTab().getDataPanel().getId();
		elementId = dpei.getId();
		elementInfo = dpei;
	}

	public DataPanelElementContext(final CompositeContext aContext) {
		super();
		compositeContext = aContext;
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

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}
}
