/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.client.api.BasicElementPanelBasis;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.DOM;

/**
 * @author anlug
 * 
 *         Обработчик выделения закладки в TabPanel.
 * 
 */
public class TabPanelSelectionHandler implements SelectionHandler<Integer> {

	@Override
	public void onSelection(final SelectionEvent<Integer> event) {
		DOM.getElementById("showcaseReady").setAttribute("isReady", "false");

		BasicElementPanelBasis.switchOffAllTimers();

		DataPanelTab dpt =
			AppCurrContext.getInstance().getUiDataPanel().get(event.getSelectedItem())
					.getDataPanelTabMetaData();

		if (!AppCurrContext.getReadyStateMap().isEmpty())
			AppCurrContext.getReadyStateMap().clear();

		if (dpt.getLayout() == DataPanelTabLayout.VERTICAL) {
			for (DataPanelElementInfo dpei : dpt.getElements()) {
				if (!dpei.getNeverShowInPanel() && !dpei.getHideOnLoad())
					AppCurrContext.getReadyStateMap().put(dpei, false);
			}
		} else {
			for (DataPanelTR dptr : dpt.getTrs()) {
				for (DataPanelTD dptd : dptr.getTds()) {
					if (!dptd.getElement().getNeverShowInPanel()
							&& !dptd.getElement().getHideOnLoad())
						AppCurrContext.getReadyStateMap().put(dptd.getElement(), false);
				}
			}
		}

		AppCurrContext.getInstance().setNavigatorActionFromTab(
				AppCurrContext.getInstance().getUiDataPanel().get(event.getSelectedItem())
						.getDataPanelTabMetaData().getAction());
		GeneralDataPanel.fillTabContent(event.getSelectedItem());
		GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();

		AppCurrContext.getInstance().setDatapanelTabIndex(event.getSelectedItem());
		String str = AppCurrContext.getInstance().getNavigatorItemId();
		if (event.getSelectedItem() != Integer.parseInt(getState())) {
			pushState(str + ";" + event.getSelectedItem());
		}

		// MessageBox.showSimpleMessage("tab handler",
		// GeneralDataPanel.getTabPanel().getTabBar()
		// .getElement().getStyle().getLeft());

	}

	public native void pushState(String obj) /*-{

		$wnd.history.pushState(obj, "Tab");

	}-*/;

	public native String getState() /*-{

		var s = "" + $wnd.history.state;
		var ar = s.split(";");
		var ret = ar[1];
		return ret;

	}-*/;

}
