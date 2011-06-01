/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 */
public class ProgressWindow extends DecoratedPopupPanel {
	ProgressWindow() {
		super();
	}

	/**
	 * Показать по центру окно ожидания.
	 */
	public static void showProgressWindow() {
		final ProgressWindow prWin = new ProgressWindow();

		if (AppCurrContext.getInstance().getProgressWindow() != null) {
			ProgressWindow.closeProgressWindow();
		}
		AppCurrContext.getInstance().setProgressWindow(prWin);
		prWin.setAnimationEnabled(true);
		prWin.setGlassEnabled(true);
		Image waiteImage = new Image();
		waiteImage.setSize("48px", "48px");
		waiteImage.setUrl(Constants.IMAGE_FOR_WAITING_WINDOW);
		prWin.add(waiteImage);
		prWin.setSize("58px", "58px");
		prWin.center();
		prWin.show();
	}

	/**
	 * Скрыть окно ожидания.
	 */
	public static void closeProgressWindow() {
		if (AppCurrContext.getInstance().getProgressWindow() != null) {
			AppCurrContext.getInstance().getProgressWindow().hide();
			AppCurrContext.getInstance().setProgressWindow(null);
		}
	}
}
