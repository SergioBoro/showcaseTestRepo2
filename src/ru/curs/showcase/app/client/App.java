package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Класс точки входа в GWT часть приложения. Используется функция
 * <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * Метод точки входа в приложение Showcase.
	 */
	@Override
	public void onModuleLoad() {
		final CompositeContext context = getCurrentContext();
		setBundleMapForConstants(context);
	}

	private void setBundleMapForConstants(final CompositeContext context) {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getBundle(context, new GWTServiceCallback<Map<String, String>>(
				"Error for bundleMap loading") {

			// new AsyncCallback<Map<String, String>>() {

			@Override
			public void onSuccess(final Map<String, String> arg0) {
				AppCurrContext.getInstance().setBundleMap(arg0);
				initialize(context);
			}

			// @Override
			// public void onFailure(final Throwable arg0) {
			// MessageBox.showSimpleMessage("error", "bundleMap");
			// }
		});
	}

	private void initialize(CompositeContext context) {
		XFormsUtils.initXForms();
		FeedbackJSNI.initFeedbackJSNIFunctions();
		// AppCurrContext.appCurrContext = AppCurrContext.getInstance();
		AppCurrContext.getInstance();

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		// dataService.getServerCurrentState(context, new
		// GWTServiceCallback<ServerState>(
		// AppCurrContext.getInstance().getInternationalizedMessages()
		// .error_of_server_current_state_retrieving_from_server()) {
		dataService.getServerCurrentState(context,
				new GWTServiceCallback<ServerState>(AppCurrContext.getInstance().getBundleMap()
						.get("error_of_server_current_state_retrieving_from_server")) {

					@Override
					public void onSuccess(final ServerState serverCurrentState) {

						if (serverCurrentState != null) {

							AppCurrContext.getInstance().setServerCurrentState(serverCurrentState);
							IDSettings.getInstance().setCaseSensivity(
									serverCurrentState.getCaseSensivityIDs());
							getAndFillMainPage();

						}
					}
				});

	}

	private void getAndFillMainPage() {
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}
		CompositeContext context = getCurrentContext();
		// dataService.getMainPage(context, new
		// GWTServiceCallback<MainPage>(AppCurrContext
		// .getInstance().getInternationalizedMessages()
		// .error_of_main_page_retrieving_from_server()) {
		dataService.getMainPage(context, new GWTServiceCallback<MainPage>(AppCurrContext
				.getInstance().getBundleMap().get("error_of_main_page_retrieving_from_server")) {

			@Override
			public void onSuccess(final MainPage mainPage) {
				AppCurrContext.getInstance().setMainPage(mainPage);
				fillMainPage();
			}

		});
	}

	// генерация и размещение приложения в DOM модели Showcase.
	private void fillMainPage() {

		// генерация и размещение заглавной части (шапки) приложения
		// Showcase
		Header head = new Header();

		RootPanel.get("showcaseHeaderContainer").add(head.generateHeader());
		JavaScriptFromGWTFeedbackJSNI.setCurrentUserDetailsForViewInHTMLControl("HEADER");

		// генерация и размещение нижней части (колонтитул) приложения
		// Showcase
		Footer bottom = new Footer();
		RootPanel.get("showcaseBottomContainer").add(bottom.generateBottom());
		JavaScriptFromGWTFeedbackJSNI.setCurrentUserDetailsForViewInHTMLControl("FOOTER");

		// генерация и размещение главной части (главной) приложения
		// Showcase
		MainPanel mainPanel = new MainPanel();
		AppCurrContext.getInstance().setMainPanel(mainPanel);
		RootPanel.get("showcaseAppContainer").add(mainPanel.startMainPanelCreation());
		// добавляем свои стили после инициализации GWT-шных

		if (AppCurrContext.getInstance().getMainPage().getSolutionCSSFileName() != null
				&& AppCurrContext.getInstance().getMainPage().getSolutionGridCSSFileName() != null
				&& AppCurrContext.getInstance().getMainPage().getProgressBarCSSFileName() != null) {

			addUserDataCSS(AppCurrContext.getInstance().getMainPage().getSolutionCSSFileName(),
					AppCurrContext.getInstance().getMainPage().getSolutionGridCSSFileName(),
					AppCurrContext.getInstance().getMainPage().getProgressBarCSSFileName());

		} else {
			addUserDataCSS("solution.css", "solutionGrid.css", "progressBar.css");
		}
	}

	private CompositeContext getCurrentContext() {
		Map<String, List<String>> params =
			com.google.gwt.user.client.Window.Location.getParameterMap();
		CompositeContext context;
		context = new CompositeContext(params);
		return context;
	}

	private void addUserDataCSS(final String solutionCSS, final String solutionGridCSS,
			final String progressBarCSS) {
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + progressBarCSS));
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + solutionCSS));
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/" + solutionGridCSS));
	}
}
