package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.*;

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
	private static final String TMP_SIMPLE_XLS = "tmp/simple.xls";

	@Test
	public void testPOI() {
		testFileCreateBase("poi/excelCreate.py", TMP_SXSSF_XLSX);
	}

	private void testFileCreateBase(final String script, final String outputFile) {
		Action action = new Action();
		Activity activity = Activity.newServerActivity("id", script);
		CompositeContext context = new CompositeContext();
		context.setMain("Мейн контекст");
		context.setAdditional(outputFile);
		activity.setContext(context);
		action.setContext(context);
		action.getServerActivities().add(activity);
		ExecServerActivityCommand command = new ExecServerActivityCommand(action);
		command.execute();

		File output = new File(outputFile);
		assertTrue(output.exists());
	}

	@Test
	@Ignore
	public void testXML2SpreadSheet() {
		testFileCreateBase("poi/XML2SpreadSheetProc.py", TMP_SIMPLE_XLS);
	}

	@Test
	public void testIReport() {
		testFileCreateBase("ireport/pdfCreate.py", TMP_TEST_PDF);
	}

}
