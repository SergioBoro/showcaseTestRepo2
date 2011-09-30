package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import ru.curs.showcase.app.api.CanBeCurrent;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextTest extends AbstractTestWithDefaultUserData {
	private static final String TEST_ACTIVITY_NAME = "test";
	private static final String MAIN_CONDITION = "Алтайский край";
	private static final String SESSION_CONDITION =
		"<sessioncontext><username>master</username><urlparams></urlparams></sessioncontext>";
	private static final String FILTER_CONDITION = "filter";
	private static final int DEF_SIZE_VALUE = 100;
	private static final String NEW_ADD_CONDITION = "New add condition";
	private static final String MOSCOW_CONTEXT = "Москва";
	private static final String TAB_1 = "1";
	private static final String TAB_2_NAME = "Вкладка 2";
	private static final String EL_06 = "06";
	private static final String TAB_2 = "2";

	/**
	 * Тест клонирования Action и составляющих его объектов.
	 */
	@Test
	public void testClone() {
		Action action = createComplexTestAction();
		Action clone = action.gwtClone();

		assertEquals(DataPanelActionType.RELOAD_PANEL, clone.getDataPanelActionType());
		assertEquals(NavigatorActionType.CHANGE_NODE, clone.getNavigatorActionType());
		assertFalse(clone.getKeepUserSettings());
		assertEquals(ShowInMode.MODAL_WINDOW, clone.getShowInMode());

		ModalWindowInfo mwi = clone.getModalWindowInfo();
		assertNotNull(mwi);
		assertEquals("mwi", mwi.getCaption());
		assertEquals(DEF_SIZE_VALUE, mwi.getHeight().intValue());
		assertEquals(DEF_SIZE_VALUE, mwi.getWidth().intValue());
		assertTrue(mwi.getShowCloseBottomButton());

		CompositeContext context = getComplexTestContext();
		assertNotNull(clone.getContext());
		assertEquals(context, clone.getContext());

		DataPanelLink link = clone.getDataPanelLink();
		assertNotNull(link);
		assertFalse(link.getFirstOrCurrentTab());
		assertEquals(TEST_XML, link.getDataPanelId());
		assertEquals(TAB_2, link.getTabId());
		assertEquals(1, link.getElementLinks().size());
		assertEquals(EL_06, link.getElementLinks().get(0).getId());
		assertEquals(ADD_CONDITION, link.getElementLinks().get(0).getContext().getAdditional());
		assertTrue(link.getElementLinks().get(0).getSkipRefreshContextOnly());
		assertTrue(link.getElementLinks().get(0).getRefreshContextOnly());
		assertTrue(link.getElementLinks().get(0).getKeepUserSettings());

		assertNotNull(clone.getNavigatorElementLink());
		assertEquals("nLink", clone.getNavigatorElementLink().getId());
		assertTrue(clone.getNavigatorElementLink().getRefresh());

		assertNotSame(action, clone);
		assertNotSame(action.getDataPanelLink(), clone.getDataPanelLink());
		assertNotSame(action.getContext(), clone.getContext());
		assertNotSame(action.getDataPanelLink().getElementLinks().get(0), clone.getDataPanelLink()
				.getElementLinks().get(0));

		assertTrue(clone.containsServerActivity());
		Activity act = clone.getServerActivities().get(0);
		assertNotNull(act);
		assertEquals(ActivityType.SP, act.getType());
		assertEquals("01", act.getId());
		assertEquals(TEST_ACTIVITY_NAME, act.getName());
		assertEquals(ADD_CONDITION, act.getContext().getAdditional());
		assertNotSame(action.getServerActivities().get(0), act);

		assertTrue(clone.containsClientActivity());
		act = clone.getClientActivities().get(0);
		assertNotNull(act);
		assertEquals(ActivityType.BrowserJS, act.getType());
		assertEquals("01", act.getId());
		assertEquals("testJS", act.getName());
		assertEquals(ADD_CONDITION, act.getContext().getAdditional());
		assertNotSame(action.getClientActivities().get(0), act);
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
		assertTrue(action.getContext().mainIsCurrent());
		assertTrue(action.getContext().addIsCurrent());
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
		assertTrue(action.getContext().addIsCurrent());
		assertTrue(action.getContext().mainIsCurrent());

		final DataPanelLink dataPanelLink = action.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(CanBeCurrent.CURRENT_ID, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		assertEquals(0, dataPanelLink.getElementLinks().size());
	}

	/**
	 * Проверка актуализации Action для Tab на основе Action при обновлении
	 * данных на открытой вкладке.
	 * 
	 */
	@Test
	public void testRefreshTab() {
		Action first = createSimpleTestAction();

		DataPanelTab tab = createStdTab();
		ActionHolder ah = new ActionHolder();
		ah.setNavigatorAction(first.gwtClone());
		ah.setNavigatorActionFromTab(tab.getAction());
		Action actual = ah.getNavigatorAction();

		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertTrue(actual.getKeepUserSettings());
		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId());
		assertFalse(dataPanelLink.getFirstOrCurrentTab());
		assertEquals(1, dataPanelLink.getElementLinks().size());

		CompositeContext context = getSimpleTestContext();
		assertEquals(context, actual.getContext());
	}

	/**
	 * Проверка установки DataPanelActionType.RELOAD_TAB при открытии новой
	 * вкладки на уже открытой панели.
	 * 
	 */
	@Test
	public void testSwitchToNewTab() {
		Action first = createSimpleTestAction();
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
	 */
	@Test
	public void testFirstOrCurrentActualize() {
		Action first = createSimpleTestAction();
		first.getDataPanelLink().setTabId(TAB_2);
		first.getContext().setAdditional(ADD_CONDITION);

		Action foc = new Action(DataPanelActionType.REFRESH_TAB);
		DataPanelLink link = foc.getDataPanelLink();
		CompositeContext cc = new CompositeContext();
		foc.setContext(cc);
		cc.setMain(MOSCOW_CONTEXT);
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_1);
		link.setFirstOrCurrentTab(true);
		Action actual = foc.gwtClone().actualizeBy(first);

		assertEquals(NavigatorActionType.DO_NOTHING, actual.getNavigatorActionType());
		assertEquals(DataPanelActionType.REFRESH_TAB, actual.getDataPanelActionType());
		assertEquals(MOSCOW_CONTEXT, actual.getContext().getMain());
		assertNull(actual.getContext().getAdditional()); // !

		final DataPanelLink dataPanelLink = actual.getDataPanelLink();
		assertNotNull(dataPanelLink);
		assertEquals(TEST_XML, dataPanelLink.getDataPanelId());
		assertEquals(TAB_2, dataPanelLink.getTabId()); // !
		assertTrue(dataPanelLink.getFirstOrCurrentTab()); // !
		assertEquals(0, dataPanelLink.getElementLinks().size()); // !
	}

	/**
	 * Тест на обновление дополнительного контекста.
	 * 
	 */
	@Test
	public void testUpdateAddContext() {
		Grid grid = createTestGrid();
		CompositeContext context = new CompositeContext();
		context.setAdditional(NEW_ADD_CONDITION);
		grid.updateAddContext(context);

		assertEquals(NEW_ADD_CONDITION, grid.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinks().get(0).getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getServerActivities().get(0)
				.getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getClientActivities().get(0)
				.getContext().getAdditional());
	}

	/**
	 * Тест на обновление дополнительного контекста.
	 * 
	 */
	@Test
	public void testActualizeActions() {
		Grid grid = createTestGrid();
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONDITION);
		context.setAdditional(NEW_ADD_CONDITION);
		grid.actualizeActions(context);

		assertEquals(MAIN_CONDITION, grid.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinks().get(0).getContext().getMain());
		assertEquals(MAIN_CONDITION, grid.getDefaultAction().getDataPanelLink().getElementLinks()
				.get(0).getContext().getMain());
		assertEquals(MAIN_CONDITION, grid.getDefaultAction().getServerActivities().get(0)
				.getContext().getMain());

		assertEquals(NEW_ADD_CONDITION, grid.getEventManager().getEvents().get(0).getAction()
				.getDataPanelLink().getElementLinks().get(0).getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getDataPanelLink()
				.getElementLinks().get(0).getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getServerActivities().get(0)
				.getContext().getAdditional());
		assertEquals(NEW_ADD_CONDITION, grid.getDefaultAction().getClientActivities().get(0)
				.getContext().getAdditional());
	}

	private Grid createTestGrid() {
		Grid grid = new Grid();
		GridEvent event = new GridEvent();
		event.setRecordId("01");
		Action action = createCurrentTestAction();
		event.setAction(action);
		grid.getEventManager().getEvents().add(event);
		grid.setDefaultAction(action);
		return grid;
	}

	/**
	 * Тест на действие обновления навигатора.
	 * 
	 */
	@Test
	public void testRefreshNavigatorAction() {
		final int el2 = 2;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, el2);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		final int el3 = 3;
		action = getAction(TREE_MULTILEVEL_XML, 0, el3);
		assertTrue(action.getNavigatorElementLink().getRefresh());
		assertNotNull(action.getNavigatorElementLink().getId());
		final int el4 = 4;
		action = getAction(TREE_MULTILEVEL_XML, 0, el4);
		assertNotNull(action.getNavigatorElementLink().getId());
	}

	/**
	 * Проверка считывания опции refresh_context_only. Она не имеет смысла в
	 * навигаторе, но это же просто тест, а не use case.
	 * 
	 */
	@Test
	public void testRefreshContextOnlyAction() {
		final int elNum = 6;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, elNum);
		assertTrue(action.getDataPanelLink().getElementLinks().get(0).getRefreshContextOnly());
		assertTrue(action.getDataPanelLink().getElementLinks().get(1).getSkipRefreshContextOnly());
	}

	/**
	 * Проверка считывания информации о модальном окне для действия.
	 * 
	 */
	@Test
	public void testActionModalInfo() {
		Action action = new Action();
		assertNull(action.getModalWindowInfo());

		final int elNum = 5;
		action = getAction(TREE_MULTILEVEL_XML, 0, elNum);
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

	/**
	 * Создаем тестовое действие с не дефолтными значениями всех возможных
	 * атрибутов.
	 * 
	 * @return - действие.
	 */
	private Action createComplexTestAction() {
		Action action = createSimpleTestAction();

		action.setKeepUserSettings(false);
		action.setShowInMode(ShowInMode.MODAL_WINDOW);

		ModalWindowInfo mwi = new ModalWindowInfo();
		mwi.setCaption("mwi");
		mwi.setHeight(DEF_SIZE_VALUE);
		mwi.setWidth(DEF_SIZE_VALUE);
		mwi.setShowCloseBottomButton(true);
		action.setModalWindowInfo(mwi);

		NavigatorElementLink nLink = new NavigatorElementLink();
		nLink.setId("nLink");
		nLink.setRefresh(true);
		action.setNavigatorElementLink(nLink);

		action.setContext(getComplexTestContext());

		DataPanelElementLink elLink = action.getDataPanelLink().getElementLinkById(EL_06);
		elLink.setKeepUserSettings(true);
		elLink.setRefreshContextOnly(true);
		elLink.setSkipRefreshContextOnly(true);

		Activity act = new Activity("01", TEST_ACTIVITY_NAME, ActivityType.SP);
		act.setContext(getComplexTestContext());
		action.getServerActivities().add(act);

		act = new Activity("01", "testJS", ActivityType.BrowserJS);
		act.setContext(getComplexTestContext());
		action.getClientActivities().add(act);

		action.determineState();

		return action;
	}

	private Action createSimpleTestAction() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		CompositeContext context = getSimpleTestContext();
		action.setContext(context);

		DataPanelLink link = action.getDataPanelLink();
		link.setDataPanelId(TEST_XML);
		link.setTabId(TAB_2);
		CompositeContext elContext = context.gwtClone();
		elContext.setAdditional(ADD_CONDITION);

		DataPanelElementLink elLink = new DataPanelElementLink(EL_06, elContext);
		link.getElementLinks().add(elLink);

		action.determineState();
		return action;
	}

	private Action createCurrentTestAction() {
		Action action = new Action(DataPanelActionType.RELOAD_PANEL);
		CompositeContext context = CompositeContext.createCurrent();
		action.setContext(context);

		DataPanelLink link = action.getDataPanelLink();
		link.setDataPanelId(CanBeCurrent.CURRENT_ID);
		link.setTabId(CanBeCurrent.CURRENT_ID);
		CompositeContext elContext = context.gwtClone();

		DataPanelElementLink elLink = new DataPanelElementLink(EL_06, elContext);
		link.getElementLinks().add(elLink);

		Activity act = new Activity("01", TEST_ACTIVITY_NAME, ActivityType.SP);
		act.setContext(context);
		action.getServerActivities().add(act);

		act = new Activity("01", TEST_ACTIVITY_NAME, ActivityType.BrowserJS);
		act.setContext(context);
		action.getClientActivities().add(act);

		action.determineState();
		return action;
	}

	private CompositeContext getSimpleTestContext() {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONDITION);
		return context;
	}

	private CompositeContext getComplexTestContext() {
		CompositeContext context = getSimpleTestContext();
		context.setFilter(FILTER_CONDITION);
		context.setAdditional(ADD_CONDITION);
		context.setSession(SESSION_CONDITION);

		return context;
	}

	/**
	 * Проверка работы функции Action.filterBy.
	 * 
	 */
	@Test
	public void testActionFilterBy() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.filterBy(FILTER_CONDITION);
		assertEquals(FILTER_CONDITION, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getFilter());
		assertEquals(FILTER_CONDITION, action.getContext().getFilter());
		assertEquals(FILTER_CONDITION, action.getServerActivities().get(0).getContext()
				.getFilter());
		assertEquals(FILTER_CONDITION, action.getClientActivities().get(0).getContext()
				.getFilter());
	}

	/**
	 * Проверка генерации фильтрующего контекста.
	 */
	@Test
	public void testActionGenerateFilterContextLine() {
		String filter = CompositeContext.generateFilterContextLine("add_context1");
		filter = filter + CompositeContext.generateFilterContextLine("add_context2");
		filter = CompositeContext.generateFilterContextGeneralPart(filter);
		assertEquals(
				"<filter><context>add_context1</context><context>add_context2</context></filter>",
				filter);
	}

	/**
	 * Проверка работы функции setCurrentAction у ActionHolder.
	 * 
	 */
	@Test
	public void testActionHolderSetCurrentAction() {
		Action first = createSimpleTestAction();
		ActionHolder ah = new ActionHolder();
		ah.setNavigatorAction(first);
		first.filterBy(FILTER_CONDITION);
		ah.setCurrentAction(first);

		Action insideAction = new Action(DataPanelActionType.REFRESH_TAB);
		insideAction.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId(CanBeCurrent.CURRENT_ID);
		dpLink.setTabId(TAB_2);
		insideAction.setDataPanelLink(dpLink);
		insideAction.determineState();
		Activity act = new Activity("01", TEST_ACTIVITY_NAME, ActivityType.SP);
		act.setContext(CompositeContext.createCurrent());
		insideAction.getServerActivities().add(act);
		act = new Activity("01", TEST_ACTIVITY_NAME, ActivityType.BrowserJS);
		act.setContext(CompositeContext.createCurrent());
		insideAction.getClientActivities().add(act);
		ah.setCurrentAction(insideAction);

		assertNotNull(ah.getCurrentAction());
		assertEquals(DataPanelActionType.REFRESH_TAB, ah.getCurrentAction()
				.getDataPanelActionType());
		assertEquals(FILTER_CONDITION, ah.getCurrentAction().getContext().getFilter());
		assertTrue(ah.getCurrentAction().getKeepUserSettings());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getContext().getMain());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getServerActivities().get(0)
				.getContext().getMain());
		assertEquals(MAIN_CONDITION, ah.getCurrentAction().getClientActivities().get(0)
				.getContext().getMain());
	}

	/**
	 * Тест для настройки keepUserSettings.
	 * 
	 */
	@Test
	public void testActionKeepUserSettings() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		action.getDataPanelLink().getElementLinks().add(new DataPanelElementLink());
		action.determineState();
		assertTrue(action.getKeepUserSettings());
		assertFalse(action.getDataPanelLink().getElementLinks().get(0).getKeepUserSettings());

		final int actionWithChildNumber = 5;
		action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
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

	/**
	 * Проверка считывания блока действия, касающегося серверной активности.
	 */
	@Test
	public void testServerActivityRead() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		assertTrue(action.containsServerActivity());
		assertEquals(1, action.getServerActivities().size());
		Activity sa = action.getServerActivities().get(0);
		assertEquals("srv01", sa.getId());
		assertEquals("exec_test", sa.getName());
		assertEquals(ActivityType.SP, sa.getType());
		assertNotNull(sa.getContext());
		assertEquals(action.getContext().getMain(), sa.getContext().getMain());
		assertEquals(
				"<context:somexml someattr=\"value\" ><other ></other>test</context:somexml>", sa
						.getContext().getAdditional());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.app.api.event.Action#needGeneralContext
	 * Action.needGeneralContext}.
	 */
	@Test
	public void testNeedGeneralContext() {
		Action action = new Action();
		DataPanelLink dpl = new DataPanelLink();
		dpl.setDataPanelId("test1.xml");
		dpl.setTabId("1");
		action.setDataPanelLink(dpl);
		action.setContext(CompositeContext.createCurrent());
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		action.setContext(CompositeContext.createCurrent());
		Activity act = new Activity("01", "test_proc", ActivityType.SP);
		act.setContext(CompositeContext.createCurrent());
		action.getServerActivities().add(act);
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		action.setContext(CompositeContext.createCurrent());
		act = new Activity("01", "test_proc", ActivityType.BrowserJS);
		act.setContext(CompositeContext.createCurrent());
		action.getClientActivities().add(act);
		action.determineState();
		assertTrue(action.needGeneralContext());

		action = new Action();
		NavigatorElementLink nel = new NavigatorElementLink();
		nel.setId("01");
		action.setNavigatorElementLink(nel);
		action.determineState();
		assertFalse(action.needGeneralContext());

		action = new Action();
		nel = new NavigatorElementLink();
		nel.setRefresh(true);
		action.setNavigatorElementLink(nel);
		action.determineState();
		assertFalse(action.needGeneralContext());
	}

	/**
	 * Проверка считывания действия, содержащего вызовы действия на клиенте, не
	 * связанные с навигатором и инф. панелью.
	 */
	@Test
	public void testReadClientActivity() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		assertTrue(action.containsClientActivity());
		assertEquals(1, action.getClientActivities().size());
		Activity ac = action.getClientActivities().get(0);
		assertEquals("show_moscow", ac.getName());
		assertEquals("cl01", ac.getId());
		assertEquals(ActivityType.BrowserJS, ac.getType());
	}

	@Test
	public void testAddRelatedToContext() {
		CompositeContext parent = CompositeContext.createCurrent();
		CompositeContext related = new CompositeContext();
		related.setMain(MAIN_CONDITION);
		related.setAdditional(ADD_CONDITION);
		related.setFilter(FILTER_CONDITION);
		related.setSession(SESSION_CONDITION);
		related.setSessionParamsMap(new TreeMap<String, ArrayList<String>>());
		related.getRelated().put("rrid", new CompositeContext());
		parent.addRelated("rid", related);

		assertEquals(1, parent.getRelated().size());
		CompositeContext test = parent.getRelated().values().iterator().next();
		assertNull(test.getMain());
		assertEquals(ADD_CONDITION, test.getAdditional());
		assertEquals(FILTER_CONDITION, test.getFilter());
		assertNull(test.getSession());
		assertTrue(test.getSessionParamsMap().isEmpty());
		assertTrue(test.getRelated().isEmpty());
	}

	@Test
	public void testActionSetAdditionalContext() {
		final int actionWithChildNumber = 5;
		Action action = getAction(TREE_MULTILEVEL_XML, 0, actionWithChildNumber);
		action.setAdditionalContext(ADD_CONDITION);
		assertEquals(ADD_CONDITION, action.getDataPanelLink().getElementLinks().get(0)
				.getContext().getAdditional());
		assertEquals(ADD_CONDITION, action.getServerActivities().get(0).getContext()
				.getAdditional());
		assertEquals(ADD_CONDITION, action.getClientActivities().get(0).getContext()
				.getAdditional());
	}

	@Test
	public void testGridContext() {
		CompositeContext context = getComplexTestContext();
		GridContext ces = new GridContext(context);

		assertEquals(MAIN_CONDITION, ces.getMain());
		assertEquals(ADD_CONDITION, ces.getAdditional());
		assertEquals(FILTER_CONDITION, ces.getFilter());
		assertEquals(SESSION_CONDITION, ces.getSession());
	}
}
