package ru.curs.showcase.app.api.event;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.GWTClonable;

/**
 * Ссылка на элемент информационной панели.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanelElementLink implements SerializableElement, GWTClonable, ContainingContext {

	private static final long serialVersionUID = 8381576475440574251L;

	/**
	 * Идентификатор элемента.
	 */
	private String id;

	/**
	 * Переопределенный контекст элемента.
	 */
	private CompositeContext context;

	/**
	 * Признак того, что нужно сохранять пользовательские настройки данного
	 * элемента после выполнения действия. Данная настройка перекрывает
	 * аналогичную настройку действия (Action).
	 */
	private Boolean keepUserSettings;

	public DataPanelElementLink() {
		super();
	}

	public DataPanelElementLink(final String aId, final CompositeContext aContext) {
		super();
		id = aId;
		context = aContext;
	}

	public final String getId() {
		return id;
	}

	public final void setId(final String aId) {
		id = aId;
	}

	@Override
	public final CompositeContext getContext() {
		return context;
	}

	public final void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	/**
	 * Определяет, нужно ли скрывать элемент.
	 * 
	 * @return результат проверки.
	 */
	public boolean doHiding() {
		return context.doHiding();
	}

	/**
	 * "Тупое" клонирование объекта, работающее в gwt. Заглушка до тех пор, пока
	 * в GWT не будет официальной реализации clone.
	 * 
	 * @return - копию объекта.
	 */
	@Override
	public DataPanelElementLink gwtClone() {
		DataPanelElementLink res = new DataPanelElementLink();
		res.id = id;
		res.context = context.gwtClone();
		res.keepUserSettings = keepUserSettings;
		return res;
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}
}
