package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.beta2.extra.gwt.ui.selector.*;
import ru.beta2.extra.gwt.ui.selector.BaseSelectorComponent.ErrorHandler;
import ru.beta2.extra.gwt.ui.selector.BaseSelectorComponent.Options;
import ru.beta2.extra.gwt.ui.selector.api.SelectorAdditionalData;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.Action;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.utils.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.FormPanel;

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

			// MessageBox.showSimpleMessage("xFormPanelClickSave. xformId=" +
			// xformId + ", linkId="
			// + linkId, data);

			final Action ac = getActionByLinkId(linkId, curXFormPanel);

			if (curXFormPanel.getElementInfo().getSaveProc() != null) {
				curXFormPanel.getDataService().saveXForms(
						new XFormContext(curXFormPanel.getContext(), data),
						curXFormPanel.getElementInfo(),
						new GWTServiceCallback<Void>(Constants.XFORM_SAVE_DATA_ERROR) {

							@Override
							public void onSuccess(final Void result) {
								InlineUploader uploader = new InlineUploader(data, curXFormPanel);
								uploader.checkForUpload(new CompleteHandler() {

									@Override
									public void onComplete(final boolean aRes) {
										// MessageBox.showSimpleMessage("InlineUploaderComplete",
										// "aRes=" + String.valueOf(aRes));

										runAction(ac, curXFormPanel.getElement());
									}

								});

								if (curXFormPanel.getUw() != null) {
									submitUploadForm(data, curXFormPanel, ac);
								}
							}
						});
			} else {
				runAction(ac, curXFormPanel.getElement());
			}
		}
	}

	/**
	 * Функция, которая будет выполняться по клику на кнопку Загрузить в XForm.
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
	public static void simpleUpload(final String xformId, final String linkId, final String data) {
		final XFormPanel curXFormPanel = getCurrentPanel(xformId);

		InlineUploader uploader = new InlineUploader(data, curXFormPanel);
		uploader.singleFormUpload(linkId);
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

		// MessageBox.showSimpleMessage("xFormPanelClickUpdate. xformId=" +
		// xformId + ", linkId="
		// + linkId, overridenAddContext);

		XFormPanel currentXFormPanel = getCurrentPanel(xformId);

		if (currentXFormPanel != null) {
			Action ac = getActionByLinkId(linkId, currentXFormPanel);
			if (ac != null) {
				ac = ac.gwtClone();
				if (overridenAddContext != null) {
					ac.setAdditionalContext(overridenAddContext);
				}
				runAction(ac, currentXFormPanel.getElement());
			}
		}
	}

	private static void runAction(final Action ac, final DataPanelElement aElement) {
		if (ac != null) {
			AppCurrContext.getInstance().setCurrentActionFromElement(ac, aElement);
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
		uh.submit(new CompleteHandler() {

			@Override
			public void onComplete(final boolean aRes) {

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
			// MessageBox.showSimpleMessage("xFormPanelClickFilter. xformId=" +
			// xformId + ", linkId="
			// + linkId, data);

			Action ac = getActionByLinkId(linkId, currentXFormPanel);

			if (ac != null) {
				ac.filterBy(data);
				runAction(ac, currentXFormPanel.getElement());
			}
		}
	}

	/**
	 * Статический метод для открытия окна селектора с единственным выбором.
	 * Может быть использован непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showSelector(final JavaScriptObject o) {

		// MessageBox.showSimpleMessage("showSelector", "showSelector");

		showSingleAndMultiSelector(o, false);
	}

	/**
	 * Статический метод для открытия окна селектора со множественным выбором.
	 * Может быть использован непосредственно в javscript-е на форме.
	 * 
	 * @param o
	 *            Объект-хендлер окна-селектора
	 * 
	 */
	public static void showMultiSelector(final JavaScriptObject o) {
		showSingleAndMultiSelector(o, true);
	}

	/**
	 * SelectorParam.
	 */
	private static final class SelectorParam extends JavaScriptObject {

		protected SelectorParam() {

		};

		/**
		 * Id элемента xForm.
		 * 
		 * @return String
		 */
		native String id()/*-{
			return this.id;
		}-*/;

		/**
		 * Название процедуры получения общего числа записей.
		 * 
		 * @return String
		 */
		native String procCount()/*-{
			return this.procCount;
		}-*/;

		/**
		 * Название процедуры получения записей как таковых.
		 * 
		 * @return String
		 */
		native String procList()/*-{
			return this.procList;
		}-*/;

		/**
		 * Название процедуры получения и общего числа записей, и записей как
		 * таковых.
		 * 
		 * @return String
		 */
		native String procListAndCount()/*-{
			return this.procListAndCount;
		}-*/;

		/**
		 * общие фильтры. Этот параметр передаётся хранимой процедуре БД (см.
		 * ниже) без изменений
		 * 
		 * @return String
		 */
		native Object generalFilters()/*-{
			return this.generalFilters != null ? this.generalFilters : "";
		}-*/;

		/**
		 * начальное значение поискового поля.
		 * 
		 * @return String
		 */
		native String currentValue()/*-{
			return this.currentValue != null ? this.currentValue : "";
		}-*/;

		/**
		 * 
		 * начальное значение галочки "Начинается с".
		 * 
		 * @return boolean
		 */
		native boolean startWith()/*-{
			return this.startWith != null ? this.startWith : true;
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
		 * Мультиселектор. Нужно ли очищать ноду перед вставкой выбранных
		 * значений.
		 * 
		 * @return boolean
		 */
		native boolean needClear()/*-{
			return this.needClear != null ? this.needClear : false;
		}-*/;

		/**
		 * Мультиселектор. Нужно ли загружать начальные выбранные значения.
		 * 
		 * @return boolean
		 */
		native boolean needInitSelection()/*-{
			return this.needInitSelection != null ? this.needInitSelection
					: false;
		}-*/;

		/**
		 * onSelectionComplete.
		 * 
		 * @param ok
		 *            boolean
		 * 
		 * @param selected
		 *            JavaScriptObject
		 */
		native void onSelectionComplete(final boolean ok, final JavaScriptObject selected)/*-{
			if (this.onSelectionComplete != null) {
				this.onSelectionComplete(ok, selected);
			}
		}-*/;

		/**
		 * Мультиселектор. Определяет тег, куда должны попадать записи.
		 * 
		 * @return String
		 */
		native String xpathRoot()/*-{
			return this.xpathRoot;
		}-*/;

		/**
		 * mapping между полями XForm'ы и полями выбранной записи.
		 * 
		 * @return Map<String, String>
		 */
		native Map<String, String> xpathMapping()/*-{
			return this.xpathMapping;
		}-*/;

		/**
		 * Ширина списка с данными.
		 * 
		 * @return String
		 */
		native String dataWidth()/*-{
			return this.dataWidth;
		}-*/;

		/**
		 * Высота списка с данными.
		 * 
		 * @return String
		 */
		native String dataHeight()/*-{
			return this.dataHeight;
		}-*/;

		/**
		 * Мультиселектор. Ширина списка с выбранными данными.
		 * 
		 * @return String
		 */
		native String selectedDataWidth()/*-{
			return this.selectedDataWidth;
		}-*/;

		native String visibleRecordCount()/*-{
			return this.visibleRecordCount;
		}-*/;

	}

	private static void showSingleAndMultiSelector(final JavaScriptObject o,
			final boolean isMultiSelector) {

		final SelectorParam param = (SelectorParam) o;

		// MessageBox.showSimpleMessage(param.windowCaption(),
		// param.windowCaption());

		XFormPanel currentXFormPanel = (XFormPanel) ActionExecuter.getElementPanelById(param.id());

		if (currentXFormPanel != null) {
			Options options = new Options();
			String defaultSelectedDataWidth = options.getSelectedDataWidth();
			if (param.dataWidth() != null) {
				options.dataWidth(param.dataWidth());
			}
			if (param.dataHeight() != null) {
				options.dataHeight(param.dataHeight());
			}
			if (param.selectedDataWidth() != null) {
				options.selectedDataWidth(param.selectedDataWidth());
			}
			if (param.visibleRecordCount() != null) {
				options.visibleRecordCount(Integer.valueOf(param.visibleRecordCount()));
			}

			options.startsWithChecked(param.startWith());

			ErrorHandler errHandler = new ErrorHandler() {
				@Override
				public void onDataServiceFailure(final Throwable throwable) {
					MessageBox.showMessageWithDetails("Ошибка при получении данных для селектора",
							throwable.getMessage(), throwable.getMessage(), MessageType.ERROR,
							false);
				}
			};
			options.errorHandler(errHandler);

			BaseSelectorComponent c;
			if (isMultiSelector) {
				if (param.dataWidth() == null) {
					options.dataWidth(defaultSelectedDataWidth);
				}

				JavaScriptObject initSelection;
				if (param.needInitSelection()) {
					initSelection = getInitSelection(param.xpathRoot(), param.xpathMapping());
				} else {
					initSelection = null;
				}

				c =
					new MultiSelectorComponent(currentXFormPanel.getSelSrv(),
							param.windowCaption(), initSelection, options);
			} else {
				c =
					new SelectorComponent(currentXFormPanel.getSelSrv(), param.windowCaption(),
							options);
			}
			c.setSelectorListener(new BaseSelectorComponent.SelectorListener() {
				@Override
				public void onSelectionComplete(final BaseSelectorComponent selector) {
					if (param.xpathMapping() == null) {
						param.onSelectionComplete(selector.isOK(),
								selector.getSelectedAsJsObject());
					} else {
						if (isMultiSelector) {
							insertXFormByXPath(selector.isOK(), selector.getSelectedAsJsObject(),
									param.xpathRoot(), param.xpathMapping(), param.needClear());
						} else {
							setXFormByXPath(selector.isOK(), selector.getSelectedAsJsObject(),
									param.xpathMapping());
						}

						param.onSelectionComplete(selector.isOK(),
								selector.getSelectedAsJsObject());

					}
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

			SelectorAdditionalData addData = new SelectorAdditionalData();
			addData.setContext(currentXFormPanel.getContext());
			addData.setElementInfo(currentXFormPanel.getElementInfo());

			c.initData(getXMLByXPathArray(param.generalFilters()), procName,
					getValueByXPath(param.currentValue()), addData);
		}
	}

	private static native void setXFormByXPath(final boolean ok, final JavaScriptObject selected,
			final Map<String, String> xpathMapping) /*-{
		$wnd.setXFormByXPath(ok, selected, xpathMapping);
	}-*/;

	private static native void insertXFormByXPath(final boolean ok,
			final JavaScriptObject selected, final String xpathRoot,
			final Map<String, String> xpathMapping, final boolean needClear) /*-{
		$wnd.insertXFormByXPath(ok, selected, xpathRoot, xpathMapping,
				needClear);
	}-*/;

	private static native String getValueByXPath(final String xpath) /*-{
		return $wnd.getValueByXPath(xpath);
	}-*/;

	private static native String getXMLByXPathArray(final Object xpathArray) /*-{
		return $wnd.getXMLByXPathArray(xpathArray);
	}-*/;

	private static native JavaScriptObject getInitSelection(final String xpathRoot,
			final Map<String, String> xpathMapping) /*-{
		return $wnd.getInitSelection(xpathRoot, xpathMapping);
	}-*/;

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
			dh.setEncoding(FormPanel.ENCODING_URLENCODED);
			dh.clear();
			dh.setErrorCaption(Constants.XFORMS_DOWNLOAD_ERROR);
			dh.setAction(ExchangeConstants.SECURED_SERVLET_PREFIX + "/download");

			try {
				dh.addParam("linkId", linkId);
				dh.addStdPostParamsToBody(new XFormContext(currentXFormPanel.getContext(), data),
						currentXFormPanel.getElementInfo());
				dh.submit();
			} catch (SerializationException e) {
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
