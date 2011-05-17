package ru.curs.showcase.model.xform;

import javax.xml.bind.annotation.XmlRootElement;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Наследник класса CompositeContext, готовый к сериализации. Введен из-за того,
 * что CompositeContext - это GWT DTO и поэтому не может содержать аннотации
 * JAXB.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "context")
public final class CompositeContextJAXBAdapter extends CompositeContext {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1264845940360097046L;

	public CompositeContextJAXBAdapter(final CompositeContext original) {
		super();
		assignNullValues(original);
	}

	public CompositeContextJAXBAdapter() {
		super();
	}
}
