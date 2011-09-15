package ru.curs.showcase.app.client.api;

import java.util.List;

import ru.beta2.extra.gwt.ui.selector.SelectorComponent;
import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Класс, реализующий функции обратного вызова из XFormPanel.
 * 
 */
public final class XFormPanelCallbacksEvents {

	/**
	 * Тестовая XFormPanel.
	 */
	private static XFormPanel testXFormPanel = null;

	public static void setTestXFormPanel(final XFormPanel testXFormPanel1) {
		testXFormPanel = testXFormPanel1;
	}

	private XFormPanelCallbacksEvents() {

	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Сохранить в XForm.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            - Данные xForm'ы
	 */
	public static void xFormPanelClickSave(final String xformId, final String linkId,
			final String data) {

		final XFormPanel curXFormPanel = getCurrentPanel(xformId);

		if (curXFormPanel != null) {
			// MessageBox.showSimpleMessage("Save. xformId=" + xformId +
			// ", linkId=" + linkId, data);

			final Action ac = getActionByLinkId(linkId, curXFormPanel);

			if (curXFormPanel.getElementInfo().getSaveProc() != null) {
				curXFormPanel.getDataService().saveXForms(
						new XFormContext(curXFormPanel.getContext(), data),
						curXFormPanel.getElementInfo(),
						new GWTServiceCallback<Void>(Constants.XFORM_SAVE_DATA_ERROR) {

							@Override
							public void onSuccess(final Void result) {
								if (curXFormPanel.getUw() != null) {
									submitUploadForm(data, curXFormPanel, ac);
								} else {
									runAction(ac);
								}
							}

						});
			} else {
				runAction(ac);
			}

		}
	}

	private static void runAction(final Action ac) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentAction(ac);
			ActionExecuter.execAction();
		}
	}

	private static Action
			getActionByLinkId(final String linkId, final XFormPanel currentXFormPanel) {
		Action ac = null;

		List<HTMLEvent> events =
			((XForm) currentXFormPanel.getElement()).getEventManager().getEventForLink(linkId);
		// TODO сделал для простоты т.к. сейчас для xforms не может вернутся
		// более 1 события
		if (events.size() > 0) {
			ac = events.get(0).getAction();
		}
		return ac;
	}

	private static void submitUploadForm(final String data, final XFormPanel currentXFormPanel,
			final Action ac) {
		UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
		try {
			XFormContext xcontext = new XFormContext(currentXFormPanel.getContext(), data);
			uh.addStdPostParamsToBody(xcontext, currentXFormPanel.getElementInfo());
		} catch (SerializationException e) {
			MessageBox.showSimpleMessage(Constants.XFORMS_UPLOAD_ERROR, e.getMessage());
		}

		currentXFormPanel.getPanel().add(uh);
		uh.submit(new UploadSubmitEndHandler() {

			@Override
			public void onEnd(final boolean aRes) {
				runAction(ac);
			}
		});
		uh.clear();
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Отфильтровать в
	 * XForm.
	 * 
	 * @param xformId
	 *            Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void xFormPanelClickFilter(final String xformId, final String linkId,
			final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			// MessageBox
			// .showSimpleMessage("Filter. xformId=" + xformId + ", linkId=" +
			// linkId, data);

			Action ac = getActionByLinkId(linkId, currentXFormPanel);

			if (ac != null) {
				ac.filterBy(data);
				runAction(ac);
			}
		}
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Обновить в XForm.
	 * 
	 * @param xformId
	 *            Id элемента xForm.
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void xFormPanelClickUpdate(final String xformId, final String linkId,
			final String overridenAddContext) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			Action ac = getActionByLinkId(linkId, currentXFormPanel);
			if (ac != null) {
				ac = ac.gwtClone();
				if (overridenAddContext != null) {
					ac.setAdditionalContext(overridenAddContext);
				}
				runAction(ac);
			}
		}
	}

	/**
	 * Статический метод для открытия окна-селектора. Может быть использован
	 * непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showSelector(final JavaScriptObject o) {

		/**
		 * SelectorParam
		 */
		final class SelectorParam extends JavaScriptObject {

			protected SelectorParam() {

			};

			/**
			 * Id элемента xForm
			 * 
			 * @return String
			 */
			native String id()/*-{
		return this.id;
	}-*/;

			/**
			 * Название процедуры получения общего числа записей
			 * 
			 * @return String
			 */
			native String procCount()/*-{
		return this.procCount;
	}-*/;

			/**
			 * Название процедуры получения записей как таковых
			 * 
			 * @return String
			 */
			native String procList()/*-{
		return this.procList;
	}-*/;

			/**
			 * Название процедуры получения и общего числа записей, и записей
			 * как таковых
			 * 
			 * @return String
			 */
			native String procListAndCount()/*-{
		return this.procListAndCount;
	}-*/;

			/**
			 * общие фильтры. Этот параметр передаётся хранимой процедуре БД
			 * (см. ниже) без изменений
			 * 
			 * @return String
			 */
			native String generalFilters()/*-{
		return this.generalFilters;
	}-*/;

			/**
			 * начальное значение поискового поля
			 * 
			 * @return String
			 */
			native String currentValue()/*-{
		return this.currentValue;
	}-*/;

			/**
			 * заголовок окна для выбора из больших списков.
			 * 
			 * @return String
			 */
			native String windowCaption()/*-{
		return this.windowCaption;
	}-*/;

			/**
			 * onSelectionComplete
			 * 
			 * @param ok
			 *            boolean
			 * 
			 * @param selected
			 *            JavaScriptObject
			 */
			native void onSelectionComplete(final boolean ok, final JavaScriptObject selected)/*-{
		this.onSelectionComplete(ok, selected);
	}-*/;
		}

		final SelectorParam param = (SelectorParam) o;

		XFormPanel currentXFormPanel = (XFormPanel) ActionExecuter.getElementPanelById(param.id());

		if (currentXFormPanel != null) {

			SelectorComponent c =
				new SelectorComponent(currentXFormPanel.getSelSrv(), param.windowCaption());
			c.setSelectorListener(new SelectorComponent.SelectorListener() {
				@Override
				public void onSelectionComplete(final SelectorComponent selector) {
					param.onSelectionComplete(selector.isOK(), selector.getSelectedAsJsObject());
				}
			});
			c.center();

			String procName;
			if (param.procListAndCount() == null) {
				procName =
					param.procCount() + "FDCF8ABB9B6540A89E350010424C2B80" + param.procList();
			} else {
				procName = param.procListAndCount();
			}

			c.initData(param.generalFilters(), procName, param.currentValue());

		}

	}

	/**
	 * Загружает файл с сервера.
	 * 
	 * @param xformId
	 *            Id элемента xForm
	 * 
	 * @param linkId
	 *            Идентификатор события
	 * 
	 * @param data
	 *            Данные xForm'ы
	 */
	public static void downloadFile(final String xformId, final String linkId, final String data) {
		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {

			// MessageBox.showSimpleMessage(
			// "downloadFile. xformId=" + xformId + ", linkId=" + linkId, data);

			DownloadHelper dh = DownloadHelper.getInstance();
			dh.clear();
			dh.setErrorCaption(Constants.XFORMS_DOWNLOAD_ERROR);
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/download");

			try {
				dh.addParam("linkId", URL.encode(linkId));
				dh.addStdPostParamsToBody(new XFormContext(currentXFormPanel.getContext(), data),
						currentXFormPanel.getElementInfo());
				dh.submit();
			} catch (Exception e) {
				MessageBox.showSimpleMessage(Constants.XFORMS_DOWNLOAD_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * Загружает файл на сервер.
	 * 
	 * @param o
	 *            JavaScriptObject
	 * 
	 */
	public static void uploadFile(final JavaScriptObject o) {
		/**
		 * SelectorParam
		 */
		final class UploadParam extends JavaScriptObject {

			protected UploadParam() {

			};

			/**
			 * Id элемента xForm
			 * 
			 * @return String
			 */
			native String xformsId()/*-{
		return this.xformsId;
	}-*/;

			/**
			 * Id ссылки на файл.
			 * 
			 * @return String
			 */
			native String linkId()/*-{
		return this.linkId;
	}-*/;

			/**
			 * onSelectionComplete
			 * 
			 * @param ok
			 *            boolean
			 * 
			 * @param fileName
			 *            String
			 */
			native void onSelectionComplete(final boolean ok, final String fileName)/*-{
		this.onSelectionComplete(ok, fileName);
	}-*/;
		}
		final UploadParam param = (UploadParam) o;

		final XFormPanel currentXFormPanel = getCurrentPanel(param.xformsId());

		if (currentXFormPanel != null) {

			if (currentXFormPanel.getUw() == null) {
				currentXFormPanel.setUw(new UploadWindow(Constants.XFORM_UPLOAD_CAPTION));
				currentXFormPanel.getPanel().add(currentXFormPanel.getUw());
				UploadHelper uh = currentXFormPanel.getUw().getUploadHelper();
				uh.setErrorCaption(Constants.XFORMS_UPLOAD_ERROR);
				uh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/upload");
			}
			currentXFormPanel.getUw().runUpload(param.linkId(), new UploadEndHandler() {

				@Override
				public void onEnd(final boolean res, final String filePath) {
					int index = filePath.lastIndexOf('\\');
					String fileName = filePath;
					if (index > -1) {
						fileName = fileName.substring(++index);
					}
					param.onSelectionComplete(res, fileName);
				}

			});
		}
	}

	/**
	 * Возвращает текущую XFormPanel.
	 * 
	 * @param xformId
	 *            - Id элемента xForm.
	 * @return XFormPanel
	 */
	public static XFormPanel getCurrentPanel(final String xformId) {
		if (testXFormPanel == null) {
			return (XFormPanel) ActionExecuter.getElementPanelById(xformId);
		} else {
			return testXFormPanel;
		}
	}

}
