/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.toolbar.ToolBarHelper;
import ru.curs.showcase.app.client.api.BasicElementPanelBasis;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author anlug
 * 
 *         Обработчик выделения закладки в TabPanel.
 * 
 */
public class TabPanelSelectionHandler implements SelectionHandler<Integer> {

	@Override
	public void onSelection(final SelectionEvent<Integer> event) {
		RootPanel.getBodyElement().removeClassName("ready");

		BasicElementPanelBasis.switchOffAllTimers();

		AppCurrContext.getInstance().setWebTextXformTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithToolbarWebtextTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithoutToolbarWebtextTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setChartXformTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithToolbarChartTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithoutToolbarChartTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGeoMapXformTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithToolbarGeoMapTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithoutToolbarGeoMapTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setPluginXformTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithToolbarPluginTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithoutToolbarPluginTrueStateForReadyStateMap(false);
		AppCurrContext.getInstance().setGridWithToolbarGridTrueStateForReadyStateMap(false);

		ToolBarHelper.booleanWithToolBar = false;
		ToolBarHelper.booleanWithToolBar1 = false;
		ToolBarHelper.booleanWithoutToolBar = false;

		DataPanelTab dpt =
			AppCurrContext.getInstance().getUiDataPanel().get(event.getSelectedItem())
					.getDataPanelTabMetaData();

		if (!AppCurrContext.getFromActionElementsMap().isEmpty())
			AppCurrContext.getFromActionElementsMap().clear();

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
