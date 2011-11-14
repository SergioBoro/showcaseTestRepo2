/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;
import ru.curs.showcase.app.api.BrowserType;

import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * Класс, генерирующий окно "О программе".
 * 
 * @author anlug
 * 
 */
public final class About {

	private About() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Процедура показывающая окно "О программе".
	 */
	public static void showAbout() {
		DialogBoxWithCaptionButton db = new DialogBoxWithCaptionButton("О программе...");
		// MessageBox.showSimpleMessage("1", "1");
		HTML about = new HTML();
		// MessageBox.showSimpleMessage("1", "11");
		String fff =
			(AppCurrContext.getInstance().getServerCurrentState().getIsNativeUser()) ? "внутренним"
					: "внешним";
		// MessageBox.showSimpleMessage("1", "12");
		String userAgent = getUserAgent();
		BrowserType browserType = null;
		String browserVersion = null;
		String browserTypeString = null;
		// MessageBox.showSimpleMessage("1", "13");

		if (userAgent != null) {
			browserVersion = ru.curs.showcase.app.api.BrowserType.detectVersion(userAgent);
			browserType = ru.curs.showcase.app.api.BrowserType.detect(userAgent);

			browserTypeString = (browserType != null) ? browserType.getName() : null;

		}
		// MessageBox.showSimpleMessage("1", "2");
		final String br = "<br />";
		String textHTML =
			"<p><img src='resources/internal/logo.gif' alt='КУРС' /></p>"
					+ "<img src='resources/internal/favicon32.png' alt='' />&nbsp;Showcase&nbsp;"
					+ AppCurrContext.getInstance().getServerCurrentState().getAppVersion()
					+ br
					+ br
					+

					"Copyright ООО 'КУРС-ИТ', 1998-2011 "
					+ br
					+ "Тел/факс: +7(495)640-2772"
					+ br
					+ "E-mail: <a href='mailto://info@mail.ru'>info@curs.ru</a>"
					+ " <br/> <a href='http://www.curs.ru' target='_blank'>http://www.curs.ru</a>"
					+ br

					+ br
					+ "Версия SQL сервера: "
					+ AppCurrContext.getInstance().getServerCurrentState().getSqlVersion()
					+ br
					+ "Версия JAVA на сервере: "
					+ AppCurrContext.getInstance().getServerCurrentState().getJavaVersion()
					+ br
					+ "Версия сервлет контейнера: "
					+ AppCurrContext.getInstance().getServerCurrentState()
							.getServletContainerVersion()
					+ br

					+ "Тип браузера: "
					+ ((browserTypeString != null) ? browserTypeString : "не удалось определить")
					+ br

					+ "Версия браузера: "
					+ ((browserVersion != null) ? browserVersion : "не удалось определить")
					+ br

					+ "Текущий пользователь '"
					+ AppCurrContext.getInstance().getServerCurrentState().getUserName()
					+ "'"
					+ "	является " + fff;

		// navigator.userAgent.toLowerCase()
		// ru.curs.showcase.app.api.BrowserType?.detect(String)
		// MessageBox.showSimpleMessage("1", "3");
		about.setHTML(textHTML);
		final int n500 = 500;
		final int n400 = 400;
		about.setPixelSize(n500, n400);
		db.add(about);
		db.center();
		db.show();
		// MessageBox.showSimpleMessage("1", "4");
	}

	/**
	 * Возвращает UserAgent.
	 * 
	 * @return UserAgent
	 */
	public static native String getUserAgent() /*-{
		return $wnd.navigator.userAgent.toLowerCase();
	}-*/;

}
