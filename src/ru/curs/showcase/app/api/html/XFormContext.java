package ru.curs.showcase.app.api.html;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Контекст XForms, включающий значения, введенные пользователем в mainInstance.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "xformsContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class XFormContext extends CompositeContext {
	private static final long serialVersionUID = -6836184134400790951L;

	private String formData;

	public XFormContext(final CompositeContext baseContext) {
		super();
		assignNullValues(baseContext);
	}

	public XFormContext(final CompositeContext baseContext, final String aFormData) {
		super();
		assignNullValues(baseContext);
		formData = aFormData;
	}

	public XFormContext() {
		super();
	}

	public XFormContext(final Map<String, List<String>> aParams, final String aFormData) {
		super(aParams);
		formData = aFormData;
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(final String aFormData) {
		formData = aFormData;
	}

	@Override
	public XFormContext gwtClone() {
		XFormContext result = (XFormContext) super.gwtClone();
		result.formData = formData;
		return result;
	}

	@Override
	protected XFormContext newInstance() {
		return new XFormContext();
	}

	@Override
	public String toString() {
		return "XFormsContext [formData=" + formData + ", toString()=" + super.toString() + "]";
	}

}
