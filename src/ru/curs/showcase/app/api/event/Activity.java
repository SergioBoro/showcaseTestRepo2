package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.*;

/**
 * Описание действия на сервере или клиенте, не связанного напрямую с элементами
 * инф. панели или вызовами навигатора. Данное действия является частью
 * {@link ru.curs.showcase.app.api.event.Action Action}. Имя действия - это имя
 * процедуры, скрипта или исполняемого файла.
 * 
 * @author den
 * 
 */
public class Activity extends NamedElement implements SerializableElement {

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
	private ServerActivityType type;

	/**
	 * Контекст, связанный с серверным действием.
	 */
	private CompositeContext context;

	public Activity(final String aName, final ServerActivityType aType) {
		setName(aName);
		type = aType;
	}

	public Activity() {
		super();
	}

	public ServerActivityType getType() {
		return type;
	}

	public void setType(final ServerActivityType aType) {
		type = aType;
	}

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}
}
