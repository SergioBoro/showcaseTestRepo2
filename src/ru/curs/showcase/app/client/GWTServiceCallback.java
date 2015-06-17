package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Базовый класс для асинхронных колбэков при вызове сервисов gwt.
 * 
 * @param <T>
 *            тип результата
 */
public abstract class GWTServiceCallback<T> implements AsyncCallback<T> {

	public GWTServiceCallback(final String amsgErrorCaption) {
		super();
		this.msgErrorCaption = amsgErrorCaption;
	}

	/**
	 * Сообщение, которое будет показано в случае, если в процессе вызова
	 * сервиса GWT произошла ошибка.
	 */
	private final String msgErrorCaption;

	@Override
	public void onFailure(final Throwable caught) {

		if (caught.getMessage().contains(ExchangeConstants.SESSION_NOT_AUTH_SIGN)) {

			Window.Location.assign(AccessToDomModel.getAppContextPath() + "/sestimeout.jsp");
			// Window.Location.replace(Window.Location.getPath() + "logout");

		} else {

			String str = GeneralException.getMessageType(caught).getName();
			if (GeneralException.getMessageType(caught) == MessageType.ERROR) {
				// str = str + " " + msgErrorCaption;
				str = msgErrorCaption;
			}

			if (GeneralException.generateDetailedInfo(caught).contains(
					"com.google.gwt.user.client.rpc.StatusCodeException")) {

				MessageBox
						.showMessageWithDetails(
								"Нет связи с сервером",
								"Проверьте наличие связи с сервером или обратитесь к администратору вашей сети",
								GeneralException.generateDetailedInfo(caught), MessageType.ERROR,
								true);

			} else {
				MessageBox.showMessageWithDetails(str, caught.getMessage(),
					GeneralException.generateDetailedInfo(caught),
					GeneralException.getMessageType(caught),
					GeneralException.needDetailedInfo(caught));
			}

			// MessageBox.showMessageWithDetails(msgErrorCaption,
			// caught.getMessage(),
			// GeneralServerException
			// .checkExeptionTypeAndCreateDetailedTextOfException(caught));
			//
			// GeneralServerException.needDetailedInfo(caught);

			// GeneralServerException.getMessageType(caught);

		}

	}

	@Override
	public void onSuccess(final T dataPanelElement) {
		UserMessage okMessage = null;
		if (dataPanelElement instanceof DataPanelElement) {
			okMessage = ((DataPanelElement) dataPanelElement).getOkMessage();
		}
		if (dataPanelElement instanceof VoidElement) {
			okMessage = ((VoidElement) dataPanelElement).getOkMessage();
		}
		if (okMessage == null) {
			return;
		}

		String textMessage = okMessage.getText();
		if ((textMessage == null) || textMessage.isEmpty()) {
			return;
		}

		MessageType typeMessage = okMessage.getType();
		if (typeMessage == null) {
			typeMessage = MessageType.INFO;
		}

		// MessageBox.showSimpleMessage(dataPanelElement.getClass().getName(),
		// okMessage);

		MessageBox.showMessageWithDetails(
				AppCurrContext.getInstance().getBundleMap().get("okMessage"), textMessage, "",
				typeMessage, false);

	}
}
