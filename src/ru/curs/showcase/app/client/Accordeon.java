/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.client.utils.MultiUserData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.*;

/**
 * @author anlug
 * 
 *         Класс генерации пользовательского интерфейса аккордеона (дерева) в
 *         главной части (в MainPanel) приложения Showcase.
 * 
 */
public class Accordeon {

	public static final String SIZE_ONE_HUNDRED_PERCENTS = "100%";

	/**
	 * Main_context временный с которым должен выполнится ближайший action.
	 */
	private static String tempMainContext = null;

	/**
	 * @return the tempMainContext
	 */
	public static String getTempMainContext() {
		return tempMainContext;
	}

	/**
	 * @param tempMainContext
	 *            the tempMainContext to set
	 */
	public static void setTempMainContext(final String atempMainContext) {
		Accordeon.tempMainContext = atempMainContext;
	}

	/**
	 * Панель содержащая навигатор.
	 */
	private final SimplePanel verpan = new SimplePanel();

	/**
	 * Коллекция объектов аккордеона, которая связывает UI элементы (группы и
	 * элементы дерева) c метаданными.
	 */
	private static List<Tree> uiListOfAccordeonTrees = new ArrayList<Tree>();

	/**
	 * TreeItem в дереве, который был выделен последний и по щелчку на который
	 * задано действие Action.
	 */
	private static TreeItem lastSelectedItem = null;

	/**
	 * Переменная хранящая ссылку на виджет accordeon, типа DecoratedStackPanel.
	 */
	private StackLayoutPanel accordeon = new StackLayoutPanel(Unit.EM);

	/**
	 * переменная accordeonData содержит точную копию структуры Navigator
	 * соответствующую текущему пользователю.
	 */
	private Navigator accordeonData;

	/**
	 * Создает удаленный proxy сервис для общения с серверной частью сервиса
	 * DataService.
	 */
	private final DataServiceAsync dataService = GWT.create(DataService.class);

	/**
	 * @return the accordeon
	 */
	public Widget getAccordeon() {
		return accordeon;
	}

	/**
	 * @param aaccordeon
	 *            the accordeon to set
	 */
	public void setAccordeon(final StackLayoutPanel aaccordeon) {
		this.accordeon = aaccordeon;
	}

	/**
	 * @return the accordeonData
	 */
	public Navigator getAccordeonData() {
		return accordeonData;
	}

	/**
	 * @param aaccordeonData
	 *            the accordeonData to set
	 */
	public void setAccordeonData(final Navigator aaccordeonData) {
		this.accordeonData = aaccordeonData;
	}

	/**
	 * 
	 * Создает аккордеон (дерево или правый навигатор) приложения.
	 * 
	 * @return возвращает заполненный виджет accordeon типа SimplePanel.
	 */
	public Widget generateAccordeon() {

		// verpan.add(new
		// HTML(Constants.PLEASE_WAIT_NAVIGATION_DATA_ARE_LOADING));

		accordeon.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);
		verpan.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		CompositeContext context = MultiUserData.getCurrentContextFromURL();

		// dataService.getNavigator(context, new
		// GWTServiceCallback<Navigator>(AppCurrContext
		// .getInstance().getInternationalizedMessages()
		// .error_of_navigator_data_retrieving_from_server()) {
		dataService.getNavigator(context, new GWTServiceCallback<Navigator>(AppCurrContext
				.getInstance().getBundleMap()
				.get("error_of_navigator_data_retrieving_from_server")) {
			@Override
			public void onFailure(final Throwable caught) {
				ProgressWindow.closeProgressWindow();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(final Navigator navigator) {
				ProgressWindow.closeProgressWindow();
				AppCurrContext
						.getInstance()
						.getMainPanel()
						.generateMainPanel(!navigator.getHideOnLoad(), navigator.getWidth(),
								navigator.getWelcomeTabCaption());

				// navigator.getWidth()
				fillAccordeon(navigator);
				verpan.clear();
				verpan.add(accordeon);
				NavigatorElement nav = navigator.getAutoSelectElement();
				if (nav != null) {
					selectNesessaryItemInAccordion(nav.getId(), true);
				}

			}

		});

		return verpan;
	}

	/**
	 * 
	 * Выделяет элемент с соответствующим id в аккордеонe.
	 * 
	 * @param id
	 *            - id
	 * @param fireEvent
	 *            - boolean параметр который определяет будет ли обрабатываться
	 *            событие клика на дереве в навигаторе при выделении.
	 * 
	 */
	public void selectNesessaryItemInAccordion(final ID id, final Boolean fireEvent) {
		if ((id == null) || id.isEmpty()) {
			return;
		}
		int n = getGroupNamberInAccordeonById(id);
		if (n > -1) {

			accordeon.showWidget(n);

			TreeItem ti = getTreeItemInAccordeonById(id);

			// ti.getParentItem().setState(true);

			if (ti != null) {

				uiListOfAccordeonTrees.get(n).setSelectedItem(ti, fireEvent);
				lastSelectedItem = ti;

				// раскрыть все текущие верхние элементы дерева до выделенного
				// элемента
				while (ti != null) {
					ti.setState(true);
					ti = ti.getParentItem();
				}

			}
		}

	}

