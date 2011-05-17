package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextTest extends AbstractTestBasedOnFiles {

	static final String FILTER_CONTEXT = "filter";
	static final String NEW_ADD_CONDITION = "New add condition";
	static final String MASTER_USER = "master";
	static final String MOSCOW_CONTEXT = "Москва";
	static final String TAB_1 = "1";
	static final String TAB_2_NAME = "Вкладка 2";
	static final String EL_06 = "06";
	static final String ADD_CONDITION = "add_condition";
	static final String TAB_2 = "2";
	static final String TEST_XML = "test.xml";

	/**
	 * Тест клонирования Action и составляющих его объектов.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testClone() throws IOException {
		Action action = createTestAction();
		Action clone = action.gwtClone();

		assertEquals(DataPanelActionType.RELOAD_PANEL, clone.getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, clone.getNavigatorActionType());
		assertNull(clone.getNavigatorElementLink());
		DataPanelLink link = clone.getDataPanelLink();
		assertNotNull(link);
		assertNotNull(link.getContext());
		CompositeContext context = getTestContext();
		assertEquals(context, link.getContext());
		assertFalse(link.getFirstOrCurrentTab());
		assertEquals(TEST_XML, link.getDataPanelId());
		assertEquals(TAB_2, link.getTabId());
		assertEquals(1, link.getElementLinks().size());
		assertEquals(EL_06, link.getElementLinks().get(0).getId());
		assertEquals(ADD_CONDITION, link.getElementLinks().get(0).getContext().getAdditional());
		assertFalse(link.getElementLinks().get(0).getSkipRefreshContextOnly());

		assertTrue(action != clone);
		assertTrue(action.getDataPanelLink() != clone.getDataPanelLink());
		assertTrue(action.getDataPanelLink().getContext() != clone.getDataPanelLink().getContext());
		assertTrue(action.getDataPanelLink().getElementLinks().get(0) != clone.getDataPanelLink()
				.getElementLinks().get(0));
	}

	/**
	 * Проверка создания текущего контекста.
	 */
	@Test
	public void testCreateCurrentContext() {
		CompositeContext cc = CompositeContext.createCurrent();
		assertTrue(cc.addIsCurrent());
		assertTrue(cc.mainIsCurrent());
		assertNull(cc.getFilter());
		assertNull(cc.getSession());
	}

	/**
	 * Проверка создания действия для обновления элементов открытой вкладки
	 * инф.панели.
	 */
	@Test
	public void testCreateRefreshElementsAction() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		assertEquals(CanBeCurrent.CURRENT_ID, action.getDataPanelLink().getDataPanelId());
		assertEquals(CanBeCurrent.CURRENT_ID, action.getDataPanelLink().getTabId());
		assertTrue(action.getDataPanelLink().getContext().mainIsCurrent());
		assertTrue(action.getDataPanelLink().getContext().addIsCurrent());
		assertEquals(ShowInMode.PANEL, action.getShowInMode());
	}

	/**
	 * Проверка создания Action для Tab.
	 */
	@Test
	public void testGetTabAction() {
		DataPanelTab tab = createStdTab();
		Action action = tab.getAction();
		assertNull(action.getNavigatorElementLink());
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, action.getDataPanelActionType());

		final DataPanelLink dataPanelLink = action.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(CanBeCurrent.CURRENT_ID, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		assertTrue(dataPanelLink.getContext().addIsCurrent());
		assertTrue(dataPanelLink.getContext().mainIsCurrent());
		assertEquals(0, dataPanelLink.getElementLinks().size());
	}

	/**
	 * Проверка актуализации Action для Tab на основе Action при обновлении
	 * данных на открытой вкладке.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRefreshTab() throws IOException {
		Action first = createTestAction();
		DataPanelTab tab = createStdTab();
		ActionHolder ah = new ActionHolder();
		ah.setNavigatorAction(first.gwtClone());
		ah.setNavigatorActionFromTab(tab.getAction());
		Action actual = ah.getNavigatorAction();
		assertEquals(NavigatorActionType.DO_NOTHING, actual.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertTrue(actual.getKeepUserSettings());
		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		CompositeContext context = getTestContext();
		assertEquals(context, dataPanelLink.getContext());
		assertEquals(1, dataPanelLink.getElementLinks().size());
	}

	/**
	 * Проверка установки DataPanelActionType.RELOAD_TAB при открытии новой
	 * вкладки на уже открытой панели.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSwitchToNewTab() throws IOException {
		Action first = createTestAction();
		DataPanelTab tab = createStdTab();
		tab.setId(TAB_1);

		ActionHolder ah = new ActionHolder();
		ah.setNavigatorAction(first.gwtClone());
		ah.setNavigatorActionFromTab(tab.getAction());
		Action actual = ah.getNavigatorAction();
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertFalse(actual.getKeepUserSettings());
	}

	/**
	 * Проверка актуализации действия типа firstOrCurrent.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFirstOrCurrentActualize() throws IOException {
		Action first = createTestAction();
		first.getDataPanelLink().setTabId(TAB_2);
		first.getDataPanelLink().getContext().setAdditional(ADD_CONDITION);

		Action foc = new Action(DataPanelActionType.REFRESH_TAB);
		DataPanelLink link = foc.getDataPanelLink();
		CompositeContext cc = new CompositeContext();
		link.setContext(cc);
		cc.setMain(MOSCOW_CONTEXT);
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_1);
		link.setFirstOrCurrentTab(true);
		Action actual = foc.gwtClone().actualizeBy(first);

		assertEquals(NavigatorActionType.DO_NOTHING, actual.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId()); // !
		assertTrue(dataPanelLink.getFirstOrCurrentTab()); // !
		assertEquals(MOSCOW_CONTEXT, dataPanelLink.getContext().getMain());
		assertEquals(null, dataPanelLink.getContext().getAdditional()); // !
		assertEquals(0, dataPanelLink.getElementLinks().size()); // !
	}

	/**
	 * Тест на обновление дополнительного контекста.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdateAddContext() throws IOException {
		Grid grid = new Grid();
		GridEvent event = new GridEvent();
		event.setRecordId("01");
		Action action = createTestAction();
		event.setAction(action);
		grid.getEventManager().getEvents().add(event);
		grid.setDefaultAction(action);
		CompositeContext context = new CompositeContext();
		context.setAdditional(NEW_ADD_CONDITION);
		grid.updateAddContext(context);

		assertEquals(NEW_ADD_CONDITION, grid.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinks().get(0).getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());
	}

	/**
	 * Тест на действие обновления навигатора.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRefreshNavigatorAction() throws IOException {
		final int el2 = 2;
		Action action = getAction("tree_multilevel.xml", 0, el2);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		final int el3 = 3;
		action = getAction("tree_multilevel.xml", 0, el3);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		assertNotNull(action.getNavigatorElementLink().getId());
		final int el4 = 4;
		action = getAction("tree_multilevel.xml", 0, el4);
		assertNotNull(action.getNavigatorElementLink().getId());
	}

	/**
	 * Проверка считывания опции refresh_context_only. Она не имеет смысла в
	 * навигаторе, но это же просто тест, а не use case.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRefreshContextOnlyAction() throws IOException {
		final int elNum = 5;
		Action action = getAction("tree_multilevel.xml", 0, elNum);
		assertTrue(action.getDataPanelLink().getElementLinks().get(0).getRefreshContextOnly());
		assertTrue(action.getDataPanelLink().getElementLinks().get(1).getSkipRefreshContextOnly());
	}

	/**
	 * Проверка считывания информации о модальном окне для действия.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testActionModalInfo() throws IOException {
		Action action = new Action();
		assertNull(action.getModalWindowInfo());

		final int elNum = 5;
		action = getAction("tree_multilevel.xml", 0, elNum);
		assertEquals("test_action_name", action.getModalWindowInfo().getCaption());
		final int mwWidth = 99;
		assertEquals(mwWidth, action.getModalWindowInfo().getWidth().intValue());
		final int mwHeight = 98;
		assertEquals(mwHeight, action.getModalWindowInfo().getHeight().intValue());
		assertTrue(action.getModalWindowInfo().getShowCloseBottomButton());
	}

	private DataPanelTab createStdTab() {
		DataPanel dp = new DataPanel();
		DataPanelTab tab = dp.add(TAB_2, TAB_2_NAME);
		return tab;
	}

	private Action createTestAction() throws IOException {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		CompositeContext context = getTestContext();
		DataPanelLink link = action.getDataPanelLink();
		link.setContext(context);
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_2);
		CompositeContext elContext = context.gwtClone();
		elContext.setAdditional(ADD_CONDITION);
		DataPanelElementLink elLink = new DataPanelElementLink(EL_06, elContext);
		link.getElementLinks().add(elLink);
		action.determineState();
		return action;
	}

	private CompositeContext getTestContext() throws IOException {
		CompositeContext context = getContext("tree_multilevel.v2.xml", 1, 0);
		return context;
	}

	/**
	 * Проверка работы функции Action.filterBy.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testActionFilterBy() throws IOException {
		final int actionWithChildNumber = 5;
		Action action = getAction("tree_multilevel.xml", 0, actionWithChildNumber);
		action.filterBy(FILTER_CONTEXT);
		assertEquals(FILTER_CONTEXT, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getFilter());
		assertEquals(FILTER_CONTEXT, action.getDataPanelLink().getContext().getFilter());
	}

	/**
	 * Проверка генерации фильтрующего контекста.
	 */
	@Test
	public void testActionGenerateFilterContextLine() {
		String filter = Action.generateFilterContextLine("add_context1");
		filter = filter + Action.generateFilterContextLine("add_context2");
		filter = Action.generateFilterContextGeneralPart(filter);
		assertEquals(
				"<filter><context>add_context1</context><context>add_context2</context></filter>",
				filter);
	}

	/**
	 * Проверка работы функции setCurrentAction у ActionHolder.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testActionHolderSetCurrentAction() throws IOException {
		Action first = createTestAction();
		ActionHolder ah = new ActionHolder();
		ah.setNavigatorAction(first);
		first.filterBy(FILTER_CONTEXT);
		ah.setCurrentAction(first);

		Action insideAction = new Action(DataPanelActionType.REFRESH_TAB);
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(CanBeCurrent.CURRENT_ID);
		dpLink.setTabId(TAB_2);
		dpLink.setContext(CompositeContext.createCurrent());
		insideAction.setDataPanelLink(dpLink);
		insideAction.determineState();
		ah.setCurrentAction(insideAction);

		assertNotNull(ah.getCurrentAction());
		assertEquals(DataPanelActionType.REFRESH_TAB, ah.getCurrentAction()
				.getDataPanelActionType());
		assertEquals(FILTER_CONTEXT, ah.getCurrentAction().getDataPanelLink().getContext()
				.getFilter());
		assertTrue(ah.getCurrentAction().getKeepUserSettings());
	}

	/**
	 * Тест для настройки keepUserSettings.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testActionKeepUserSettings() throws IOException {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		action.getDataPanelLink().getElementLinks().add(new DataPanelElementLink());
		action.determineState();
		assertTrue(action.getKeepUserSettings());
		assertFalse(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());

		final int actionWithChildNumber = 5;
		action = getAction("tree_multilevel.xml", 0, actionWithChildNumber);
		action.determineState();
		assertFalse(action.getKeepUserSettings());
		assertFalse(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());
		assertTrue(action.getDataPanelLink().getElementLinks().get(1).getKeepUserSettings());

		action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		action.getDataPanelLink().getElementLinks().add(new DataPanelElementLink());
		action.determineState();
		action.setKeepUserSettingsForAll(true);
		assertTrue(action.getKeepUserSettings());
		assertTrue(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());
	}
}
