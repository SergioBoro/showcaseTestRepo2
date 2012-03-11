/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.beta2.extra.gwt.ui.panels.CursScrolledTabLayoutPanel;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Генерация (создание) панели данных приложения Showcase (панель
 *         закладок TabPanel).
 * 
 */

public class GeneralDataPanel {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";
	public static final String STYLE = "style";

	/**
	 * HandlerRegistration.
	 */
	private static HandlerRegistration testRegTabPanelSelectionHandler;

	/**
	 * Панель вкладок (закладок) - GeneralDataPanel.
	 */
	private static CursScrolledTabLayoutPanel tabPanel = new CursScrolledTabLayoutPanel(2.0,
			Unit.EM);

	/**
	 * Процедура создания GeneralDataPanel, которая включает в себя вкладки с
	 * данными.
	 * 
	 * @return возвращает виджет GeneralDataPanel типа SimplePanel.
	 */
	public Widget generateDataPanel() {

		final int n87 = 87;
		final int n73 = 73;
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();

				if ((event.getTypeInt() == Event.ONKEYUP) && (nativeEvent.getCtrlKey())
						&& (nativeEvent.getShiftKey()) && (nativeEvent.getAltKey())
						&& ((nativeEvent.getKeyCode() == n87) | (nativeEvent.getKeyCode() == n73))) {

					String url =
						"http://" + Window.Location.getHost() + Window.Location.getPath()
								+ "log/lastLogEvents.jsp";

					Window.open(url, "_blank", "");

				}

			}

		});

		final SimplePanel basicDataVerticalPanel = new SimplePanel();

		basicDataVerticalPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		getTabPanel().setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		basicDataVerticalPanel.add(getTabPanel());

		// tabPanel.setSize("400px", "400px");

		showWelcomPage();
		// generateTestPanels();
		getTabPanel().selectTab(0);

		return basicDataVerticalPanel;
	}

	private void showWelcomPage() {
		final SimplePanel tabVerticalPanel = new SimplePanel();

		ScrollPanel sp = new ScrollPanel();
		sp.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		tabVerticalPanel.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		HTML ht = new HTML();
		ht.setHTML(AppCurrContext.getInstance().getMainPage().getWelcome());
		// ht.setHTML("<iframe width='100%' height='100%' style='border:0px;' src='"
		// + MultiUserData.getPathWithUserData("html/welcome.jsp") + "'/>");
		ht.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		sp.add(ht);
		tabVerticalPanel.add(sp);
		getTabPanel().add(tabVerticalPanel, Constants.WELCOME_TAB_CAPTION);
		JavaScriptFromGWTFeedbackJSNI.setCurrentUserDetailsForViewInHTMLControl("WELCOME");

	}

	/**
	 * 
	 * Процедура перерисовки вкладок в DataPanel при выборе item-са в аккардеоне
	 * (дереве, навигаторе).
	 * 
	 * @param dp
	 *            - панель вкладок DataPanel
	 * 
	 */
	public static void redrowGeneralDataPanelAtnavigatorClick(final DataPanel dp) {
		XFormPanel.destroyXForms(); // Важно !!!!

		if (testRegTabPanelSelectionHandler != null) {
			testRegTabPanelSelectionHandler.removeHandler();
			testRegTabPanelSelectionHandler = null;
		}
		if (AppCurrContext.getInstance().getRegTabPanelSelectionHandler() != null) {
			AppCurrContext.getInstance().getRegTabPanelSelectionHandler().removeHandler();
			AppCurrContext.getInstance().setRegTabPanelSelectionHandler(null);
		}

		if (getTabPanel().getWidgetCount() > 0) {

			// Integer p1 = getTabPanel().getWidgetCount();
			// for (int i = p1 - 1; i < 0; i--) {
			// getTabPanel().remove(i);
			// getTabPanel().getTabWidget(i).removeFromParent();
			// getTabPanel().getTabWidget(i).setVisible(false);
			// }
			getTabPanel().clear();

		}

		AppCurrContext.getInstance().setDataPanelMetaData(dp);

		Collection<DataPanelTab> dptCollection = dp.getTabs();

		AppCurrContext.getInstance().getUiDataPanel().clear();
		for (DataPanelTab dpt : dptCollection) {
			Widget w = generateTab(dpt);
			AppCurrContext.getInstance().getUiDataPanel().add(new UIDataPanelTab(dpt, w));

		}

		Widget tempWidget = new HTML();
		getTabPanel().add(tempWidget);
		getTabPanel().selectTab(tempWidget);

		AppCurrContext.getInstance().setRegTabPanelSelectionHandler(
				getTabPanel().addSelectionHandler(new TabPanelSelectionHandler()));
		selectTab(dp.getActiveTabForAction(AppCurrContext.getInstance().getNavigatorAction()));
		getTabPanel().remove(tempWidget);
	}

	/**
	 * 
	 * Выделяет (открывает) закладку в UI.
	 * 
	 * @param actDpt
	 *            - закладка DataTabPanel для открытия по умолчанию.
	 */
	public static void selectTab(final DataPanelTab actDpt) {
		for (int i = 0; i < getTabPanel().getWidgetCount(); i++) {
			if (actDpt.getId().equals(
					AppCurrContext.getInstance().getUiDataPanel().get(i).getDataPanelTabMetaData()
							.getId())) {
				getTabPanel().selectTab(i, true);
				break;
			}
		}
	}

	/**
	 * 
	 * Генерирует с нуля.
	 * 
	 * @param dpt
	 *            - DataTabPanel - класс вкладки
	 * 
	 * @return - возвращает виджет вкладки в GeneralDataPanel
	 */
	public static Widget generateTab(final DataPanelTab dpt) {

		SimplePanel vp = new SimplePanel();
		getTabPanel().add(vp, dpt.getName());
		return vp;
	}

	public static Widget generateElement(final DataPanelElementInfo dpe) {
		switch (dpe.getType()) {
		case WEBTEXT:
			return generateWebTextElement(dpe);

		case XFORMS:
			return generateXFormsElement(dpe);

		case GRID:
			return generateGridElement(dpe);

		case CHART:
			return generateChartElement(dpe);

		case GEOMAP:
			return generateMapElement(dpe);

		case PLUGIN:
			return generatePluginElement(dpe);

		default:
			return null;

		}
	}

	public static void fillTabWithVerticalLayout(final DataPanelTab dpt, final VerticalPanel vp1) {

		// Вертикальное размещение элементов
		Collection<DataPanelElementInfo> tabscoll = dpt.getElements();
		for (DataPanelElementInfo dpe : tabscoll) {
			Widget el = null;
			if (dpe.getCacheData()) {
				el =
					AppCurrContext.getInstance().getMapOfDataPanelElements()
							.get(dpe.getKeyForCaching(getElementContextForNavigatorAction(dpe)));

			}

			if (el == null) {
				el = generateElement(dpe);
			}
			if (el != null) {
				el.addStyleName("dataPanelElement-BorderCorners");

				if (dpe.getHtmlAttrs().getStyleClass() != null) {
					el.addStyleName(dpe.getHtmlAttrs().getStyleClass());
				}

				el.setWidth(SIZE_ONE_HUNDRED_PERCENTS);
				DOM.setElementAttribute(el.getElement(), "id", dpe.getFullId());

				if (!(dpe.getNeverShowInPanel())) {

					vp1.add(el);

					if (dpe.getCacheData()) {
						AppCurrContext
								.getInstance()
								.getMapOfDataPanelElements()
								.put(dpe.getKeyForCaching(getElementContextForNavigatorAction(dpe)),
										el);
					}

				}
			}
		}

	}

	public static void fillTabWithTableLayout(final DataPanelTab dpt, final VerticalPanel vp1) {
		// Табличное размещение элементов

		FlexTable ft = new FlexTable();
		vp1.add(ft);

		ft.getElement().setAttribute(STYLE, dpt.getHtmlAttrs().getStyle());
		ft.setStylePrimaryName(dpt.getHtmlAttrs().getStyleClass());
		// ft.setSize("100%", "100%");

		Collection<DataPanelTR> trColl = dpt.getTrs();
		// Integer layoutRowCount = trColl.size();

		Integer currentRowCount = -1;
		for (DataPanelTR tr : trColl) {
			currentRowCount++;

			Integer currentColoumnCount = -1;
			Collection<DataPanelTD> tdColl = tr.getTds();

			for (DataPanelTD td : tdColl) {

				currentColoumnCount++;

				DataPanelElementInfo dpe = td.getElement();

				Widget el = null;
				// if (dpe.getCacheData()) {
				// el =
				// AppCurrContext
				// .getInstance()
				// .getMapOfDataPanelElements()
				// .get(dpe.getKeyForCaching(getElementContextForNavigatorAction(dpe)));

				// }

				// if (el == null) {
				el = generateElement(dpe);

				if (el != null) {

					ft.setWidget(currentRowCount, currentColoumnCount, el);
					el.addStyleName("dataPanelElement-BorderCorners");

					if (dpe.getHtmlAttrs().getStyleClass() != null) {
						el.addStyleName(dpe.getHtmlAttrs().getStyleClass());
					}

					el.setWidth(SIZE_ONE_HUNDRED_PERCENTS);
					DOM.setElementAttribute(el.getElement(), "id", dpe.getFullId());

				}

				if (td.getColspan() != null) {
					ft.getFlexCellFormatter().setColSpan(currentRowCount, currentColoumnCount,
							td.getColspan());
				}

				if (td.getRowspan() != null) {
					ft.getFlexCellFormatter().setRowSpan(currentRowCount, currentColoumnCount,
							td.getRowspan());
				}

				ft.getFlexCellFormatter().getElement(currentRowCount, currentColoumnCount)
						.setAttribute(STYLE, td.getHtmlAttrs().getStyle());
				ft.getFlexCellFormatter().getElement(currentRowCount, currentColoumnCount)
						.setAttribute("width", td.getWidth());
				ft.getFlexCellFormatter().setStylePrimaryName(currentRowCount,
						currentColoumnCount, td.getHtmlAttrs().getStyleClass());

				// if (!(dpe.getNeverShowInPanel())) {

				// vp1.add(el);

				// if (dpe.getCacheData()) {
				// AppCurrContext
				// .getInstance()
				// .getMapOfDataPanelElements()
				// .put(dpe.getKeyForCaching(getElementContextForNavigatorAction(dpe)),
				// el);
				// }

				// }

			}

			ft.getRowFormatter().getElement(currentRowCount)
					.setAttribute(STYLE, tr.getHtmlAttrs().getStyle());
			ft.getRowFormatter().getElement(currentRowCount)
					.setAttribute("height", tr.getHeight());
			ft.getRowFormatter().setStylePrimaryName(currentRowCount,
					tr.getHtmlAttrs().getStyleClass());

		}
	}

	/**
	 * Заполняет вкладку соответствующим контентом из виджетов.
	 * 
	 * @param tabIndex
	 *            - индекс (порядковый номер с нуля) вкладки.
	 */
	public static void fillTabContent(final int tabIndex) {

		XFormPanel.destroyXForms(); // Важно !!!!
		// очистка текущей вкладки полностью
		SimplePanel vp = (SimplePanel) getTabPanel().getWidget(tabIndex);
		vp.clear();

		ScrollPanel sp = new ScrollPanel();

		vp.add(sp);
		sp.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		VerticalPanel vp1 = new VerticalPanel();
		vp1.setSpacing(2);
		vp1.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		sp.add(vp1);

		AppCurrContext.getInstance().getUiDataPanel().get(tabIndex).getUiElements().clear();

		DataPanelTab dpt =
			AppCurrContext.getInstance().getUiDataPanel().get(tabIndex).getDataPanelTabMetaData();

		if (dpt.getLayout().equals(DataPanelTabLayout.VERTICAL)) {
			fillTabWithVerticalLayout(dpt, vp1);
		} else {
			fillTabWithTableLayout(dpt, vp1);

		}
	}

	private static Widget generateChartElement(final DataPanelElementInfo dpe) {

		ChartPanel chp = null;
		Widget w = null;
		if ((!(dpe.getHideOnLoad())) && (!(dpe.getNeverShowInPanel()))) {

			chp = new ChartPanel(getElementContextForNavigatorAction(dpe), dpe);

			w = chp.getPanel();
		} else {
			// в случае когда у данного элемента есть главный элемент
			chp = new ChartPanel(dpe);
			w = chp.getPanel();
			chp.hidePanel();

		}

		getUiElements(dpe).add(new UIDataPanelElement(chp));
		return w;

	}

	/**
	 * 
	 * создает и возвращает виджет Verticalpanel для элементов типа Grid (на
	 * виджете VerticalPanel лежит компонента Grid).
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateGridElement(final DataPanelElementInfo dpe) {
		Widget w = null;

		DataPanelElementSubType subtype = dpe.getSubType();
		if (subtype == null) {
			subtype = DataPanelElementSubType.PAGING_GRID;
		}
		switch (subtype) {
		case EXT_LIVE_GRID:
		case EXT_PAGING_GRID:
			ExtGridPanel edgp = null;

			if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
				edgp = new ExtGridPanel(getElementContextForNavigatorAction(dpe), dpe, null);
				w = edgp.getPanel();
				w.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
			} else {
				edgp = new ExtGridPanel(dpe);
				w = edgp.getPanel();
				edgp.hidePanel();
			}

			getUiElements(dpe).add(new UIDataPanelElement(edgp));

			break;

		case EXT_TREE_GRID:
			MessageBox.showSimpleMessage("generateGridElement", "TreeGrid из ExtGWT");
			break;

		case PAGING_GRID: // существующий грид
		default:
			GridPanel dgp = null;

			if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
				dgp = new GridPanel(getElementContextForNavigatorAction(dpe), dpe, null);
				w = dgp.getPanel();
				w.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
			} else {
				dgp = new GridPanel(dpe);
				w = dgp.getPanel();
				dgp.hidePanel();
			}

			getUiElements(dpe).add(new UIDataPanelElement(dgp));

			break;
		}

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет HTML для элементов типа WebText.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateWebTextElement(final DataPanelElementInfo dpe) {

		WebTextPanel wtp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {
			wtp = new WebTextPanel(getElementContextForNavigatorAction(dpe), dpe);
			w = wtp.getPanel();
			w.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		} else {
			wtp = new WebTextPanel(dpe);
			w = wtp.getPanel();
			wtp.hidePanel();
		}

		getUiElements(dpe).add(new UIDataPanelElement(wtp));

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет для элементов типа XForm.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateXFormsElement(final DataPanelElementInfo dpe) {

		XFormPanel wtp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			wtp = new XFormPanel(getElementContextForNavigatorAction(dpe), dpe, null);
			w = wtp.getPanel();
			w.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		} else {
			wtp = new XFormPanel(dpe);
			w = wtp.getPanel();
			wtp.hidePanel();
		}

		getUiElements(dpe).add(new UIDataPanelElement(wtp));

		return w;
	}

	/**
	 * 
	 * создает и возвращает виджет для элемента типа Plugin.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generatePluginElement(final DataPanelElementInfo dpe) {

		BasicElementPanelBasis mp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			mp = new PluginPanel(getElementContextForNavigatorAction(dpe), dpe);

			w = mp.getPanel();
		} else {
			// в случае когда у данного элемента есть главный элемент
			mp = new PluginPanel(dpe);
			w = mp.getPanel();
			mp.hidePanel();

		}

		getUiElements(dpe).add(new UIDataPanelElement(mp));
		return w;

	}

	/**
	 * 
	 * создает и возвращает виджет для элемента типа Map.
	 * 
	 * @param dpe
	 *            - DataPanelElement
	 * 
	 * @return - Widget
	 */
	public static Widget generateMapElement(final DataPanelElementInfo dpe) {

		BasicElementPanelBasis mp = null;
		Widget w = null;
		if (!(dpe.getHideOnLoad()) && (!(dpe.getNeverShowInPanel()))) {

			mp = new GeoMapPanel(getElementContextForNavigatorAction(dpe), dpe);

			w = mp.getPanel();
		} else {
			// в случае когда у данного элемента есть главный элемент
			mp = new GeoMapPanel(dpe);
			w = mp.getPanel();
			mp.hidePanel();

		}

		getUiElements(dpe).add(new UIDataPanelElement(mp));
		return w;

	}

	private static List<UIDataPanelElement> getUiElements(final DataPanelElementInfo dpe) {
		return AppCurrContext.getInstance().getUiDataPanel().get(dpe.getTab().getPosition())
				.getUiElements();
	}

	private static CompositeContext getElementContextForNavigatorAction(
			final DataPanelElementInfo dpe) {
		return dpe.getContext(AppCurrContext.getInstance().getNavigatorAction());
	}

	/**
	 * @param atabPanel
	 *            the tabPanel to set
	 */
	public static void setTabPanel(final CursScrolledTabLayoutPanel atabPanel) {
		GeneralDataPanel.tabPanel = atabPanel;
	}

	/**
	 * @return the tabPanel
	 */
	public static CursScrolledTabLayoutPanel getTabPanel() {
		return tabPanel;
	}

}
