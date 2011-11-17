package ru.curs.showcase.app.api.event;

import javax.xml.bind.annotation.*;

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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity extends NamedElement implements SerializableElement, ContainingContext {

	private static final long serialVersionUID = -3677230519093999292L;

	private CompositeContext context;

	private Boolean onServerSide;

	public static Activity newServerActivity(final String aId, final String aName) {
		Activity res = new Activity(aId, aName, true);
		return res;
	}

	public static Activity newClientActivity(final String aId, final String aName) {
		Activity res = new Activity(aId, aName, false);
		return res;
	}

	protected Activity(final String aId, final String aName, final Boolean aOnServerSide) {
		super(aId, aName);
		onServerSide = aOnServerSide;
	}

	public Activity() {
		super();
	}

	@Override
	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext aContext) {
		context = aContext;
	}

	public Activity gwtClone() {
		Activity res = new Activity(getId(), getName(), onServerSide);
		res.setContext(context.gwtClone());
		return res;
	}

	public Boolean getOnServerSide() {
		return onServerSide;
	}

	public void setOnServerSide(final Boolean aOnServerSide) {
		onServerSide = aOnServerSide;
	}
}
