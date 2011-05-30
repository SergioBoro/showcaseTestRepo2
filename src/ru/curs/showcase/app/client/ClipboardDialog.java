package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс диалога копирования в буфер обмена.
 */
class ClipboardDialog extends DialogBoxWithCaptionButton {

	/**
	 * TextArea ta.
	 */
	private final TextArea ta = new TextArea();

	ClipboardDialog(final String data) {
		setText("Скопируйте текст в буфер обмена");

		final int characterWidth = 60;
		final int visibleLines = 12;

		VerticalPanel p = new VerticalPanel();
		ta.setCharacterWidth(characterWidth);
		ta.setVisibleLines(visibleLines);
		p.add(ta);
		Button btn = new Button("Готово", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				ClipboardDialog.this.hide();
			}
		});
		p.add(btn);
		p.setCellHorizontalAlignment(btn, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(p);

		ta.setText(data);
		ta.setFocus(true);
		ta.setSelectionRange(0, ta.getText().length());
	}

	@Override
	public void center() {
		super.center();
		ta.setFocus(true);
		ta.setSelectionRange(0, ta.getText().length());
	}
}
