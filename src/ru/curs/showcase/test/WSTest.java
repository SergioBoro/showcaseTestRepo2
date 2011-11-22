package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.curs.showcase.model.command.ExternalCommand;
import ru.curs.showcase.model.jython.JythonExternalCommandGateway;

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
}
