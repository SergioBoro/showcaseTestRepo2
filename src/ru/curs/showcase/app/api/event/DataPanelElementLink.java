package ru.curs.showcase.app.api.event;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.*;

/**
 * Ссылка на элемент информационной панели.
 * 
 * @author den
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataPanelElementLink implements SerializableElement, GWTClonable, ContainingContext,
		SizeEstimate {

	private static final long serialVersionUID = 8381576475440574251L;

	/**
	 * Идентификатор элемента.
	 */
	private ID id;

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

	/**
	 * Признак того, что нужно выполнять частичное обновление элемента. Данная
	 * настройка перекрывает аналогичную настройку действия (Action).
	 */
	private Boolean partialUpdate = null;

	private List<String> group = null;

	public DataPanelElementLink() {
		super();
	}

	public DataPanelElementLink(final String aId, final CompositeContext aContext) {
		super();
		id = new ID(aId);
		context = aContext;
	}

	public final ID getId() {
		return id;
	}

	public final void setId(final ID aId) {
		id = aId;
	}

	public final void setId(final String aId) {
		id = new ID(aId);
	}

	public final List<String> getGroup() {
		return group;
	}

	public final void setGroup(final String aGroup) {
		group = new ArrayList<String>();
		String str = aGroup.trim();
		String[] result = str.split("\\s+");
		for (int i = 0; i < result.length; i++) {
			group.add(result[i]);
		}
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
		res.partialUpdate = partialUpdate;
		return res;
	}

	public Boolean getKeepUserSettings() {
		return keepUserSettings;
	}

	public void setKeepUserSettings(final Boolean aKeepUserSettings) {
		keepUserSettings = aKeepUserSettings;
	}

	public Boolean getPartialUpdate() {
		return partialUpdate;
	}

	public void setPartialUpdate(final Boolean aPartialUpdate) {
		partialUpdate = aPartialUpdate;
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		result += id.length();
		result += context.sizeEstimate();
		return result;
	}
}
