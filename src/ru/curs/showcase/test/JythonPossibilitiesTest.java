package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

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

	private static final String TMP_TEST_PDF = "tmp/test.pdf";
	private static final String TMP_SXSSF_XLSX = "tmp/sxssf.xlsx";

	@Test
	public void testPOI() {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", "poi/excelCreate.py");
		CompositeContext context = new CompositeContext();
		context.setMain("Мейн контекст");
		context.setAdditional(TMP_SXSSF_XLSX);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();

		File output = new File(TMP_SXSSF_XLSX);
		assertTrue(output.exists());
	}

	@Test
	public void testIReport() {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", "ireport/pdfCreate.py");
		CompositeContext context = new CompositeContext();
		context.setMain("Мейн контекст");
		context.setAdditional(TMP_TEST_PDF);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();

		File output = new File(TMP_TEST_PDF);
		assertTrue(output.exists());
	}

}
