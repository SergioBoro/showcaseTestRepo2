/**
 * 
 */
package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;

import com.google.gwt.core.client.GWT;

/**
 * 
 * Класс-исполнитель действий (Action), которые возникают (вызываются) при
 * нажатии (кликах) внутри BasicElementPanel.
 * 
 * @author anlug
 * 
 */
public final class ActionExecuter {

	/**
	 * Создает удаленный proxy сервис для общения с серверной частью
	 * DataService.
	 */
	private static DataServiceAsync dataService = null;

	private ActionExecuter() {
		super();
	}

	/**
	 * 
	 * Функция, которая "исполняет" текущее действие при клике на элемент.
	 * 
	 */
	public static void execAction() {

		final Action ac = AppCurrContext.getInstance().getCurrentAction();

		if (ac == null) {
			return;
		}

		if (ac.containsServerActivity()) {
			if (dataService == null) {
				dataService = GWT.create(DataService.class);
			}
			dataService.execServerAction(ac, new GWTServiceCallback<Void>(
					Constants.ERROR_IN_SERVER_ACTIVITY) {

				@Override
				public void onSuccess(final Void fakeRes) {
					execClientAction(ac);
				}

			});
		} else {
			execClientAction(ac);
		}
	}

	private static void execClientAction(final Action ac) {
		if (ac.getNavigatorActionType() != NavigatorActionType.DO_NOTHING) {

			boolean fireSelectionAction =
				ac.getNavigatorActionType() == NavigatorActionType.CHANGE_NODE_AND_DO_ACTION;
			Accordeon acrd = AppCurrContext.getInstance().getMainPanel().getAccordeon();
			if (ac.getNavigatorElementLink().getRefresh()) {
				acrd.refreshAccordeon(ac.getNavigatorElementLink().getId(), fireSelectionAction);
			} else {
				acrd.selectNesessaryItemInAccordion(ac.getNavigatorElementLink().getId(),
						fireSelectionAction);
			}

		}

		final DataPanelActionType dpat = ac.getDataPanelActionType();

		switch (dpat) {
		case DO_NOTHING:
			break;
		case RELOAD_PANEL:
			break;
		case REFRESH_TAB:
			// Обновить вкладку целиком (активную), а перед этим закрыть
			// модальное окно если оно открыто.
			if ((ac.getShowInMode() == ShowInMode.PANEL)
					&& (AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement() != null)) {
				AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement()
						.closeWindow();
			}

			DataPanelTab dpt =
				AppCurrContext.getInstance().getDataPanelMetaData().getActiveTabForAction(ac);

			Collection<DataPanelElementInfo> tabscoll = dpt.getElements();
			Iterator<DataPanelElementInfo> itr = tabscoll.iterator();
			while (itr.hasNext()) {
				DataPanelElementInfo dpe = itr.next();

				if (dpe.getHideOnLoad()) {
					BasicElementPanel bep = getElementPanelById(dpe.getId());
					bep.hidePanel();

				}

				if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
					BasicElementPanel bep = getElementPanelById(dpe.getId());

					bep.showPanel();

					boolean keepElementSettings = bep.getElementInfo().getKeepUserSettings(ac);

					bep.saveSettings(keepElementSettings);
					bep.reDrawPanel(bep.getElementInfo().getContext(ac), false);
				}
			}

			break;
		case RELOAD_ELEMENTS:
			for (int k = 0; k < ac.getDataPanelLink().getElementLinks().size(); k++) {

				String elementIdForDraw = ac.getDataPanelLink().getElementLinks().get(k).getId();

				BasicElementPanel bep = getElementPanelById(elementIdForDraw);
				if (bep != null) {
					if ((ac.getShowInMode() == ShowInMode.PANEL)
							&& (AppCurrContext.getInstance()
									.getCurrentOpenWindowWithDataPanelElement() != null)) {
						AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement()
								.closeWindow();

						// ------------------ если на вкладке есть xForm то
						// прорисовываем ее
						drawXFormPanelsAfterModalWindowShown(ac);
						// ------------

					}
					if ((ac.getShowInMode() == ShowInMode.MODAL_WINDOW)
							&& (AppCurrContext.getInstance()
									.getCurrentOpenWindowWithDataPanelElement() == null)) {

						ModalWindowInfo mwi = ac.getModalWindowInfo();
						WindowWithDataPanelElement modWind = null;
						if (mwi != null) {

							if ((mwi.getCaption() != null) && (mwi.getWidth() != null)
									&& (mwi.getHeight() != null)) {
								modWind =
									new WindowWithDataPanelElement(mwi.getCaption(),
											mwi.getWidth(), mwi.getHeight(),
											mwi.getShowCloseBottomButton());
							} else {

								if (mwi.getCaption() != null) {
									modWind =
										new WindowWithDataPanelElement(mwi.getCaption(),
												mwi.getShowCloseBottomButton());
								} else {
									modWind =
										new WindowWithDataPanelElement(
												mwi.getShowCloseBottomButton());
								}

							}

						} else {
							modWind = new WindowWithDataPanelElement(false);
						}

						modWind.showModalWindow(bep);

					}
					if (ac.getDataPanelLink().getElementLinks().get(k).doHiding()) {
						bep.hidePanel();
						continue;
					}
					bep.showPanel();

					boolean keepElementSettings =
						ac.getDataPanelLink().getElementLinks().get(k).getKeepUserSettings();

					bep.saveSettings(keepElementSettings);

					bep.reDrawPanel(bep.getElementInfo().getContext(ac), ac.getDataPanelLink()
							.getElementLinks().get(k).getRefreshContextOnly());

				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param id
	 *            - Id элемента для которого необходимо вернуть
	 *            BasicElementPanel.
	 * @return - BasicElementPanel
	 */
	public static BasicElementPanel getElementPanelById(final String id) {
		List<UIDataPanelTab> uiDataPanel = AppCurrContext.getInstance().getUiDataPanel();
		for (int i = 0; i < uiDataPanel.size(); i++) {
			List<UIDataPanelElement> uiElements = uiDataPanel.get(i).getUiElements();
			for (int j = 0; j < uiElements.size(); j++) {
				if (uiElements.get(j).getElementPanel().getElementInfo().getId().equals(id)) {
					return uiElements.get(j).getElementPanel();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Функция перерисовки всех xForm элементов на вкладке (tab) (необходима
	 * из-за особого механизма работы с XForm).
	 * 
	 * @param ac
	 *            Action
	 */
	public static void drawXFormPanelsAfterModalWindowShown(final Action ac) {

		if (ac == null) {
			return;
		}
		DataPanelTab dpt1 =
			AppCurrContext.getInstance().getDataPanelMetaData().getActiveTabForAction(ac);

		Collection<DataPanelElementInfo> tabscoll1 = dpt1.getElements();
		Iterator<DataPanelElementInfo> itr1 = tabscoll1.iterator();
		while (itr1.hasNext()) {
			DataPanelElementInfo dpe1 = itr1.next();

			if (!(dpe1.getNeverShowInPanel())) {
				BasicElementPanel bep1 = getElementPanelById(dpe1.getId());
				if (bep1 instanceof XFormPanel) {
					bep1.saveSettings(true);
					bep1.showPanel();
					bep1.reDrawPanel(bep1.getElementInfo().getContext(ac), false);

				}
			}
		}
	}

}
