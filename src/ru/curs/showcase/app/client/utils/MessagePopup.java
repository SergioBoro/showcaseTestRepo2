package ru.curs.showcase.app.client.utils;

import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.widget.core.client.Popup;

/**
 * Компонент для выдачи всплывающего сообщения.
 * 
 */
public final class MessagePopup extends Popup {

	public MessagePopup(final String message) {
		super();

		MouseDownHandler handler = new MouseDownHandler() {
			@Override
			public void onMouseDown(final MouseDownEvent arg0) {
				hide();
			}
		};
		addDomHandler(handler, MouseDownEvent.getType());

		setAutoHide(false);

		addStyleName("message-popup-panel");

		HTML ht = new HTML(message);
		ht.addStyleName("message-popup-label");
		add(ht);

	}

	public void show(final Widget w) {
		Timer tm = new Timer() {
			@Override
			public void run() {
				cancel();
				hide();
			}
		};
		tm.schedule(Constants.GRID_MESSAGE_POPUP_EXPORT_TO_EXCEL_DELAY);

		show(w.getElement(), new Style.AnchorAlignment(Style.Anchor.RIGHT));
	}
}
