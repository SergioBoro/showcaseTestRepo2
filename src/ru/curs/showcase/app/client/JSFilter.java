package ru.curs.showcase.app.client;

import java.util.Date;

import ru.beta2.extra.gwt.ui.panels.DialogBoxWithCaptionButton;
import ru.curs.gwt.datagrid.model.GridValueType;
import ru.curs.showcase.app.api.grid.*;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;
import com.google.gwt.view.client.*;

/**
 * Класс задания фильтра.
 */
public class JSFilter extends DialogBoxWithCaptionButton {

	private final VerticalPanel vpMain = new VerticalPanel();
	private final SplitLayoutPanel slpMain = new SplitLayoutPanel();
	private ScrollPanel sp;
	private final VerticalPanel vpEdit = new VerticalPanel();
	private final FlowPanel hpFooter = new FlowPanel();

	private final ProvidesKey<Filter> providesKey = new ProvidesKey<Filter>() {
		@Override
		public Object getKey(final Filter filter) {
			return filter.getId();
		}
	};
	private CellList<Filter> celllist;
	private SingleSelectionModel<Filter> selectionModel;
	private ListDataProvider<Filter> listDataProvider;

	private final ListBox linkBox = new ListBox(false);
	private final ListBox columnBox = new ListBox(false);
	private final ListBox conditionBox = new ListBox(false);
	private final TextBox valueBox = new TextBox();
	private DateBox dateBox = null;
	private final DatePicker datePicker = new DatePicker();
	private Button btnUpdate = null;

	private int maxId = 0;
	private final JSLiveGridPluginPanel jsLiveGridPluginPanel;

	// conditionEqual: равно
	// conditionNotEqual: не равно
	// conditionLess: меньше чем
	// conditionLessEqual: меньше или равно
	// conditionGreater: больше чем
	// conditionGreaterEqual: больше или равно
	// conditionContain: содержит
	// conditionStartWith: начинается с
	// conditionEndWith: заканчивается на
	// conditionNotContain: не содержит
	// conditionNotStartWith: не начинается с
	// conditionNotEndWith: не оканчивается на
	// conditionIsEmpty: пусто
	private static final String[] STRING_CONDITIONS = {
			AppCurrContext.getInstance().getInternationalizedMessages().conditionContain(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionStartWith(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionEndWith(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionNotContain(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionNotStartWith(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEndWith(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionIsEmpty() };
	private static final String[] DATE_NUMBER_CONDITIONS = {
			AppCurrContext.getInstance().getInternationalizedMessages().conditionEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionGreater(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionLess(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionGreaterEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionLessEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionNotEqual(),
			AppCurrContext.getInstance().getInternationalizedMessages().conditionIsEmpty() };

	/**
	 * Класс ячейки условия фильтра в списке.
	 */
	private final class FilterCell extends AbstractCell<Filter> {
		@Override
		public void render(final com.google.gwt.cell.client.Cell.Context context,
				final Filter filter, final SafeHtmlBuilder sb) {
			renderFilterCell(context, filter, sb);
		}
	}

	public JSFilter(final JSLiveGridPluginPanel aJSLiveGridPluginPanel) {
		setText(AppCurrContext.getInstance().getInternationalizedMessages().grid_caption_filter());

		jsLiveGridPluginPanel = aJSLiveGridPluginPanel;

		fillCelllist();
		fillVPEdit();

		vpMain.setSize("100%", "100%");
		slpMain.setSize("600px", "550px");
		// Эксперименты для Opera
		// vpEdit.setHeight("550px");
		// vpEdit.setHeight("100%");
		// vpEdit.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		sp.setHeight("100%");
		final int size = 300;
		slpMain.addWest(sp, size);
		slpMain.add(vpEdit);
		hpFooter.setSize("100%", "100%");

		vpMain.add(slpMain);
		fillHPFooter();
		vpMain.add(hpFooter);
		setWidget(vpMain);

		center();

		initForm();

	}

	private void fillCelllist() {

		celllist = new CellList<Filter>(new FilterCell(), providesKey);
		celllist.setSize("100%", "100%");
		celllist.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);

		selectionModel = new SingleSelectionModel<Filter>(providesKey);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(final SelectionChangeEvent event) {
				setEnableDisableVPEdit();
				setVPEdit(selectionModel.getSelectedObject());
			}
		});

		celllist.setSelectionModel(selectionModel);

		listDataProvider = new ListDataProvider<Filter>(providesKey);
		listDataProvider.addDataDisplay(celllist);

		sp = new ScrollPanel(celllist);

	}

	private void fillVPEdit() {
		final int spacing = 4;
		vpEdit.setSpacing(spacing);

		linkBox.addItem("OR");
		linkBox.addItem("AND");
		vpEdit.add(new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
				.jsFilterLink()
				+ ":"));
		vpEdit.add(linkBox);

		columnBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				fillListBoxConditions();
			}
		});
		for (final LiveGridColumnConfig egcc : ((LiveGridMetadata) jsLiveGridPluginPanel
				.getElement()).getColumns()) {
			columnBox.addItem(egcc.getCaption());
		}
		vpEdit.add(new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
				.jsFilterColumn()
				+ ":"));
		vpEdit.add(columnBox);

		vpEdit.add(new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
				.jsFilterCondition()
				+ ":"));
		vpEdit.add(conditionBox);

