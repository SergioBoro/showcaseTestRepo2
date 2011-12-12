package ru.curs.showcase.test.util;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import org.junit.Test;

import ru.curs.showcase.model.command.ExternalCommand;
import ru.curs.showcase.model.jython.JythonExternalCommandGateway;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.test.ws.*;

/**
 * Тесты WS (веб-сервиса).
 * 
 * @author den
 * 
 */
public class WSTest extends AbstractTestWithDefaultUserData {

	private static final String WS_GET_FILE_PY = "ws/GetFile.py";
	private static final String COMMAND_TYPE_GET_DP_PARAM_A_XML =
		"<command type=\"getDP\" param=\"a.xml\"/>";

	@Test
	public void testGateway() {
		JythonExternalCommandGateway gateway = new JythonExternalCommandGateway();
		String res = gateway.handle(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);
		assertTrue(res.indexOf("<element id=\"1\" type=\"webtext\" transform=\"bal.xsl\" />") > -1);
	}

	@Test
	public void testCommand() {
		ExternalCommand command =
			new ExternalCommand(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);
		String res = command.execute();
		assertTrue(res.indexOf("<element id=\"6\" type=\"webtext\" transform=\"bal.xsl\" />") > -1);
	}

	@Test
	public void testWSClient() throws IOException, ShowcaseExportException_Exception {
		Properties localprops = new Properties();
		localprops.load(new FileInputStream("local.properties"));
		Endpoint.publish(localprops.getProperty("webapp") + "/forall/webservices",
				new ru.curs.showcase.app.server.ShowcaseExternals());
		ShowcaseExternalsService service = new ShowcaseExternalsService();
		ShowcaseExternals port = service.getPort(ShowcaseExternals.class);
		String response = port.handle(COMMAND_TYPE_GET_DP_PARAM_A_XML, WS_GET_FILE_PY);

		assertTrue(response.indexOf("<tab id=\"6\" name=\"XForms как фильтр\">") > -1);
	}

	@Test
	public void testWSClientException() throws IOException {
		Properties localprops = new Properties();
		localprops.load(new FileInputStream("local.properties"));
		Endpoint.publish(localprops.getProperty("webapp") + "/forall/webservices",
				new ru.curs.showcase.app.server.ShowcaseExternals());
		ShowcaseExternalsService service = new ShowcaseExternalsService();
		ShowcaseExternals port = service.getPort(ShowcaseExternals.class);
		try {
			port.handle("<command type=\"getНав\" param=\"a.xml\"/>", WS_GET_FILE_PY);
			fail();
		} catch (ShowcaseExportException_Exception e) {
			assertEquals(
					"При вызове Jython процедуры 'ws/GetFile.py' произошла ошибка: getНав не реализовано !",
					e.getMessage());
		}
	}
}
