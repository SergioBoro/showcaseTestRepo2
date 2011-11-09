package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.webtext.WebTextGetCommand;
import ru.curs.showcase.util.ReflectionUtils;

/**
 * Тест для WebTextDBGateway.
 * 
 * @author den
 * 
 */
public class WebTextSLTest extends AbstractTestWithDefaultUserData {
	/**
	 * Основной тест для проверки работы WebTextDBGateway.
	 */
	@Test
	public void testGetData() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "1");

		WebTextGetCommand command = new WebTextGetCommand(context, element);
		WebText wt = command.execute();

		assertNotNull(context.getSession());
		assertEquals(0, wt.getEventManager().getEvents().size());
		assertNull(wt.getDefaultAction());
		assertNotNull(wt.getData());
	}

	/**
	 * Тест на выборку событий и действия по умолчанию из БД.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testEventsAndDefAction() throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement(TEST2_XML, "1", "3");
		CompositeContext clonedContext = context.gwtClone();

		WebTextGetCommand command = new WebTextGetCommand(clonedContext, element);
		WebText wt = command.execute();

		assertEquals(1, wt.getEventManager().getEvents().size());
		assertEquals("0", wt.getEventManager().getEvents().get(0).getId1());

		Action action = wt.getEventManager().getEvents().get(0).getAction();
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		action = wt.getDefaultAction();
		assertNotNull(action);
		assertEquals(DataPanelActionType.RELOAD_ELEMENTS, action.getDataPanelActionType());
		stdCheckAction(context, action);

		assertNotNull(wt.getData());
	}

	private void stdCheckAction(final CompositeContext context, final Action action)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		assertEquals(NavigatorActionType.DO_NOTHING, action.getNavigatorActionType());
		assertTrue(ReflectionUtils.equals(context, action.getContext()));
		assertEquals(1, action.getDataPanelLink().getElementLinks().size());
		assertTrue(ReflectionUtils.equals(context, action.getDataPanelLink().getElementLinks()
				.get(0).getContext()));
		assertNull(action.getContext().getSession());
	}

	/**
	 * Тест для случая, когда не задана хранимая процедура, возвращающая данные.
	 */
	@Test
	public void testGetStaticDataByXSLT() {
		DataPanelElementInfo el = new DataPanelElementInfo("id", DataPanelElementType.WEBTEXT);
		CompositeContext context = new CompositeContext();
		el.setTransformName("bal_test.xsl");
		generateTestTabWithElement(el);

		WebTextGetCommand command = new WebTextGetCommand(context, el);
		WebText wt = command.execute();

		assertTrue(wt.getData().startsWith("<h3>Здесь находится просто статический текст</h3>"));
		assertTrue(wt.getData().endsWith(
				"<p>Коля у Светы спёр кассеты, а Света у Коли уперла костет</p>"));
	}
}
