package ru.curs.showcase.app.api.html;

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
public class XFormsContext extends CompositeContext {
	private static final long serialVersionUID = -6836184134400790951L;

	private String formData;

	public XFormsContext(final CompositeContext baseContext) {
		super();
		assignNullValues(baseContext);
	}

	public XFormsContext(final CompositeContext baseContext, final String aFormData) {
		super();
		assignNullValues(baseContext);
		formData = aFormData;
	}

	public XFormsContext() {
		super();
	}

	public String getFormData() {
		return formData;
	}

	public void setFormData(final String aFormData) {
		formData = aFormData;
	}

	@Override
	public XFormsContext gwtClone() {
		XFormsContext result = (XFormsContext) super.gwtClone();
		result.formData = formData;
		return result;
	}

	@Override
	protected XFormsContext newInstance() {
		return new XFormsContext();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((formData == null) ? 0 : formData.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof XFormsContext)) {
			return false;
		}
		XFormsContext other = (XFormsContext) obj;
		if (formData == null) {
			if (other.formData != null) {
				return false;
			}
		} else if (!formData.equals(other.formData)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "XFormsContext [formData=" + formData + ", toString()=" + super.toString() + "]";
	}

}
