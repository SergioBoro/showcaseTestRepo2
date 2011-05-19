/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.*;
import ru.curs.showcase.app.client.api.Constants;
import ru.curs.showcase.app.client.utils.DownloadHelper;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса средней (главной,
 *         рабочей панели приложения - MainPanel) части приложения Showcase.
 * 
 */
public class MainPanel {

	/**
	 * Процедура создания MainPanel, которая включает в себя Accordeon и
	 * GeneralDataPanel.
	 * 
	 * @return возвращает заполненный виджет MainPanel типа VerticalPanel.
	 */
	public Widget generateMainPanel() {

		final VerticalPanel basicVerticalPanel = new VerticalPanel();
		basicVerticalPanel.add(new DownloadHelper());

		final CursSplitLayoutPanel p = new CursSplitLayoutPanel();

		final int n35 = 35;
		final int n85 = 85;
		p.setPixelSize(Window.getClientWidth() - n35, Window.getClientHeight() - n85);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				int height = event.getHeight() - n85;
				int width = event.getWidth() - n35;
				p.setHeight(height + "px");
				p.setWidth(width + "px");
			}
		});

		DecoratorPanel decoratorPanel = new DecoratorPanel();
		decoratorPanel.setWidget(p);
		basicVerticalPanel.add(decoratorPanel);

		basicVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		basicVerticalPanel.setSpacing(Constants.SPACINGN);

		Widget accordeon = (new Accordeon()).generateAccordeon();
		p.addWest(accordeon, Constants.SPLITLAYOUTPANELSIZEN);
		// accordeon.p.setWidgetMinSize(accordeon, splitMinWidgetSizeN);
		Widget gp = (new GeneralDataPanel()).generateDataPanel();
		p.add(gp);
		p.setWidgetMinSize(gp, 1);

		p.setSplitterDragHandler(new SplitterDragHandler() {

			@Override
			public void splitterDragEvent() {
				// GeneralDataPanel.getTabPanel().saveTabBarCurrentScrollingPosition();
				GeneralDataPanel.getTabPanel().checkIfScrollButtonsNecessary();

			}

		});

		return basicVerticalPanel;
	}
}
