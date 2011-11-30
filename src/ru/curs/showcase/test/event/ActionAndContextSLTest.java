package ru.curs.showcase.test.event;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.curs.showcase.app.api.ExchangeConstants;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.event.ExecServerActionCommand;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тесты для действий и контекста.
 * 
 * @author den
 * 
 */
public class ActionAndContextSLTest extends AbstractTest {

	/**
	 * Проверка выполнения действия на сервере.
	 * 
	 */
	@Test
	public void testServerActivityExec() {
		final int actionNumber = 1;
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		ExecServerActionCommand command = new ExecServerActionCommand(action);
		command.execute();
		assertNotNull(action.getServerActivities().get(0).getContext().getSession());
	}

	/**
	 * Проверка выполнения действия на сервере, приводящего к ошибке.
	 * 
	 */
	@Test(expected = GeneralException.class)
	public void testServerActivityExecFail() {
		final int actionNumber = 2;
		AppInfoSingleton.getAppInfo().setCurUserDataId("test1");
		Action action = getAction(TREE_MULTILEVEL_V2_XML, 0, actionNumber);
		action.getContext().setSessionParamsMap(generateTestURLParamsForSL(TEST1_USERDATA));
		ExecServerActionCommand command = new ExecServerActionCommand(action);
		command.execute();
	}

	@Test
	public void testJythonActivity() {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", "TestJythonProc.py");
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		context.setMain("Мейн контекст");
		context.setAdditional(ADD_CONTEXT_TAG);
		context.setFilter(FILTER_TAG);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActionCommand command = new ExecServerActionCommand(action);
		command.execute();
	}

	@Test
	public void testJythonSAXActivity() {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", "SAXJythonProc.py");
		CompositeContext context =
			new CompositeContext(generateTestURLParams(ExchangeConstants.DEFAULT_USERDATA));
		context.setMain("Мейн контекст");
		context.setAdditional("<xml wise=\"true\"/>");
		context.setFilter(FILTER_TAG);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActionCommand command = new ExecServerActionCommand(action);
		command.execute();
	}
}