	private void fillAccordeon(final Navigator navigator) {
		final int m = 4;

		for (int i = 0; i < navigator.getGroups().size(); i++) {
			accordeon.add(getGroupTreeWidget(navigator.getGroups().get(i)),
					getGroupString(navigator.getGroups().get(i)), true, m);
		}
	}

	private String getGroupString(final NavigatorGroup ng) {

		HorizontalPanel hPanel = new HorizontalPanel();

		final int n = 5;
		hPanel.setSpacing(n);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		Image im = new Image();
		im.setUrl(ng.getImageId()); // by default
									// "resources/group_icon_default.png"
		hPanel.add(im);
		HTML headerText = new HTML(ng.getName());
		headerText.setStyleName("cw-StackPanelHeader");
		hPanel.add(headerText);
		return hPanel.getElement().getString();
	}

	private Widget getGroupTreeWidget(final NavigatorGroup ng) {

		if (!(ng.getElements().size() > 0)) {
			// return new
			// HTML(AppCurrContext.getInstance().getInternationalizedMessages().empty());
			return new HTML(AppCurrContext.getInstance().getBundleMap().get("empty"));
		}
		SimplePanel simpPanel = new SimplePanel();
		ScrollPanel sp = new ScrollPanel();
		simpPanel.add(sp);
		Tree groupTree = new Tree();
		uiListOfAccordeonTrees.add(groupTree);
		sp.add(groupTree);
		sp.setSize(SIZE_ONE_HUNDRED_PERCENTS, SIZE_ONE_HUNDRED_PERCENTS);

		for (int i = 0; i < ng.getElements().size(); i++) {

			// groupTree.add(w);
			// com.google.gwt.safehtml.shared.SafeHtmlString
			// SafeHtml sh;
			// sh = new SafeHtmlString("fdfdf");
			// groupTree.addItem(sh);
			// (itemText)
			TreeItem ti = groupTree.addTextItem(ng.getElements().get(i).getName());
			ti.setUserObject(ng.getElements().get(i));
			generateTreeItem(ng.getElements().get(i), ti);

		}

		groupTree.addSelectionHandler(new TreeSelectionHandler());

		return simpPanel;

	}

	private void generateTreeItem(final NavigatorElement element, final TreeItem treeItem) {
		if (!(element.getElements().size() > 0)) {
			return;
		}

		for (int i = 0; i < element.getElements().size(); i++) {
			TreeItem ti = treeItem.addTextItem(element.getElements().get(i).getName());
			ti.setUserObject(element.getElements().get(i));
			generateTreeItem(element.getElements().get(i), ti);
		}

	}

	/**
	 * @param auiListOfAccordeonTrees
	 *            the uiWidgetsAndDataAccordeon to set
	 */
	public static void setUiListOfAccordeonTrees(final List<Tree> auiListOfAccordeonTrees) {
		Accordeon.uiListOfAccordeonTrees = auiListOfAccordeonTrees;
	}

	/**
	 * @return the uiListOfAccordeonTrees
	 */
	public static List<Tree> getUiListOfAccordeonTrees() {
		return uiListOfAccordeonTrees;
	}

	/**
	 * 
	 * Функция, которая убирает выделение всех элементов всех деревьев
	 * аккардиона, за исключением элемента lastSelectedItem.
	 * 
	 * @param alastSelectedItem
	 *            - TreeItem
	 */
	public static void
			unselectAllTreesItemsExcludingLastSelecter(final TreeItem alastSelectedItem) {

		for (int i = 0; i < getUiListOfAccordeonTrees().size(); i++) {
			if (alastSelectedItem.getTree() != getUiListOfAccordeonTrees().get(i)) {
				getUiListOfAccordeonTrees().get(i).setSelectedItem(null);
			}
		}
		lastSelectedItem = alastSelectedItem;
	}

	/**
	 * Функция, которая отменяет выделение alastSelectedItem, и выделяет
	 * предыдущий выделенный в аккардионе TreeItem (это необходимо, если
	 * произошел клик на TreeItem, на котором нет дейстия-Action).
	 * 
	 * @param alastSelectedItem
	 *            - TreeItem
	 */
	public static void selectLastSelectedItem(final TreeItem alastSelectedItem) {

		alastSelectedItem.getTree().setSelectedItem(null);
		if (lastSelectedItem != null) {
			if (lastSelectedItem.getTree() == alastSelectedItem.getTree()) {
				lastSelectedItem.getTree().setSelectedItem(lastSelectedItem, false);
			}
		}
	}

