package ru.curs.showcase.app.api.event;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.NamedElement;

/**
 * Описание действия на сервере или клиенте, не связанного напрямую с элементами
 * инф. панели или вызовами навигатора. Данное действия является частью
 * {@link ru.curs.showcase.app.api.event.Action Action}. Имя действия - это имя
 * процедуры, скрипта или исполняемого файла.
 * 
 * @author den
 * 
 */
public class Activity extends NamedElement implements SerializableElement, ContainingContext {

	@Override
	public String toString() {
		return "Activity [type=" + type + ", context=" + context + ", getId()=" + getId()
				+ ", getName()=" + getName() + "]";
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3677230519093999292L;

	/**
	 * Тип действия на сервере.
	 */
	private ActivityType type;

	/**
	 * Контекст, связанный с серверным действием.
	 */
	private CompositeContext context;

	public Activity(final String aId, final String aName, final ActivityType aType) {
		super(aId, aName);
		type = aType;
	}

	public Activity() {
		super();
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(final ActivityType aType) {
		type = aType;
	}

	@Override
	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}
}
