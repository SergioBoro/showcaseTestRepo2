package ru.curs.showcase.app.api.html;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.element.DataPanelElement;

/**
 * Класс, содержащий данные для отрисовки элемента WebText и необходимые
 * обработчики событий.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WebText extends DataPanelElement {

	private static final long serialVersionUID = 2992185048516571628L;

	public WebText() {
		super();
	}

	public WebText(final String aData) {
		super();
		this.data = aData;
	}

	/**
	 * HTML данные для вставки на страницу.
	 */
	private String data;

	public final String getData() {
		return data;
	}

	public final void setData(final String aData) {
		this.data = aData;
	}

	@Override
	protected HTMLEventManager initEventManager() {
		return new HTMLEventManager();
	}

	@Override
	public HTMLEventManager getEventManager() {
		return (HTMLEventManager) super.getEventManager();
	}
}
