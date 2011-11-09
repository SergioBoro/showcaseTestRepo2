package ru.curs.showcase.app.client;

import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

/**
 * Класс панели с графиком и легендой.
 */
public class ChartPanel extends BasicElementPanelBasis {

	public ChartPanel(final CompositeContext context1, final DataPanelElementInfo element1) {

		this.setContext(context1);
		setElementInfo(element1);
		setIsFirstLoading(true);

		generalChartPanel = new VerticalPanel();
		generalHp = new HorizontalPanel();
		generalChartPanel.add(new HTML(Constants.PLEASE_WAIT_CHART_DATA_ARE_LOADING));

		dataService = GWT.create(DataService.class);

		setChartPanel();

	}

	public ChartPanel(final DataPanelElementInfo element1) {

		// я бы убрал этот код-начало
		setElementInfo(element1);
		generalHp = new HorizontalPanel();
		this.setContext(null);
		setIsFirstLoading(true);
		// я бы убрал этот код-конец

		generalChartPanel = new VerticalPanel();
		generalChartPanel.add(new HTML(Constants.PLEASE_WAIT_CHART_DATA_ARE_LOADING));
	}

	private void setChartPanel() {

		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getChart(getContext(), getElementInfo(), new GWTServiceCallback<Chart>(
				Constants.ERROR_OF_CHART_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final Chart achart) {

				chart = achart;
				if (chart != null) {
					fillChartPanel(achart);
				}
			}
		});

	}

	/**
	 * 
	 * Заполняет виджет графика содержимым.
	 * 
	 * @param achart
	 *            Chart
	 */
	protected void fillChartPanel(final Chart achart) {

		final String divIdGraph = getElementInfo().getId() + Constants.CHART_DIV_ID_SUFFIX;
		final String divIdLegend = getElementInfo().getId() + Constants.CHART_LEGEND_DIV_ID_SUFFIX;

		final String htmlForChart = "<div id='" + divIdGraph + "' class='cursChart'></div>";

		final String htmlForLegend = "<div id='" + divIdLegend + "'></div>";

		footerHTML = new HTML(achart.getFooter());

		headerHTML = new HTML(achart.getHeader());

		chartHTML = new HTML(htmlForChart);

		legendHTML = new HTML(htmlForLegend);
		generalChartPanel.clear();
		generalHp.clear();

		generalChartPanel.add(headerHTML);

		switch (achart.getLegendPosition()) {
		case LEFT:

			generalChartPanel.add(generalHp);
			generalHp.add(legendHTML);
			generalHp.add(chartHTML);
			break;

		case RIGHT:

			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			generalHp.add(legendHTML);
			break;

		case TOP:

			generalChartPanel.add(legendHTML);
			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			break;

		case BOTTOM:

			generalChartPanel.add(generalHp);
			generalHp.add(chartHTML);
			generalChartPanel.add(legendHTML);
			break;

		default:
			break;

		}
		generalChartPanel.add(footerHTML);

		final String paramChart1 = achart.getJsDynamicData();

		final String paramChart2 = achart.getTemplate();

		try {

			drawChart(divIdGraph, divIdLegend, paramChart1, paramChart2);

		} catch (Exception e) {
			if (e.getCause() != null) {
				MessageBox.showMessageWithDetails(Constants.ERROR_OF_CHART_PAINTING,
						e.getMessage(), GeneralException.generateDetailedInfo(e.getCause()),
						GeneralException.getMessageType(e.getCause()),
						GeneralException.needDetailedInfo(e.getCause()));
			} else {
				MessageBox.showSimpleMessage(Constants.ERROR_OF_CHART_PAINTING, e.getMessage());
			}
		}

		checkForDefaultAction();

		if (getElementInfo().getRefreshByTimer()) {
			Timer timer = getTimer();
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer() {

				@Override
				public void run() {
					refreshPanel();
				}

			};
			final int n1000 = 1000;
			timer.schedule(getElementInfo().getRefreshInterval() * n1000);
		}
	}

	/**
	 * VerticalPanel на которой отображен график и легенда.
	 */
	private final VerticalPanel generalChartPanel;

	/**
	 * HorizontalPanel на которой отображен график и легенда.
	 */
	private final HorizontalPanel generalHp;

	/**
	 * DataGrid chart.
	 */
	private Chart chart = null;

	/**
	 * @return Возвращает текущий объект типа Сhart - данные графика.
	 */
	public Chart getChart() {
		return chart;
	}

	/**
	 * Устанавливает текущий объект типа Сhart - данные графика.
	 * 
	 * @param achart
	 *            - объект типа Сhart
	 */
	public void setChart(final Chart achart) {
		this.chart = achart;
	}

	/**
	 * DataServiceAsync.
	 */
	private DataServiceAsync dataService;

	/**
	 * HTML виждет для подписи графика в нижней части.
	 */
	private HTML footerHTML = null;

	/**
	 * HTML виждет для подписи графика в заголовной части.
	 */
	private HTML headerHTML = null;

	/**
	 * HTML виждет для графика.
	 */
	private HTML chartHTML = null;

	/**
	 * HTML виждет для легенды графика.
	 */
	private HTML legendHTML = null;

	/**
	 * Ф-ция, возвращающая панель с графиком и легендой, если она необходима.
	 * 
	 * @return - Панель с графиком и легендой.
	 */
	@Override
	public VerticalPanel getPanel() {
		return generalChartPanel;
	}

	/**
	 * 
	 * Процедура прорисовки графика с помощью библиотеки dojo.
	 * 
	 * @param divIdGraph
	 *            - ID для div графика
	 * @param divIdLegend
	 *            - ID для div легенды графика
	 * @param jsonStr1
	 *            - JSON строка с данными графика
	 * @param jsonStr2
	 *            - JSON строка с настройками графика
	 */
	public native void drawChart(final String divIdGraph, final String divIdLegend,
			final String jsonStr1, final String jsonStr2) /*-{
		$wnd.gwtChartFunc = 
		@ru.curs.showcase.app.client.api.ChartPanelCallbacksEvents::chartPanelClick(Ljava/lang/String;Ljava/lang/String;I);

		$wnd.dojo.require("course.charting");
		$wnd.course.charting.makeChart(divIdGraph, divIdLegend, jsonStr1, jsonStr2, $wnd.convertorFunc);
	}-*/;

	@Override
	public void reDrawPanel(final CompositeContext context1, final Boolean refreshContextOnly) {

		this.setContext(context1);
		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");
		if ((!getIsFirstLoading()) && refreshContextOnly) {
			chart.updateAddContext(context1);
		} else {
			generalChartPanel.clear();
			generalChartPanel.add(new HTML(Constants.PLEASE_WAIT_CHART_DATA_ARE_LOADING));
			if (dataService == null) {
				dataService = GWT.create(DataService.class);
			}

			dataService.getChart(getContext(), getElementInfo(), new GWTServiceCallback<Chart>(
					Constants.ERROR_OF_CHART_DATA_RETRIEVING_FROM_SERVER) {

				@Override
				public void onSuccess(final Chart achart) {

					chart = achart;
					if (chart != null) {
						fillChartPanel(achart);
						getPanel().setHeight("100%");
						if (getIsFirstLoading() && refreshContextOnly) {
							chart.updateAddContext(context1);
						}
						setIsFirstLoading(false);
					}
				}
			});
		}
	}

	@Override
	public void hidePanel() {
		generalChartPanel.setVisible(false);

	}

	@Override
	public void showPanel() {
		generalChartPanel.setVisible(true);

	}

	private void checkForDefaultAction() {
		if (chart.getActionForDependentElements() != null) {
			AppCurrContext.getInstance().setCurrentAction(chart.getActionForDependentElements());
			ActionExecuter.execAction();
		}
	}

	@Override
	public DataPanelElement getElement() {
		return chart;
	}

	@Override
	public void refreshPanel() {

		getPanel().setHeight(String.valueOf(getPanel().getOffsetHeight()) + "px");

		// generalChartPanel.clear();
		// generalChartPanel.add(new
		// HTML(Constants.PLEASE_WAIT_CHART_DATA_ARE_LOADING));
		if (dataService == null) {
			dataService = GWT.create(DataService.class);
		}

		dataService.getChart(getContext(), getElementInfo(), new GWTServiceCallback<Chart>(
				Constants.ERROR_OF_CHART_DATA_RETRIEVING_FROM_SERVER) {

			@Override
			public void onSuccess(final Chart achart) {

				chart = achart;
				if (chart != null) {
					fillChartPanel(achart);
					getPanel().setHeight("100%");
				}
			}
		});

	}
}
