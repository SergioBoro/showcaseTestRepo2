/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;

import com.google.gwt.user.client.ui.*;

/**
 * 
 * Класс, генерирующий модальное окно с переданным в него в виде HTML
 * содержанием. Окно может содержать кнопку закрыть (в верхнем правом углу).
 * 
 * @author anlug
 * 
 */
public final class ModalWindowWithHTMLContent {

	private static DialogBox currentOpenModalWindowsl = null;

	// public static DialogBox currentOpenModalWindowsl;

	private ModalWindowWithHTMLContent() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Процедура показывающая окно.
	 * 
	 * @param caption
	 *            - заголовок окна.
	 * @param textHTML
	 *            - HTML, который будет отображаться внутри окна.
	 * @param showCloseButton
	 *            - параметр определяющий будет ли показываться кнопка "Закрыть"
	 *            и действовать клавиша Esc на закрытие окна.
	 * 
	 */
	public static void showWindow(final String caption, final String textHTML,
			final Boolean showCloseButton) {

		if (getCurrentOpenModalWindowsl() == null) {

			if (showCloseButton) {
				showWindowWithCloseButton(caption, textHTML);
			} else {
				showWindowWithoutCloseButton(caption, textHTML);
			}
		}

	}

	public static void showWindowWithCloseButton(final String caption, final String textHTML) {
		DialogBoxWithCaptionButton db = new DialogBoxWithCaptionButton(caption) {

			@Override
			public void closeWindow() {
				ModalWindowWithHTMLContent.closeWindow();
			}

		};
		HTML html = new HTML();

		html.setHTML(textHTML);
		final int n500 = 500;
		final int n400 = 400;
		html.setPixelSize(n500, n400);
		db.add(html);
		setCurrentOpenModalWindowsl(db);
		db.center();
		db.show();
	}

	public static void showWindowWithoutCloseButton(final String caption, final String textHTML) {

		DialogBox db = new DialogBox();

		db.setText(caption);

		HTML html = new HTML();

		html.setHTML(textHTML);
		final int n500 = 500;
		final int n400 = 400;
		html.setPixelSize(n500, n400);
		db.add(html);
		setCurrentOpenModalWindowsl(db);
		db.center();
		db.show();

	}

	public static DialogBox getCurrentOpenModalWindowsl() {
		return currentOpenModalWindowsl;
	}

	public static void setCurrentOpenModalWindowsl(final DialogBox acurrentOpenModalWindowsl) {
		ModalWindowWithHTMLContent.currentOpenModalWindowsl = acurrentOpenModalWindowsl;
	}

	public static void closeWindow() {
		if (getCurrentOpenModalWindowsl() != null) {
			getCurrentOpenModalWindowsl().hide();
			setCurrentOpenModalWindowsl(null);
		}

	}
}
