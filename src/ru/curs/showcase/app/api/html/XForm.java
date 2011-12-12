package ru.curs.showcase.app.api.html;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
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
public class XForm extends DataPanelElement {

	private static final long serialVersionUID = 2992185048516571628L;

	/**
	 * Смысловые элементы xforms - html, js, css. В будущем будет разбито на
	 * отдельные поля.
	 */
	private List<String> xFormParts;

	public void setXFormParts(final List<String> xFormParts1) {
		this.xFormParts = xFormParts1;
	}

	public final List<String> getXFormParts() {
		return xFormParts;
	}

	@Override
	protected HTMLEventManager initEventManager() {
		return new HTMLEventManager();
	}

	@Override
	public HTMLEventManager getEventManager() {
		return (HTMLEventManager) super.getEventManager();
	}

	public XForm() {
		super();
	}

	public XForm(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

}