	/**
	 * Функция возвращающая элемент TreeItem дерева по его уникальному в рамках
	 * аккардиона Id.
	 * 
	 * @param id
	 *            - уникальный Id для элемента дерева
	 * @return - TreeItem для элемента дерева
	 */
	public TreeItem getTreeItemInAccordeonById(final ID id) {

		TreeItem ti = null;
		if (id == null) {
			return ti;
		}
		if (!(uiListOfAccordeonTrees.size() > 0)) {
			return ti;
		}

		for (Tree t : uiListOfAccordeonTrees) {

			for (int i = 0; i < t.getItemCount(); i++) {

				if (id.equals(((NavigatorElement) t.getItem(i).getUserObject()).getId())) {

					return t.getItem(i);

				} else {
					ti = getChildItemsInTreeElement(t.getItem(i), id);
					if (ti != null) {
						return ti;
					}

				}

			}

		}

		return ti;

	}

	private TreeItem getChildItemsInTreeElement(final TreeItem ti, final ID id) {

		if (!(ti.getChildCount() > 0)) {
			return null;
		}

		for (int i = 0; i < ti.getChildCount(); i++) {

			if (id.equals(((NavigatorElement) ti.getChild(i).getUserObject()).getId())) {

				return ti.getChild(i);

			} else {
				TreeItem temp = getChildItemsInTreeElement(ti.getChild(i), id);
				if (temp != null) {
					return temp;
				}

			}

		}
		return null;
	}

	/**
	 * Функция возвращающая номер группы в аккардионе куда входит элемент дерева
	 * с заданным Id.
	 * 
	 * @param id
	 *            - уникальный Id для элемента дерева
	 * @return - Integer - номер группы аккардеона
	 */
	public Integer getGroupNamberInAccordeonById(final ID id) {

		TreeItem ti = null;
		if (id == null) {
			return -1;
		}
		if (!(uiListOfAccordeonTrees.size() > 0)) {
			return -1;
		}

		for (int t = 0; t < uiListOfAccordeonTrees.size(); t++) {
			for (int i = 0; i < uiListOfAccordeonTrees.get(t).getItemCount(); i++) {

				if (id.equals(((NavigatorElement) uiListOfAccordeonTrees.get(t).getItem(i)
						.getUserObject()).getId())) {

					return t;

				} else {
					ti = getChildItemsInTreeElement(uiListOfAccordeonTrees.get(t).getItem(i), id);
					if (ti != null) {
						return t;
					}

				}

			}

		}

		return -1;

	}

	/**
	 * Обновляет навигатор (аккардион).
	 * 
	 * @param selectionId
	 *            - Id узла, для выделения в навигаторе после его обновления.
	 * @param fireEventSelection
	 *            - boolean параметр который определяет будет ли обрабатываться
	 *            событие клика на дереве в навигаторе при выделении, либо узел
	 *            будет выделен без какого-либо действия.
	 */
	public void refreshAccordeon(final ID selectionId, final boolean fireEventSelection) {

		final ID idToSelect =
			(accordeon.getVisibleIndex() > -1) ? ((NavigatorElement) uiListOfAccordeonTrees
					.get(accordeon.getVisibleIndex()).getSelectedItem().getUserObject()).getId()
					: new ID("");

		verpan.clear();
		// verpan.add(new
		// HTML(AppCurrContext.getInstance().getInternationalizedMessages()
		// .please_wait_data_are_loading()));
		verpan.add(new HTML(AppCurrContext.getInstance().getBundleMap()
				.get("please_wait_data_are_loading")));
		accordeon.clear();

		CompositeContext context = MultiUserData.getCurrentContextFromURL();
		// dataService.getNavigator(context, new
		// GWTServiceCallback<Navigator>(AppCurrContext
		// .getInstance().getInternationalizedMessages()
		// .error_of_navigator_data_retrieving_from_server()) {
		dataService.getNavigator(context, new GWTServiceCallback<Navigator>(AppCurrContext
				.getInstance().getBundleMap()
				.get("error_of_navigator_data_retrieving_from_server")) {
			@Override
			public void onFailure(final Throwable caught) {

				ProgressWindow.closeProgressWindow();
				super.onFailure(caught);

			}

			@Override
			public void onSuccess(final Navigator navigator) {

				uiListOfAccordeonTrees.clear();
				ProgressWindow.closeProgressWindow();

				fillAccordeon(navigator);
				verpan.clear();
				verpan.add(accordeon);

				if ((selectionId != null) && (!selectionId.isEmpty())) {
					selectNesessaryItemInAccordion(selectionId, fireEventSelection);
				} else {
					selectNesessaryItemInAccordion(idToSelect, fireEventSelection);
				}

			}

		});
	}
}
