package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.*;
import ru.curs.showcase.model.navigator.*;

/**
 * Класс для тестирования фабрики навигаторов.
 * 
 * @author den
 * 
 */
public class NavigatorFactoryTest extends AbstractTestWithDefaultUserData {

	private static final String TEST_ELEMEMNT_NAME = "Вывоз, включая экспорт - Ноябрь";

	/**
	 * Проверка навигатора, созданного обычным конструктором.
	 */
	@Test
	public void testContructor() {
		Navigator nav = new Navigator();
		assertFalse(nav.getHideOnLoad());
		assertNotNull(nav.getGroups());
		assertEquals(0, nav.getGroups().size());
		assertNull(nav.getGroupById("fake"));
		assertNull(nav.getAutoSelectElement());
		assertEquals("300px", nav.getWidth());
	}

	/**
	 * Число элементов в первой группе навигатора.
	 */
	private static final int FIRST_GRP_ELEMENTS_COUNT = 6;

	/**
	 * Тест навигатора, построенного на основе данных из файла XML.
	 * 
	 */
	@Test
	public void testNavigatorFromFile() {
		CompositeContext context =
			new CompositeContext(
					generateTestURLParams(ExchangeConstants.SHOWCASE_USER_DATA_DEFAULT));
		NavigatorFactory factory = new NavigatorFactory(context);
		NavigatorGateway gateway = new NavigatorFileGateway();
		InputStream stream = gateway.getRawData(new CompositeContext(), TREE_MULTILEVEL_XML);

		Navigator nav = factory.fromStream(stream);
		assertEquals("200px", nav.getWidth());
		assertTrue(nav.getHideOnLoad());
		assertEquals(nav.getGroups().get(0).getElements().get(1), nav.getAutoSelectElement());
		final NavigatorGroup firstGroup = nav.getGroupById("1");
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A9", firstGroup.getElements().get(1)
				.getId());
		assertEquals("1", firstGroup.getId());
		assertEquals("Балансы зерна", firstGroup.getName());
		assertEquals("solutions/default/resources/group_icon_default.png", firstGroup.getImageId());
		assertEquals(FIRST_GRP_ELEMENTS_COUNT, firstGroup.getElements().size());
		assertEquals(0, firstGroup.getElements().get(0).getElements().size());
		assertEquals(1, firstGroup.getElements().get(1).getElements().size());
		assertNotNull(firstGroup.getElements().get(0).getAction());
		NavigatorElement testEl = firstGroup.getElements().get(1);
		assertNotNull(testEl.getAction());
		assertEquals(DataPanelActionType.RELOAD_PANEL, testEl.getAction().getDataPanelActionType());
		assertEquals(NavigatorActionType.DO_NOTHING, testEl.getAction().getNavigatorActionType());
		assertEquals("C6CC2BA4-A6ED-4630-8E58-CBBFA9C8C0A8", testEl.getElements().get(0).getId());
		assertEquals(TEST_ELEMEMNT_NAME, testEl.getElements().get(0).getName());
		Action action = testEl.getElements().get(0).getAction();
		assertNotNull(action);
		DataPanelLink link = action.getDataPanelLink();
		assertNotNull(link);
		assertEquals("test.xml", link.getDataPanelId());
		assertEquals("1", link.getTabId());
		assertNotNull(action.getContext());
		assertEquals(TEST_ELEMEMNT_NAME, action.getContext().getMain());
		assertNull(action.getContext().getAdditional());
		assertEquals(1, link.getElementLinks().size());
		assertEquals("1", link.getElementLinks().get(0).getId());
		assertNotNull(link.getElementLinks().get(0).getContext());
		assertEquals(TEST_ELEMEMNT_NAME, link.getElementLinks().get(0).getContext().getMain());
		assertNull(link.getElementLinks().get(0).getContext().getSession());
		assertEquals("(1=1)", link.getElementLinks().get(0).getContext().getAdditional());
	}
}
