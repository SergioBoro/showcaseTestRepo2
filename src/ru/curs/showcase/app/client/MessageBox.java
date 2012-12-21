/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.client.api.Constants;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс сообщений приложения Showcase
 * 
 */
public final class MessageBox {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	public static final String NBSP = "&nbsp;";

	private MessageBox() {
		super();
	}

	/**
	 * 
	 * Процедура вывода стандартного сообщения с одной кнопкой ОК в стиле gwt.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showSimpleMessage(final String caption, final String message) {
		final DialogBox dlg = new DialogBox();
		VerticalPanel dialogContents = new VerticalPanel();
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});
		Label l = new Label(message);
		dialogContents.add(l);
		dialogContents.add(ok);
		dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		dlg.center();
		ok.setFocus(true);
		return dlg;
	}

	/**
	 * 
	 * Функция создания окна DialogBox, которое закрывается горячей клавишей
	 * ESC.
	 * 
	 * @return возвращает DialogBox.
	 * 
	 */
	public static DialogBox createDialogBoxWithClosingOnEsc() {

		return new DialogBox() {
			@Override
			protected void onPreviewNativeEvent(final NativePreviewEvent event) {

				if ((event.getTypeInt() == Event.ONKEYUP)
						&& (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)) {
					hide();
				}

				super.onPreviewNativeEvent(event);
			}
		};
	}

	/**
	 * 
	 * Процедура вывода сообщения с одной кнопкой ОК в стиле gwt c текстом для
	 * скрытия.
	 * 
	 * @param caption
	 *            - заглавие окна сообщения
	 * @param message
	 *            - текст сообщения
	 * @param hideMessage
	 *            - текст сообщения, который скрывается
	 * @param messageType
	 *            - тип сообщения: информация, ошибка, предупреждение
	 * @param showDetailedMessage
	 *            - переменная, определяющая показывать ли в сообщении
	 *            "подробности"
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showMessageWithDetails(final String caption, final String message,
			final String hideMessage, final MessageType messageType,
			final Boolean showDetailedMessage) {
		final DialogBox dlg = createDialogBoxWithClosingOnEsc();

		dlg.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		final int n = 10;
		dialogContents.setSpacing(n);
		dlg.setWidget(dialogContents);
		dlg.setAnimationEnabled(true);
		dlg.setGlassEnabled(true);
		dlg.setText(caption);
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				dlg.hide();
			}
		});

		HorizontalPanel horPan = new HorizontalPanel();
		final int n5 = 5;
		horPan.setSpacing(n5);
		dialogContents.add(horPan);

		horPan.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horPan.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		Image im1 = new Image();

		switch (messageType) {

		case INFO:
			im1.setUrl(Constants.MESSAGE_INFO_IMAGE);
			break;

		case WARNING:
			im1.setUrl(Constants.MESSAGE_ALERT_IMAGE);
			break;

		case ERROR:
			im1.setUrl(Constants.MESSAGE_ERROR_IMAGE);
			break;

		default:
			break;
		}

		horPan.add(im1);

		Label l = new Label(message);
		horPan.setCellHorizontalAlignment(l, HasHorizontalAlignment.ALIGN_LEFT);
		horPan.add(l);

		if (messageType == MessageType.ERROR) {
			HorizontalPanel consoleLinkPanel = new HorizontalPanel();
			dialogContents.add(consoleLinkPanel);
			consoleLinkPanel.setWidth(SIZE_ONE_HUNDRED_PERCENTS);
			consoleLinkPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			Anchor anchor = new Anchor("Веб-консоль", "log/lastLogEvents.jsp", "_blank");
			consoleLinkPanel.add(anchor);
		}

		if (showDetailedMessage) {
			final DisclosurePanel dp = new DisclosurePanel();

			HorizontalPanel hp = new HorizontalPanel();

			hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			Image im = new Image();
			im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);

			hp.add(im);
			hp.add(new HTML(NBSP + "Показать подробную информацию" + NBSP + NBSP));
			dp.setHeader(hp);

			dialogContents.add(dp);
			final TextArea textArea = new TextArea();
			dp.addOpenHandler(new OpenHandler<DisclosurePanel>() {

				@Override
				public void onOpen(final OpenEvent<DisclosurePanel> arg0) {

					HorizontalPanel hp = new HorizontalPanel();

					hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					Image im = new Image();
					im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_OPEN_IMAGE);
					textArea.setSize("600px", "250px");
					hp.add(im);
					hp.add(new HTML(NBSP + "Скрыть подробную информацию" + NBSP + NBSP));
					dp.setHeader(hp);

				}

			});

			dp.addCloseHandler(new CloseHandler<DisclosurePanel>() {

				@Override
				public void onClose(final CloseEvent<DisclosurePanel> arg0) {
					HorizontalPanel hp = new HorizontalPanel();

					hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					Image im = new Image();
					im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);

					hp.add(im);
					hp.add(new HTML(NBSP + "Показать подробную информацию"));
					dp.setHeader(hp);

				}

			});

			dp.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
			textArea.setSize("90%", SIZE_ONE_HUNDRED_PERCENTS);
			final int n1 = 5;
			textArea.setVisibleLines(n1);
			textArea.setText(hideMessage);

			textArea.setReadOnly(true);
			dp.setContent(textArea);
		}
		dialogContents.add(ok);
		dialogContents.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);
		dlg.center();
		ok.setFocus(true);

		return dlg;

	}
}
