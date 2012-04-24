package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.core.primelements.navigator.NavigatorGetCommand;
import ru.curs.showcase.util.exception.*;

/**
 * Класс для тестирования фабрики навигаторов.
 * 
 * @author den
 * 
 */
public class NavigatorSLTest extends AbstractTest {
	@Test
	public void testNavigatorFromXML() {
		NavigatorGetCommand command = new NavigatorGetCommand(new CompositeContext());
		Navigator nav = command.execute();
		assertFalse(nav.getHideOnLoad());
		assertEquals("180px", nav.getWidth());
	}

	@Test
	public void testNavigatorFromMSSQL() {
		NavigatorGetCommand command = new NavigatorGetCommand(new CompositeContext());
		command.setPropFile("t01.app.properties");
		Navigator nav = command.execute();
		assertEquals("199px", nav.getWidth());
	}

	@Test
	public void mssqlFilesNotAllowedForPostgreSQL() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL("pg"));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		command.setPropFile("t01.app.properties");
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(NotImplementedYetException.class, e.getCause().getClass());
			return;
		}
		fail();
	}

	@Test
	public void appShouldCheckForExistanceSQLFile() {
		NavigatorGetCommand command = new NavigatorGetCommand(new CompositeContext());
		command.setPropFile("t02.app.properties");
		try {
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SettingsFileOpenException.class, e.getCause().getClass());
			return;
		}
		fail();
	}

	@Test
	@Ignore
	public void testNavigatorFromDB() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST2_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator nav = command.execute();
		final int groupsCount = 2;
		assertEquals(groupsCount, nav.getGroups().size());
		final int elementsCount = 10;
		assertEquals(elementsCount, nav.getGroups().get(0).getElements().size());
		final int subElementsCount = 100;
		assertEquals(subElementsCount, nav.getGroups().get(0).getElements().get(0).getElements()
				.size());
	}

	@Test
	public void testNavigatorFromJython() {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator nav = command.execute();

		assertTrue(nav.getHideOnLoad());
		assertEquals(1, nav.getGroups().size());
		final int l1ElementsCount = 1;
		assertEquals(l1ElementsCount, nav.getGroups().get(0).getElements().size());
		assertEquals("04", nav.getAutoSelectElement().getId().getString());
		final int l2ElementsCount = 0;
		assertEquals(l2ElementsCount, nav.getGroups().get(0).getElements().get(0).getElements()
				.size());
	}
}
