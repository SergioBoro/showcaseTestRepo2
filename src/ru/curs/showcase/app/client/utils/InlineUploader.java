package ru.curs.showcase.app.client.utils;

import java.util.Map.Entry;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;

/**
 * Класс для загрузки файлов на сервер прямо из XForm.
 * 
 * @author den
 * 
 */
public class InlineUploader {
	private static final String NAME_ATTR = "name";
	private static final String VALUE_ATTR = "value";

	private final String data;
	private final XFormPanel currentXFormPanel;
	private final Action ac;

	/**
	 * Обработчик окончания загрузки файлов.
	 */
	private CompleteHandler submitHandler = null;

	public InlineUploader(final String aData, final XFormPanel aCurrentXFormPanel, final Action aAc) {
		super();

		data = aData;
		currentXFormPanel = aCurrentXFormPanel;
		ac = aAc;
	}

	public void checkForUpload(final CompleteHandler uplSubmitEndHandler) {
		submitHandler = uplSubmitEndHandler;
		DataPanelElementInfo dpei = currentXFormPanel.getElementInfo();

		for (Entry<String, DataPanelElementProc> entry : dpei.getProcs().entrySet()) {
			if (entry.getValue().getType() == DataPanelElementProcType.UPLOAD) {
				JavaScriptObject form = getElementById(dpei.getUploaderId(entry.getKey()));
				if (form != null) {
					submitInlineForm(dpei, form);
				}
			}
		}

	}

	private void submitInlineForm(final DataPanelElementInfo dpei, final JavaScriptObject element) {
		boolean isFilesSelected = false;
		try {
			FormElement form = (FormElement) FormElement.as(element);
			SerializationStreamFactory ssf = WebUtils.createStdGWTSerializer();
			XFormContext context = new XFormContext(currentXFormPanel.getContext(), data);
			for (int i = 0; i < form.getElements().getLength(); i++) {
				Element el = form.getElements().getItem(i);
				if (XFormContext.class.getName().equals(el.getAttribute(NAME_ATTR))) {
					el.setAttribute(VALUE_ATTR, context.toParamForHttpPost(ssf));
					continue;
				}
				if (DataPanelElementInfo.class.getName().equals(el.getAttribute(NAME_ATTR))) {
					el.setAttribute(VALUE_ATTR, dpei.toParamForHttpPost(ssf));
					continue;
				}
				if ("file".equals(el.getAttribute("type"))) {
					isFilesSelected = isFilesSelected || isFilesSelected(el);
				}
			}
			if (isFilesSelected) {
				form.submit();
				clearForm(form);
			}
		} catch (Exception e) {
			MessageBox.showSimpleMessage(Constants.XFORMS_UPLOAD_ERROR, e.getMessage());
		}

	}

	private void clearForm(final FormElement form) {
		form.setInnerHTML(form.getInnerHTML());
	}

	private static native boolean isFilesSelected(final Element el)/*-{
		return el.value;
	}-*/;

	private static native JavaScriptObject getElementById(final String id) /*-{
		return $wnd.document.getElementById(id);
	}-*/;
}
