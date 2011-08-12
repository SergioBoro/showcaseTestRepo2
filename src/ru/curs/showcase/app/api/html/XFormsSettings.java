package ru.curs.showcase.app.api.html;

import ru.curs.showcase.app.api.ElementSettings;

/**
 * Настройки XForms, сделанные пользователем.
 * 
 * @author den
 * 
 */
public class XFormsSettings extends ElementSettings {
	private static final long serialVersionUID = -6836184134400790951L;

	private String mainInstance;

	public XFormsSettings(final String aMainInstance) {
		super();
		mainInstance = aMainInstance;
	}

	public XFormsSettings() {
		super();
	}

	public String getMainInstance() {
		return mainInstance;
	}

	public void setMainInstance(final String aMainInstance) {
		mainInstance = aMainInstance;
	}
}
