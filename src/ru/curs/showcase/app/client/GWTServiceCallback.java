package ru.curs.showcase.app.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.app.client.utils.AccessToDomModel;

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
		} else {

			if ((GeneralException.getOriginalExceptionClass(caught) != null) && GeneralException
					.getOriginalExceptionClass(caught).contains("ValidateException")) {

				String textMessage = caught.getMessage();
				if ((textMessage == null) || textMessage.isEmpty()) {
					textMessage = "";
				}

				MessageType typeMessage = GeneralException.getMessageType(caught);
				if (typeMessage == null) {
					typeMessage = MessageType.ERROR;
				}

				String captionMessage = GeneralException.getMessageCaption(caught);
				if (captionMessage == null) {
					captionMessage = msgErrorCaption;
				}

				String subtypeMessage = GeneralException.getMessageSubtype(caught);

				MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage,
						false, subtypeMessage);

			} else {
				String str = GeneralException.getMessageType(caught).getName();
				if (GeneralException.getMessageType(caught) == MessageType.ERROR) {
					str = msgErrorCaption;
				}

				if (GeneralException.generateDetailedInfo(caught)
						.contains("com.google.gwt.user.client.rpc.StatusCodeException")) {
					MessageBox.showMessageWithDetails("Нет связи с сервером",
							"Проверьте наличие связи с сервером или обратитесь к администратору вашей сети",
							GeneralException.generateDetailedInfo(caught), MessageType.ERROR, true,
							null);
				} else {
					MessageBox.showMessageWithDetails(str, caught.getMessage(),
							GeneralException.generateDetailedInfo(caught),
							GeneralException.getMessageType(caught),
							GeneralException.needDetailedInfo(caught), null);
				}
			}

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

		String captionMessage = okMessage.getCaption();
		if (captionMessage == null) {
			captionMessage = AppCurrContext.getInstance().getBundleMap().get("okMessage");
		}

		String subtypeMessage = okMessage.getSubtype();

		MessageBox.showMessageWithDetails(captionMessage, textMessage, "", typeMessage, false,
				subtypeMessage);

	}
}
