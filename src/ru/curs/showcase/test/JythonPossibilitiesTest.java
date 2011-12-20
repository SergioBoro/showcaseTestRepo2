package ru.curs.showcase.test;

import org.junit.Test;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.event.ExecServerActivityCommand;

/**
 * Класс для проверок возможностей Jython и сценариев его использования.
 * 
 * @author den
 * 
 */
public class JythonPossibilitiesTest extends AbstractTest {

	@Test
	public void testPOI() {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", "poi/excelCreate.py");
		CompositeContext context = new CompositeContext();
		context.setMain("Мейн контекст");
		context.setAdditional("tmp");
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();
	}

}
