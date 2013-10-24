package ru.curs.showcase.app.client.utils;

import java.util.List;

import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;

import com.google.gwt.core.client.GWT;

/**
 * Общие утилиты для XForms. В том числе, генерация главной XForm'ы.
 */
public final class XFormsUtils {

	private static DataServiceAsync dataService = null;

	private XFormsUtils() {
		throw new UnsupportedOperationException();
	}

	public static void initXForms() {

		setCallbackJSNIFunction();

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getMainXForms(new GWTServiceCallback<List<String>>(AppCurrContext
				.getInstance().getInternationalizedMessages().xformsErrorGetMainData()) {
			@Override
			public void onSuccess(final List<String> mainXForm) {
				// destroy();

				if (mainXForm.size() > 0) {
					addMainXFormBody(mainXForm.get(0));
				}

				for (int i = 2; i < mainXForm.size(); i++) {
					addMainXFormScript(mainXForm.get(i));
				}

				initMainXForm();
			}
		});
	}

	private static native void initMainXForm() /*-{
		$wnd.xsltforms_init();
	}-*/;

	private static native void destroy() /*-{
		//Подчищаем динамические скрипты
		var div = $doc.getElementById('target');
		while (div.childNodes.length > 0)
			div.removeChild(div.firstChild);

	}-*/;

	/**
	 * Установка процедур обратного вызова.
	 */
	// CHECKSTYLE:OFF
	private static native void setCallbackJSNIFunction() /*-{
															$wnd.gwtXFormSave = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormFilter = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickFilter(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormUpdate = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::xFormPanelClickUpdate(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.showSelector =	@ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showSelector(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.showMultiSelector = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::showMultiSelector(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtXFormDownload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::downloadFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormUpload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::uploadFile(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtXFormSimpleUpload = @ru.curs.showcase.app.client.api.XFormPanelCallbacksEvents::simpleUpload(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
															$wnd.gwtXFormOnSubmitComplete = @ru.curs.showcase.app.client.utils.InlineUploader::onSubmitComplete(Ljava/lang/String;);
															$wnd.gwtXFormOnChooseFiles = @ru.curs.showcase.app.client.utils.InlineUploader::onChooseFiles(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;Ljava/lang/String;);
															$wnd.gwtCreatePlugin =	@ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::createPlugin(Lcom/google/gwt/core/client/JavaScriptObject;);
															$wnd.gwtGetDataPlugin = @ru.curs.showcase.app.client.api.PluginPanelCallbacksEvents::pluginGetData(Lcom/google/gwt/core/client/JavaScriptObject;);
															}-*/;

	// CHECKSTYLE:ON

	/**
	 * Динамически вставляет в страницу скрипт, используемый главной XForm'ой.
	 * 
	 * @param code
	 *            Javascript-код, который необходимо вставить
	 */
	private static native void addMainXFormScript(final String code) /*-{
		var newscript = $doc.createElement('script');
		newscript.text = code;
		newscript.type = "text/javascript";
		var div = $doc.getElementById('target');
		div.appendChild(newscript);
	}-*/;

	/**
	 * Динамически вставляет в страницу содержимое главной XForm'ы.
	 * 
	 * @param mainXForm
	 *            содержимое главной XForm'ы
	 */
	private static native void addMainXFormBody(final String mainXForm) /*-{
		var div = $doc.getElementById('mainXForm');
		div.innerHTML = mainXForm;
	}-*/;

}