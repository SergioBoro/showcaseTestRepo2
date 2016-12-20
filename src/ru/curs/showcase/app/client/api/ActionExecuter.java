/**
 * 
 */
package ru.curs.showcase.app.client.api;

import java.util.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.element.VoidElement;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.*;
import ru.curs.showcase.app.client.internationalization.CourseClientLocalization;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

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

			setEnableDisableState(ac.getActionCaller(), false);

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

			dataService.execServerAction(
					ac,
					new GWTServiceCallback<VoidElement>(
					// AppCurrContext.getInstance().getBundleMap().get("error_in_server_activity"))
					// {
							CourseClientLocalization.gettext(AppCurrContext.getInstance()
									.getDomain(), "when execution of server action")) {

						@Override
						public void onSuccess(final VoidElement ve) {

							super.onSuccess(ve);

							handleClientBlocks(ac);

							setEnableDisableState(ac.getActionCaller(), true);

						}

					});
		} else {
			handleClientBlocks(ac);
		}
	}

	private static void setEnableDisableState(final Object actionCaller, final boolean state) {
		if (actionCaller == null) {
			return;
		}

		if (state) {
			Timer enabledTimer = new Timer() {
				@Override
				public void run() {
					if (actionCaller instanceof MenuItem) {
						((MenuItem) actionCaller).setEnabled(state);
					}
					if (actionCaller instanceof TextButton) {
						((TextButton) actionCaller).setEnabled(state);
					}
				}
			};
			final int delay = 3000;
			enabledTimer.schedule(delay);
		} else {
			if (actionCaller instanceof MenuItem) {
				((MenuItem) actionCaller).setEnabled(state);
			}
			if (actionCaller instanceof TextButton) {
				((TextButton) actionCaller).setEnabled(state);
			}
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

			if ((ac.getShowInMode() == ShowInMode.PANEL)
					&& (AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement() != null)) {
				AppCurrContext.getInstance().getCurrentOpenWindowWithDataPanelElement()
						.closeWindow();
			}
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

				if (mwi.getCaption() != null) {
					modWind =
						new WindowWithDataPanelElement(mwi.getCaption(), mwi.getWidth(),
								mwi.getHeight(), mwi.getShowCloseBottomButton(),
								mwi.getCloseOnEsc());
				} else {

					if (mwi.getCaption() != null) {

						modWind =
							new WindowWithDataPanelElement(mwi.getCaption(),
									mwi.getShowCloseBottomButton(), mwi.getCloseOnEsc());

					} else {
						modWind =
							new WindowWithDataPanelElement(mwi.getShowCloseBottomButton(),
									mwi.getCloseOnEsc());
					}

				}

			} else {
				modWind = new WindowWithDataPanelElement(false, true);
			}

			// modWind.addCloseHandler(new CloseHandler<PopupPanel>() {
			// @Override
			// public void onClose(CloseEvent<PopupPanel> arg0) {
			// MessageBox.showSimpleMessage("Message",
			// "Are you sure? Data could be lost!");
			// }
			// });

			modWind.showModalWindow(bep);

		}

		if (dpel.getPreserveHidden() && !bep.getPanel().isVisible()) {
			return;
		}
		if (dpel.doHiding()) {
			bep.hidePanel();
			return;
		}
		bep.showPanel();

		boolean keepElementSettings = dpel.getKeepUserSettings();
		bep.setNeedResetLocalContext(!keepElementSettings);
		bep.setPartialUpdate(dpel.getPartialUpdate());
		bep.setCurrentLevelUpdate(dpel.getCurrentLevelUpdate());
		bep.setChildLevelUpdate(dpel.getChildLevelUpdate());
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

				if (bep != null) {
					bep.showPanel();

					boolean keepElementSettings = bep.getElementInfo().getKeepUserSettings(ac);
					bep.setNeedResetLocalContext(!keepElementSettings);
					bep.setPartialUpdate(bep.getElementInfo().getPartialUpdate(ac));
					bep.setCurrentLevelUpdate(bep.getElementInfo().getCurrentLevelUpdate(ac));
					bep.setChildLevelUpdate(bep.getElementInfo().getChildLevelUpdate(ac));
					bep.reDrawPanel(bep.getElementInfo().getContext(ac));
				}
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

}