		vpEdit.add(new HTML(AppCurrContext.getInstance().getInternationalizedMessages()
				.jsFilterValue()
				+ ":"));
		vpEdit.add(valueBox);

		dateBox =
			new DateBox(datePicker, new Date(), new DateBox.DefaultFormat(
					DateTimeFormat.getFormat("dd.MM.yyyy HH:mm")));
		vpEdit.add(dateBox);
		vpEdit.add(datePicker);

		HTML html = new HTML("");
		html.setHeight("10px");
		vpEdit.add(html);

		btnUpdate =
			new Button(AppCurrContext.getInstance().getInternationalizedMessages()
					.jsFilterUpdate(), new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					updateFilter();
				}
			});
		vpEdit.add(btnUpdate);

		conditionBox.setWidth("197px");
		valueBox.setWidth("190px");
		dateBox.setWidth("195px");
		datePicker.setWidth("200px");
		btnUpdate.setWidth("250px");

		setVisibleValueBoxes(true);

	}

	private void setEnableDisableVPEdit() {

		boolean enabled =
			(selectionModel.getSelectedObject() != null)
					&& (celllist.getVisibleItems().indexOf(selectionModel.getSelectedObject()) > -1);

		if (enabled && linkBox.isEnabled()) {
			return;
		}

		linkBox.setSelectedIndex(-1);
		columnBox.setSelectedIndex(-1);
		conditionBox.setSelectedIndex(-1);
		conditionBox.clear();
		valueBox.setText("");

		if (!enabled) {
			setVisibleValueBoxes(true);
		}

		linkBox.setEnabled(enabled);
		columnBox.setEnabled(enabled);
		conditionBox.setEnabled(enabled);
		valueBox.setEnabled(enabled);
		dateBox.setEnabled(enabled);
		btnUpdate.setEnabled(enabled);

	}

	private void fillHPFooter() {
		Button btnAdd =
			new Button(AppCurrContext.getInstance().getInternationalizedMessages().jsFilterAdd(),
					new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							Filter filter = new Filter();
							maxId++;
							filter.setId(String.valueOf(maxId));
							listDataProvider.getList().add(filter);
							adjustCelllistRowCount();
						}
					});
		btnAdd.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnAdd);

		HTML html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(html);

		Button btnDel =
			new Button(AppCurrContext.getInstance().getInternationalizedMessages().jsFilterDel(),
					new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							listDataProvider.getList().remove(selectionModel.getSelectedObject());
							adjustCelllistRowCount();
							setEnableDisableVPEdit();
						}
					});
		btnDel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnDel);

		html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(html);

		Button btnClear =
			new Button(
					AppCurrContext.getInstance().getInternationalizedMessages().jsFilterClear(),
					new ClickHandler() {
						@Override
						public void onClick(final ClickEvent event) {
							maxId = 0;
							listDataProvider.getList().clear();
							adjustCelllistRowCount();
							setEnableDisableVPEdit();
						}
					});
		btnClear.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		hpFooter.add(btnClear);

		Button btnCancel =
			new Button(AppCurrContext.getInstance().getInternationalizedMessages()
					.jsFilterCancel(), new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					hide();
				}
			});
		btnCancel.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnCancel);

		html = new HTML("&nbsp;");
		html.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(html);

		Button btnOK = new Button("OK", new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();

				jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().setMaxId(maxId);
				jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getFilters().clear();
				for (final Filter filter : listDataProvider.getList()) {
					jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getFilters()
							.add(new Filter(filter));
				}

				jsLiveGridPluginPanel.refreshPanel();

			}
		});
		btnOK.setWidth("80px");
		btnOK.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
		hpFooter.add(btnOK);

	}

	private void initForm() {
		maxId = jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo().getMaxId();
		listDataProvider.getList().clear();
		for (final Filter filter : jsLiveGridPluginPanel.getLocalContext().getGridFilterInfo()
				.getFilters()) {
			listDataProvider.getList().add(new Filter(filter));
		}
		adjustCelllistRowCount();

		setEnableDisableVPEdit();

	}

	private void adjustCelllistRowCount() {
		int count = listDataProvider.getList().size();
		celllist.setRowCount(count, true);
		celllist.setPageSize(count);
		celllist.setVisibleRange(0, count);
	}

	private void setListBoxSelectedIndex(final ListBox lst, final String str) {
		int index = -1;
		for (int i = 0; i < lst.getItemCount(); i++) {
			if (lst.getItemText(i).equals(str)) {
				index = i;
				break;
			}
		}
		lst.setSelectedIndex(index);
	}

	private void setVisibleValueBoxes(final boolean b) {
		valueBox.setVisible(b);
		dateBox.setVisible(!b);
		datePicker.setVisible(!b);
	}

	private void fillListBoxConditions() {
		conditionBox.clear();
		if (columnBox.getSelectedIndex() > -1) {
			GridValueType vt =
				((LiveGridMetadata) jsLiveGridPluginPanel.getElement()).getColumns()
						.get(columnBox.getSelectedIndex()).getValueType();

			if (vt.isGeneralizedString()) {
				setListBoxConditions(STRING_CONDITIONS);
			} else {
				setListBoxConditions(DATE_NUMBER_CONDITIONS);
			}
			conditionBox.setSelectedIndex(-1);

			if (vt.isDate()) {
				setVisibleValueBoxes(false);
			} else {
				setVisibleValueBoxes(true);
			}
		} else {
			setVisibleValueBoxes(true);
		}
	}

	private void setListBoxConditions(final String[] conditions) {
		for (int i = 0; i < conditions.length; i++) {
			conditionBox.addItem(conditions[i]);
		}
	}

	private void setVPEdit(final Filter filter) {

		setListBoxSelectedIndex(linkBox, filter.getLink());

		setListBoxSelectedIndex(columnBox, filter.getColumn());

		fillListBoxConditions();
		setListBoxSelectedIndex(conditionBox, filter.getCondition());

		if (valueBox.isVisible()) {
			valueBox.setText(filter.getValue());
		} else {
			dateBox.setValue(filter.getDateValue());
		}

	}

	private void updateFilter() {
		Filter filter = selectionModel.getSelectedObject();
		filter.setLink(linkBox.getItemText(linkBox.getSelectedIndex()));
		filter.setColumn(columnBox.getItemText(columnBox.getSelectedIndex()));
		filter.setCondition(conditionBox.getItemText(conditionBox.getSelectedIndex()));
		if (valueBox.isVisible()) {
			filter.setValue(valueBox.getText());
		} else {
			filter.setDateValue(dateBox.getValue());
			filter.setValue(dateBox.getTextBox().getText());
		}
		listDataProvider.refresh();

	}

	private void renderFilterCell(final com.google.gwt.cell.client.Cell.Context context,
			final Filter filter, final SafeHtmlBuilder sb) {
		if (filter.getLink() == null) {
			sb.appendEscaped(AppCurrContext.getInstance().getInternationalizedMessages()
					.jsFilterCondition()
					+ " " + filter.getId());
		} else {
			String img;
			if ("OR".equalsIgnoreCase(filter.getLink())) {
				img = "resources/internal/or.png";
			} else {
				img = "resources/internal/and.png";
			}
			sb.appendHtmlConstant("<table>");
			sb.appendHtmlConstant("<td><img border=\"0\" src=" + img + "></td>");
			sb.appendHtmlConstant("<td style='vertical-align:middle;'>");
			sb.appendHtmlConstant("\"" + filter.getColumn() + "\" " + filter.getCondition() + " '"
					+ filter.getValue() + "'");
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("</table>");
		}
	}

}
