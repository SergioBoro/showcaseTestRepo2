/**
 * 
 */
package ru.curs.showcase.app.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.*;

import ru.curs.showcase.app.api.MessageType;

/**
 * Интерфейс, ссылающийся на иконки, которые могут понадобится в окне сообщений
 * на клиенте.
 * 
 */
interface ImagesForDialogBox extends ClientBundle {
	/**
	 * Возвращает ImageResource для картинки "ошибка".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_error.png")
	ImageResource getErrorIcon();

	/**
	 * Возвращает ImageResource для картинки "Вниманеи".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_alert.png")
	ImageResource getAlertIcon();

	/**
	 * Возвращает ImageResource для картинки "Инфо".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/message_info.png")
	ImageResource getInfoIcon();

	/**
	 * Возвращает ImageResource для картинки
	 * "arrow_for_disclosure_panel_close.png".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/arrow_for_disclosure_panel_close.png")
	ImageResource getIconArrowForDisclosurePanelClose();

	/**
	 * Возвращает ImageResource для картинки
	 * "arrow_for_disclosure_panel_open.png".
	 * 
	 * @return - ImageResource.
	 */
	@Source("resources/arrow_for_disclosure_panel_open.png")
	ImageResource getIconArrowForDisclosurePanelOpen();

}

/**
 * @author anlug
 * 
 *         Класс сообщений приложения Showcase
 * 
 */
public final class MessageBox {

	/**
	 * GWT сервис для доступа к иконкам, хранящимся на сервере.
	 */
	private static ImagesForDialogBox images =
		(ImagesForDialogBox) GWT.create(ImagesForDialogBox.class);

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	public static final String NBSP = "&nbsp;";

	private static final int Z_INDEX = 103;

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

		dlg.getElement().getStyle().setZIndex(Z_INDEX);

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
	 * @return возвращает DialogBox
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
	 * @param messageSubtype
	 *            - подтип сообщения
	 * @return возвращает DialogBox
	 * 
	 */
	public static DialogBox showMessageWithDetails(final String caption, final String message,
			final String hideMessage, final MessageType messageType,
			final Boolean showDetailedMessage, final String messageSubtype) {
		final DialogBox dlg = createDialogBoxWithClosingOnEsc();

		dlg.getElement().getStyle().setZIndex(Z_INDEX);

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

		if (messageSubtype == null) {
			switch (messageType) {
			case INFO:
				im1.setResource(images.getInfoIcon());
				break;

			case WARNING:
				im1.setResource(images.getAlertIcon());
				break;

			case ERROR:
				im1.setResource(images.getErrorIcon());
				break;

			default:
				break;
			}
		} else {
			String url = Window.Location.getProtocol() + "//" + Window.Location.getHost()
					+ Window.Location.getPath() + messageSubtype;
			im1.setUrl(url);
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
			// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);
			im.setResource(images.getIconArrowForDisclosurePanelClose());

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
					// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_OPEN_IMAGE);
					im.setResource(images.getIconArrowForDisclosurePanelOpen());

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
					// im.setUrl(Constants.ARROW_FOR_DISCLOSURE_PANEL_CLOSE_IMAGE);
					im.setResource(images.getIconArrowForDisclosurePanelClose());

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
