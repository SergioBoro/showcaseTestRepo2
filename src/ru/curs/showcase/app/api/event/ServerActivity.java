package ru.curs.showcase.app.api.event;

import ru.curs.showcase.app.api.SerializableElement;

/**
 * Описание действия на сервере, не связанного с загрузкой элементов инф. панели
 * или вызовами навигатора. Данное действия является подчиненным по отношению к
 * {@link ru.curs.showcase.app.api.event.Action Action}.
 * 
 * @author den
 * 
 */
public class ServerActivity implements SerializableElement {

	@Override
	public String toString() {
		return "ServerActivity [name=" + name + ", type=" + type + ", context=" + context + "]";
	}

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3677230519093999292L;

	/**
	 * Название процедуры, скрипта или исполняемого файла, который требуется
	 * выполнить.
	 */
	private String name;

	/**
	 * Тип действия на сервере.
	 */
	private ServerActivityType type;

	/**
	 * Контекст, связанный с серверным действием.
	 */
	private CompositeContext context;

	public ServerActivity(final String aName, final ServerActivityType aType) {
		name = aName;
		type = aType;
	}

	public ServerActivity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(final String aName) {
		name = aName;
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
