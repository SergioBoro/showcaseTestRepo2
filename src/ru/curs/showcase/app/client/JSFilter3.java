package ru.curs.showcase.app.client;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Класс задания фильтра.
 */
public class JSFilter3 extends DialogBoxWithCaptionButton {

	private final VerticalPanel p = new VerticalPanel();
	private final FlowPanel hpFooter = new FlowPanel();

	private final StackLayoutPanel accordeon = new StackLayoutPanel(Unit.PX);

	private final ScrollPanel sp = new ScrollPanel();

	private int conditionCount = 0;

	JSFilter3(final String data) {
		setText("Фильтр");

		p.setSize("100%", "100%");
		// sp.setSize("450px", "400px");

		accordeon.setSize("450px", "400px");

		// accordeon.setSize("100%", "100%");
		hpFooter.setSize("100%", "100%");

		final int headerSize = 2;
		accordeon.add(createFiltersItem(), new HTML("OR (\"Регион\" содержит 'обл')"), headerSize);
		accordeon
				.add(createFiltersItem(), new HTML("OR (\"Регион\" содержит 'моск')"), headerSize);
		accordeon.add(createFiltersItem(), new HTML("AND (10 <= \"4кв. 2005г.\" <= 700)"),
				headerSize);

		// p.add(accordeon);

		// p.add(sp);

		sp.setAlwaysShowScrollBars(true);

		sp.add(accordeon);
		p.add(sp);

		Button btnAdd = new Button("Добавить условие", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				conditionCount++;

				// accordeon.add(new HTML("Условие " +
				// String.valueOf(conditionCount)), new HTML(
				// "Условие " + String.valueOf(conditionCount)), headerSize);

				accordeon.add(createFiltersItem(),
						new HTML("Условие " + String.valueOf(conditionCount)), headerSize);

				accordeon.showWidget(accordeon.getWidgetCount() - 1);

			}
		});
		hpFooter.add(btnAdd);

		Button btnDel = new Button("Удалить условие", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {

				accordeon.remove(accordeon.getVisibleIndex());
			}
		});
		hpFooter.add(btnDel);

		Button btnCancel = new Button("Отмена", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		btnCancel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnCancel);

		Button btnClear = new Button("Очистить", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				accordeon.clear();
			}
		});
		btnClear.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnClear);

		Button btnFilter = new Button("Фильтр", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		btnFilter.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnFilter);

		p.add(hpFooter);

		setWidget(p);

	}

	private Widget createFiltersItem() {
		VerticalPanel filtersPanel = new VerticalPanel();
		// filtersPanel.setSize("400px", "300px");
		// filtersPanel.setSize("100%", "100%");
		final int spacing = 4;
		filtersPanel.setSpacing(spacing);

		final ListBox operatorBox = new ListBox(false);
		operatorBox.addItem("OR");
		operatorBox.addItem("AND");
		filtersPanel.add(new HTML("Соответствие:"));
		filtersPanel.add(operatorBox);

		final ListBox columnBox = new ListBox(false);
		columnBox.addItem("Регион");
		columnBox.addItem("Картинка");
		filtersPanel.add(new HTML("Столбец:"));
		filtersPanel.add(columnBox);

		final ListBox conditionBox = new ListBox(false);
		conditionBox.addItem("содержит");
		conditionBox.addItem("равно");
		conditionBox.addItem("начинается с");
		conditionBox.addItem("заканчивается на");
		filtersPanel.add(new HTML("Условие:"));
		filtersPanel.add(conditionBox);

		final TextBox valueBox = new TextBox();
		filtersPanel.add(new HTML("Значение:"));
		filtersPanel.add(valueBox);

		return new SimplePanel(filtersPanel);
	}
}
