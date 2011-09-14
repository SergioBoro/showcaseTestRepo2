package ru.curs.showcase.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.navigator.NavigatorGetCommand;

/**
 * Класс для тестирования фабрики навигаторов.
 * 
 * @author den
 * 
 */
public class NavigatorSLTest extends AbstractTest {
	/**
	 * Тест навигатора, построенного на основе данных из БД.
	 * 
	 */
	@Test
	public void testNavigatorFromDBBySL() throws GeneralException {
		NavigatorGetCommand command =
			new NavigatorGetCommand(new CompositeContext());
		Navigator nav = command.execute();
		assertFalse(nav.getHideOnLoad());
		assertEquals("180px", nav.getWidth());
	}

	/**
	 * Тест навигатора, построенного на основе данных из файла.
	 */
	@Test
	public void testNavigatorFromFileBySL() throws GeneralException {
		CompositeContext context = new CompositeContext();
		context.setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		NavigatorGetCommand command = new NavigatorGetCommand(context);
		Navigator nav = command.execute();
		assertTrue(nav.getHideOnLoad());
		assertEquals(1, nav.getGroups().size());
		final int l1ElementsCount = 1;
		assertEquals(l1ElementsCount, nav.getGroups().get(0).getElements().size());
		assertEquals("04", nav.getAutoSelectElement().getId());
		final int l2ElementsCount = 0;
		assertEquals(l2ElementsCount, nav.getGroups().get(0).getElements().get(0).getElements()
				.size());
	}
}
