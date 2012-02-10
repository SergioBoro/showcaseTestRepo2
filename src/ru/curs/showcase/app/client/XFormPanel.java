package ru.curs.showcase.app.client;

import java.util.List;

import ru.beta2.extra.gwt.ui.selector.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с XForm.
 */
public class XFormPanel extends BasicElementPanelBasis {

	private static final String PROC100 = "100%";
	private static final String SHOWCASE_APP_CONTAINER = "showcaseAppContainer";

	private final VerticalPanel p = new VerticalPanel();
	private HTML xf = null;

	private XForm xform = null;
	private String mainInstance;

	/**
	 * Окно для загрузки файлов на сервер.
	 */
	private UploadWindow uw = null;

	private DataServiceAsync dataService = null;

	private final SelectorDataServiceAsync selSrv = GWT.create(SelectorDataService.class);
	{
		((ServiceDefTarget) selSrv).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "SelectorDataService" + Window.Location.getQueryString());
	}

	public SelectorDataServiceAsync getSelSrv() {
		return selSrv;
	}

	/**
	 * Ф-ция, возвращающая панель с XForm.
	 * 
	 * @return - Панель с XForm.
	 */
	@Override
	public VerticalPanel getPanel() {
		return p;
	}

	@Override
	public DataPanelElement getElement() {
		return xform;
	}

	public DataServiceAsync getDataService() {
		return dataService;
	}

	public UploadWindow getUw() {
		return uw;
	}

	public void setUw(final UploadWindow auw) {
		this.uw = auw;
	}

	@Override
	public void hidePanel() {
		p.setVisible(false);
	}

	@Override
	public void showPanel() {
		p.setVisible(true);
	}

	/**
	 * Конструктор класса XFormPanel без начального показа XForm.
	 */
	public XFormPanel(final DataPanelElementInfo element) {

		setElementInfo(element);
		setContext(null);

		setCallbackJSNIFunction();

		// --------------

		mainInstance = null;

	}

	/**
	 * Конструктор класса XFormPanel с начальным показом XForm.
	 */
	public XFormPanel(final CompositeContext context, final DataPanelElementInfo element,
			final XForm xform1) {

		setContext(context);
		setElementInfo(element);

		setCallbackJSNIFunction();

		// --------------

		mainInstance = null;

		p.clear();
		p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

		if (xform1 == null) {
			setXFormPanel();
		} else {

			RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
			RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);

			setXFormPanelByXForms(xform1);
		}

	}

	@Override
	public void reDrawPanel(final CompositeContext context) {

		reDrawPanelExt(context, null);

	}

	/**
	 * Расширенная ф-ция reDrawPanel. Используется в рабочем режиме и для тестов
	 * 
	 * @param context
	 *            CompositeContext
	 * @param xform1
	 *            XForms
	 */
	public void reDrawPanelExt(final CompositeContext context, final XForm xform1) {

		setContext(context);
		// --------------

		p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			p.clear();
			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		}

		if (xform1 == null) {
			setXFormPanel();
		} else {
			RootPanel.get(SHOWCASE_APP_CONTAINER).clear();
			RootPanel.get(SHOWCASE_APP_CONTAINER).add(p);

			setXFormPanelByXForms(xform1);
		}

	}

	private void setXFormPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getXForms(new XFormContext(getContext(), mainInstance), getElementInfo(),
				new GWTServiceCallback<XForm>("при получении данных XForm с сервера") {

					@Override
					public void onSuccess(final XForm xform1) {
						setXFormPanelByXForms(xform1);
					}
				});

	}

	private void setXFormPanelByXForms(final XForm xform1) {
		xform = xform1;

		destroy();

		// p.setSize(PROC100, PROC100);

		xf = new HTML();

		xf.setWidth(PROC100);

		p.clear();
		p.add(xf);

		instrumentForm(xf, xform.getXFormParts());

		Action ac = xform.getActionForDependentElements();
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, xform);
			ActionExecuter.execAction();
		}

		if (getElementInfo().getRefreshByTimer()) {
			Timer timer = getTimer();
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer() {

				@Override
				public void run() {
					refreshPanel();
				}

			};
			final int n1000 = 1000;
			timer.schedule(getElementInfo().getRefreshInterval() * n1000);
		}

		// p.setHeight(PROC100);
		p.setSize(PROC100, PROC100);
	}

	/**
	 * Возвращает содержимое mainInstance.
	 * 
	 * @return содержимое mainInstance
	 * 
	 */
	public String fillAndGetMainInstance() {
		fillMainInstance();
		return mainInstance;
	}

	private native void fillMainInstance() /*-{
		if ($wnd.xforms.defaultModel != null) {
			this.@ru.curs.showcase.app.client.XFormPanel::mainInstance = $wnd.Writer
					.toString($wnd.xforms.defaultModel
							.getInstanceDocument('mainInstance'));
		}
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
		$wnd.gwtXFormOnChooseFiles = @ru.curs.showcase.app.client.utils.InlineUploader::onChooseFiles(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Boolean;);
	}-*/;

	// CHECKSTYLE:ON

	/**
	 * Инициализирует X-форму, запуская инструментующий javascript.
	 */
	private static native void initForm() /*-{
		$wnd.init();
	}-*/;

	/**
	 * Инструментует панель с формой.
	 */
	private static void instrumentForm(final HTML html, final List<String> stringList) {

		// прописываем HTML формы
		if (stringList.size() > 0) {
			html.setHTML(stringList.get(0));
		}

		// прописываем динамический CSS
		if (stringList.size() > 1) {
			AccessToDomModel.addCSS(stringList.get(1));
		}

		// выставляем скрипты
		for (int i = 2; i < stringList.size(); i++) {
			AccessToDomModel.addScript(stringList.get(i));
		}

		initForm();

	}

	/**
	 * Закрывает X-форму, деиницилизирует инструментовку и подчищает все
	 * динамически созданные скрипты и стили.
	 */
	private static native void destroy() /*-{
		//Деинициализируем механизм XSLTForms
		if ($wnd.xforms != null)
			$wnd.xforms.close();

		//Подчищаем динамические стили
		var hdr = $doc.getElementsByTagName('head')[0];
		var ss1 = $doc.getElementById('dynastyle');
		if (ss1 != null)
			hdr.removeChild(ss1);

		//Подчищаем
		ss1 = $doc.getElementById('xf-model-config');
		if (ss1 != null)
			hdr.removeChild(ss1);

		//Подчищаем динамические скрипты
		var div = $doc.getElementById('target');
		while (div.childNodes.length > 0)
			div.removeChild(div.firstChild);

	}-*/;

	/**
	 * Закрывает форму, снимая всю ранее выставленную Javascript-инструментовку.
	 */
	public static synchronized void destroyXForms() {
		destroy();

		List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
		for (int i = 0; i < uiDataPanel.size(); i++) {
			List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
			for (int j = 0; j < uiElements.size(); j++) {
				if (uiElements.get(j).getElementPanel().getElementInfo().getType() == DataPanelElementType.XFORMS) {
					uiElements.get(j).getElementPanel().getPanel().clear();
					((XFormPanel) uiElements.get(j).getElementPanel()).xf = null;
				}
			}
		}

	}

	@Override
	public void prepareSettings(final boolean keepElementSettings) {
		if (keepElementSettings) {
			if (xf != null) {
				fillMainInstance();
			}
		} else {
			mainInstance = null;
		}
	}

	/**
	 * Должна вызываться перед показом модального окна, содержащего XFormPanel.
	 * 
	 * @param bep
	 *            - BasicElementPanel, который будет отображаться в модальном
	 *            окне
	 */
	public static void beforeModalWindow(final BasicElementPanel bep) {
		if ((bep instanceof XFormPanel) && (bep != null)) {
			List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
			for (int i = 0; i < uiDataPanel.size(); i++) {
				List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
				for (int j = 0; j < uiElements.size(); j++) {
					if ((uiElements.get(j).getElementPanel().getElementInfo().getTab() == bep
							.getElementInfo().getTab())
							&& (uiElements.get(j).getElementPanel().getElementInfo().getType() == DataPanelElementType.XFORMS)
							&& (uiElements.get(j).getElementPanel() != bep)) {
						((XFormPanel) uiElements.get(j).getElementPanel())
								.reDrawBeforeModalWindow();
					}
				}
			}

			// destroy();

		}
	}

	/**
	 * Вызывается перед показом модального окна, содержащего XFormPanel.
	 */
	public void reDrawBeforeModalWindow() {
		fillMainInstance();

		p.clear();
		p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));

		xf = null;
	}

	@Override
	public final void refreshPanel() {
		p.setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if (this.getElementInfo().getShowLoadingMessage()) {
			p.clear();
			p.add(new HTML(Constants.PLEASE_WAIT_DATA_ARE_LOADING));
		}
		setXFormPanel();

	}

	@Override
	public CompositeContext getDetailedContext() {
		return new XFormContext(getContext(), fillAndGetMainInstance());
	}
}
