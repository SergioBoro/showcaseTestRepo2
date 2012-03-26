/**
 * 
 */
package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.curs.showcase.app.api.ID;
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
	 * Устанавливает related элементы для передачи их в server activity.
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

			ID elementId = AppCurrContext.getInstance().getCurrentElementId();
			CompositeContext panelContext = null;
			if (elementId != null) {
				BasicElementPanel panel = getElementPanelById(elementId);
				panelContext = panel.getContext();
			}
			ac.setRelated(panelContext);

			dataService.execServerAction(ac, new GWTServiceCallback<Void>(
					Constants.ERROR_IN_SERVER_ACTIVITY) {

				@Override
				public void onSuccess(final Void fakeRes) {
					handleClientBlocks(ac);
				}

			});
		} else {
			handleClientBlocks(ac);
		}
	}

	private static void handleClientBlocks(final Action ac) {
		for (Activity act : ac.getClientActivities()) {
			runClientActivity(act.getName(), act.getContext().getMain(), act.getContext()
					.getAdditional(), act.getContext().getFilter());
		}

		handleNavigatorBlock(ac);
		handleDataPanelBlock(ac);
	}

	private static native void runClientActivity(final String procName, final String mainContext,
			final String addContext, final String filterContext)
	/*-{
		var exp = '$wnd.' + procName + "('" + mainContext + "', '" + addContext
		+ "', '" + filterContext + "')";
		eval(exp);
	}-*/;

	private static void handleDataPanelBlock(final Action ac) {
		final DataPanelActionType dpat = ac.getDataPanelActionType();

		switch (dpat) {
		case DO_NOTHING:
			// MessageBox.showSimpleMessage("1", "DO_NOTHING");
			break;
		case RELOAD_PANEL:
			// MessageBox.showSimpleMessage("1", "RELOAD_PANEL");
			break;
		case REFRESH_TAB:
			// MessageBox.showSimpleMessage("1", "231");
			handleRefreshTab(ac);
			break;
		case RELOAD_ELEMENTS:
			// MessageBox.showSimpleMessage("1", "232");
			handleReloadElements(ac);
			break;
		default:
			break;
		}
	}

	private static void handleReloadElements(final Action ac) {
		for (int k = 0; k < ac.getDataPanelLink().getElementLinks().size(); k++) {

			DataPanelElementLink dpel = ac.getDataPanelLink().getElementLinks().get(k);
			ID elementIdForDraw = dpel.getId();

			BasicElementPanel bep = getElementPanelById(elementIdForDraw);
			if (bep != null) {

				handleReloadElement(ac, bep, dpel);

			}
		}
	}

	private static void handleReloadElement(final Action ac, final BasicElementPanel bep,
			final DataPanelElementLink dpel) {
		if ((ac.getShowInMode() == ShowInMode.PANEL)
				&& (AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement() != null)) {
			AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement().closeWindow();
		}
		if ((ac.getShowInMode() == ShowInMode.MODAL_WINDOW)
				&& (AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement() == null)) {

			ModalWindowInfo mwi = ac.getModalWindowInfo();
			WindowWithDataPanelElement modWind = null;
			if (mwi != null) {

				if ((mwi.getCaption() != null) && (mwi.getWidth() != null)
						&& (mwi.getHeight() != null)) {
					modWind =
						new WindowWithDataPanelElement(mwi.getCaption(), mwi.getWidth(),
								mwi.getHeight(), mwi.getShowCloseBottomButton());
				} else {

					if (mwi.getCaption() != null) {
						modWind =
							new WindowWithDataPanelElement(mwi.getCaption(),
									mwi.getShowCloseBottomButton());
					} else {
						modWind = new WindowWithDataPanelElement(mwi.getShowCloseBottomButton());
					}

				}

			} else {
				modWind = new WindowWithDataPanelElement(false);
			}

			modWind.showModalWindow(bep);

		}
		if (dpel.doHiding()) {
			bep.hidePanel();
			return;
		}
		bep.showPanel();

		boolean keepElementSettings = dpel.getKeepUserSettings();
		bep.setNeedResetLocalContext(!keepElementSettings);

		bep.reDrawPanel(bep.getElementInfo().getContext(ac));
	}

	private static void handleRefreshTab(final Action ac) {
		// Обновить вкладку целиком (активную), а перед этим закрыть
		// модальное окно если оно открыто.
		if ((ac.getShowInMode() == ShowInMode.PANEL)
				&& (AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement() != null)) {
			AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement().closeWindow();
		}

		DataPanelTab dpt =
			AppCurrContext.getInstance().getDataPanelMetaData().getActiveTabForAction(ac);

		Collection<DataPanelElementInfo> tabscoll = dpt.getElements();
		for (DataPanelElementInfo dpe : tabscoll) {

			if (dpe.getHideOnLoad()) {
				BasicElementPanel bep = getElementPanelById(dpe.getId());
				bep.hidePanel();

			}

			if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
				BasicElementPanel bep = getElementPanelById(dpe.getId());

				bep.showPanel();

				boolean keepElementSettings = bep.getElementInfo().getKeepUserSettings(ac);

				bep.setNeedResetLocalContext(!keepElementSettings);
				bep.reDrawPanel(bep.getElementInfo().getContext(ac));
			}
		}
	}

	private static void handleNavigatorBlock(final Action ac) {

		if (ac.getNavigatorElementLink() != null) {

			// }

			// if (ac.getNavigatorActionType() !=
			// NavigatorActionType.DO_NOTHING) {

			boolean fireSelectionAction =
				ac.getNavigatorActionType() == NavigatorActionType.CHANGE_NODE_AND_DO_ACTION;
			Accordeon acrd = AppCurrContext.getInstance().getMainPanel().getAccordeon();

			if ((ac.getDataPanelLink() == null) && (!ac.containsServerActivity())) {
				if (ac.getContext() != null) {

					Accordeon.setTempMainContext(ac.getContext().getMain());
				}
			}
			if (ac.getNavigatorElementLink().getRefresh()) {
				acrd.refreshAccordeon(ac.getNavigatorElementLink().getId(), fireSelectionAction);
			} else {
				acrd.selectNesessaryItemInAccordion(ac.getNavigatorElementLink().getId(),
						fireSelectionAction);
			}

		}
	}

	public static BasicElementPanel getElementPanelById(final ID id) {
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

	public static BasicElementPanel getElementPanelById(final String id) {
		return getElementPanelById(new ID(id));
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
		DataPanelTab dpt =
			AppCurrContext.getInstance().getDataPanelMetaData().getActiveTabForAction(ac);

		for (DataPanelElementInfo dpe : dpt.getElements()) {
			if (!dpe.getNeverShowInPanel()) {
				BasicElementPanel bep = getElementPanelById(dpe.getId());
				if (bep instanceof XFormPanel) {
					bep.setNeedResetLocalContext(false);
					bep.showPanel();
					bep.reDrawPanel(bep.getElementInfo().getContext(ac));

				}
			}
		}
	}

}
