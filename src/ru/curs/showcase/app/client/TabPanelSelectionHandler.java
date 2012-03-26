/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.BasicElementPanelBasis;

import com.google.gwt.event.logical.shared.*;

/**
 * @author anlug
 * 
 *         Обработчик выделения закладки в TabPanel.
 * 
 */
public class TabPanelSelectionHandler implements SelectionHandler<Integer> {

	@Override
	public void onSelection(final SelectionEvent<Integer> event) {

		BasicElementPanelBasis.switchOffAllTimers();

		AppCurrContext.getInstance().setNavigatorActionFromTab(
				AppCurrContext.getInstance().getUiDataPanel().get(event.getSelectedItem())
						.getDataPanelTabMetaData().getAction());
		GeneralDataPanel.fillTabContent(event.getSelectedItem());
		GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();
		// MessageBox.showSimpleMessage("tab handler",
		// GeneralDataPanel.getTabPanel().getTabBar()
		// .getElement().getStyle().getLeft());

	}

}
