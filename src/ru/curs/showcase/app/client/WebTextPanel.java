package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с WebText.
 */
public class WebTextPanel extends BasicElementPanelBasis {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * VerticalPanel на которой отображен web-text.
	 */
	private final VerticalPanel generalWebTextPanel;

	/**
	 * WebText.
	 */
	private WebText webText;

	/**
	 * HTML элемент, который показывает WebText.
	 */
	private HTML thmlwidget = null;

	public WebTextPanel(final CompositeContext context1, final DataPanelElementInfo element1) {

		this.setContext(context1);
		this.setElementInfo(element1);
		setIsFirstLoading(true);
		generalWebTextPanel = new VerticalPanel();

		generalWebTextPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		thmlwidget = new HTML(Constants.PLEASE_WAIT_WEBTEXT_DATA_ARE_LOADING);

		dataService = GWT.create(DataService.class);
		generalWebTextPanel.add(thmlwidget);
		setWebTextPanel();
	}

	public WebTextPanel(final DataPanelElementInfo element1) {

		this.setElementInfo(element1);

		setContext(null);
		setIsFirstLoading(true);

		thmlwidget = new HTML(Constants.PLEASE_WAIT_WEBTEXT_DATA_ARE_LOADING);
		generalWebTextPanel = new VerticalPanel();

		generalWebTextPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		generalWebTextPanel.add(thmlwidget);

	}

	private void setWebTextPanel() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getWebText(getContext(), getElementInfo(), new GWTServiceCallback<WebText>(
				Constants.ERROR_OF_WEBTEXT_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final WebText awt) {

				webText = awt;
				if (webText != null) {
					fillWebTextPanel(webText);
				}
			}

		});

	}

	/**
	 * 
	 * Заполняет WebTextPanel текстом.
	 * 
	 * @param aWebText
	 *            - WebText
	 */
	protected void fillWebTextPanel(final WebText aWebText) {
		thmlwidget.setHTML(aWebText.getData());
		setCollbackJSNIFunction();
		checkForDefaultAction();

		if (getElementInfo().getRefreshByTimer()) {
			Timer timer = getTimer();
			if (timer != null) {
				timer.cancel();
				// MessageBox.showSimpleMessage("fff", "timer cancel");
			}
			timer = new Timer() {

				@Override
				public void run() {
					refreshPanel();
				}

			};
			setTimer(timer);
			final int n1000 = 1000;
			timer.schedule(getElementInfo().getRefreshInterval() * n1000);

		}

	}

	/**
	 * @return Возвращает текущий объект типа WebText - данные веб текста.
	 */
	public WebText getWebText() {
		return webText;
	}

	// CHECKSTYLE:OFF
	/**
	 * 
	 * Процедура определяющая функцию, которая будет выполняться по клику на
	 * ссылку в WebText.
	 * 
	 */
	public native void setCollbackJSNIFunction() /*-{
		$wnd.gwtWebTextFunc = 
		@ru.curs.showcase.app.client.api.WebTextPanelCallbacksEvents::webTextPanelClick(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
	}-*/;

	// CHECKSTYLE:ON

	@Override
	public VerticalPanel getPanel() {
		return generalWebTextPanel;
	}

	public void setDataService(final DataServiceAsync adataService) {
		this.dataService = adataService;
	}

	public DataServiceAsync getDataService() {
		return dataService;
	}

	@Override
	public void reDrawPanel(final CompositeContext context1, final Boolean refreshContextOnly) {
		setContext(context1);

		// generalWebTextPanel.getOffsetHeight()

		// MessageBox
		// .showSimpleMessage("size",
		// String.valueOf(generalWebTextPanel.getOffsetHeight()));

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if ((!getIsFirstLoading()) && refreshContextOnly) {
			webText.updateAddContext(context1);
		} else {
			thmlwidget.setText(Constants.PLEASE_WAIT_WEBTEXT_DATA_ARE_LOADING);
			if (dataService == null) {
				dataService = GWT.create(DataService.class);
			}

			dataService.getWebText(getContext(), getElementInfo(),
					new GWTServiceCallback<WebText>(
							Constants.ERROR_OF_WEBTEXT_DATA_RETRIEVING_FROM_SERVER) {

						@Override
						public void onSuccess(final WebText awt) {

							webText = awt;
							if (webText != null) {
								fillWebTextPanel(awt);
								getPanel().setHeight(SIZE_ONE_HUNDRED_PERCENTS);
								if (getIsFirstLoading() && refreshContextOnly) {
									webText.updateAddContext(context1);
								}
								setIsFirstLoading(false);
							}
						}
					});
		}

	}

	@Override
	public void hidePanel() {
		generalWebTextPanel.setVisible(false);
	}

	@Override
	public void showPanel() {
		generalWebTextPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (webText.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentAction(webText.getActionForDependentElements());
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return webText;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		thmlwidget.setText(Constants.PLEASE_WAIT_WEBTEXT_DATA_ARE_LOADING);
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getWebText(getContext(), getElementInfo(), new GWTServiceCallback<WebText>(
				Constants.ERROR_OF_WEBTEXT_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final WebText awt) {

				webText = awt;
				if (webText != null) {
					fillWebTextPanel(awt);
					getPanel().setHeight(SIZE_ONE_HUNDRED_PERCENTS);

				}
			}
		});

	}

}
