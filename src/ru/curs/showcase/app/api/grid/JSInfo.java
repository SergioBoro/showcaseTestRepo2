package ru.curs.showcase.app.api.grid;

import java.util.*;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Информация о "плагиновской" составляющей грида.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JSInfo implements SerializableElement {
	private static final long serialVersionUID = -848555821344283638L;

	private String createProc;
	private String refreshProc;
	private String clipboardProc;

	private List<String> requiredJS = new ArrayList<String>();

	public String getCreateProc() {
		return createProc;
	}

	public void setCreateProc(final String aCreateProc) {
		createProc = aCreateProc;
	}

	public String getRefreshProc() {
		return refreshProc;
	}

	public void setRefreshProc(final String aRefreshProc) {
		refreshProc = aRefreshProc;
	}

	public String getClipboardProc() {
		return clipboardProc;
	}

	public void setClipboardProc(final String aClipboardProc) {
		clipboardProc = aClipboardProc;
	}

	public List<String> getRequiredJS() {
		return requiredJS;
	}

	public void setRequiredJS(final List<String> aRequiredJS) {
		requiredJS = aRequiredJS;
	}

}
