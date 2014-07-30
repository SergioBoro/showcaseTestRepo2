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
	private String addRecordProc;
	private String saveProc;
	private String revertProc;
	private String clipboardProc;

	private List<String> requiredJS = new ArrayList<String>();

	private FilterMultiselector filterMultiselector = null;

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

	public String getAddRecordProc() {
		return addRecordProc;
	}

	public void setAddRecordProc(final String aAddRecordProc) {
		addRecordProc = aAddRecordProc;
	}

	public String getSaveProc() {
		return saveProc;
	}

	public void setSaveProc(final String aSaveProc) {
		saveProc = aSaveProc;
	}

	public String getRevertProc() {
		return revertProc;
	}

	public void setRevertProc(final String aRevertProc) {
		revertProc = aRevertProc;
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

	public FilterMultiselector getFilterMultiselector() {
		return filterMultiselector;
	}

	public void setFilterMultiselector(final FilterMultiselector aFilterMultiselector) {
		filterMultiselector = aFilterMultiselector;
	}

}
