package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.MainPage;
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
		addUserDataCSS();
		FeedbackJSNI.initFeedbackJSNIFunctions();
		// AppCurrContext.appCurrContext = AppCurrContext.getInstance();
		AppCurrContext.getInstance();

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		CompositeContext context = getCurrentContext();

		dataService.getMainPage(context, new GWTServiceCallback<MainPage>("dsds") {

			@Override
			public void onSuccess(final MainPage mainPage) {
				AppCurrContext.getInstance().setMainPage(mainPage);
				fillMainPage();
			}

		});
		// getMainPage();

	}

	// генерация и размещение приложения в DOM модели Showcase.
	private void fillMainPage() {

		// генерация и размещение заглавной части (шапки) приложения
		// Showcase
		Header head = new Header();
		RootPanel.get("showcaseHeaderContainer").add(head.generateHeader());

		// генерация и размещение нижней части (колонтитул) приложения
		// Showcase
		Footer bottom = new Footer();
		RootPanel.get("showcaseBottomContainer").add(bottom.generateBottom());

		// генерация и размещение главной части (главной) приложения
		// Showcase
		MainPanel mainPanel = new MainPanel();
		AppCurrContext.getInstance().setMainPanel(mainPanel);
		RootPanel.get("showcaseAppContainer").add(mainPanel.startMainPanelCreation());
	}

	private CompositeContext getCurrentContext() {
		Map<String, List<String>> params =
			com.google.gwt.user.client.Window.Location.getParameterMap();
		CompositeContext context;
		context = new CompositeContext(params);
		return context;
	}

	private void addUserDataCSS() {
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/solution.css"));
		AccessToDomModel.addCSSLink(MultiUserData.getPathWithUserData("css/solutionGrid.css"));
	}
}
