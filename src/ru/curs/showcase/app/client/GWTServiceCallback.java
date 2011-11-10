package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.ExchangeConstants;
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

			String str = GeneralException.getMessageType(caught).getName() + " ";
			MessageBox.showMessageWithDetails(str + msgErrorCaption, caught.getMessage(),
					GeneralException.generateDetailedInfo(caught),
					GeneralException.getMessageType(caught),
					GeneralException.needDetailedInfo(caught));
			// MessageBox.showMessageWithDetails(msgErrorCaption,
			// caught.getMessage(),
			// GeneralServerException
			// .checkExeptionTypeAndCreateDetailedTextOfException(caught));
			//
			// GeneralServerException.needDetailedInfo(caught);

			// GeneralServerException.getMessageType(caught);

		}

	}
}
