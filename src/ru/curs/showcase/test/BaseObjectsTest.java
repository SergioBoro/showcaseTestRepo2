package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.SQLException;

import org.junit.Test;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.server.ServerCurrentStateBuilder;
import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.model.datapanel.DataPanelXMLGateway;
import ru.curs.showcase.util.*;

/**
 * Общий тестовый класс для мелких базовых объектов.
 * 
 * @author den
 * 
 */
public class BaseObjectsTest {
	/**
	 * Проверяет работу функции CommandResult.newSuccessResult().
	 */
	@Test
	public void testCommandResultNewSuccess() {
		CommandResult res = CommandResult.newSuccessResult();
		assertTrue(res.getSuccess());
		assertNull(res.getErrorCode());
		assertNull(res.getErrorMessage());
	}

	/**
	 * Проверяет работу функции CommandResult.newErrorResult().
	 */
	@Test
	public void testCommandResultErrorSuccess() {
		final int errorCode = 1;
		final String errorMes = "ErrorMes";
		CommandResult res = CommandResult.newErrorResult(errorCode, errorMes);
		assertFalse(res.getSuccess());
		assertEquals(errorCode, res.getErrorCode().intValue());
		assertEquals(errorMes, res.getErrorMessage());
	}

	/**
	 * Проверка работы InputStreamDuplicator.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInputStreamDuplicator() throws IOException {
		InputStream is =
			AppProps.loadUserDataToStream(String.format("%s//%s",
					DataPanelXMLGateway.DP_STORAGE_PARAM_NAME, "a.xml"));

		StreamConvertor dup = new StreamConvertor(is);
		String data = XMLUtils.xsltTransform(dup.getCopy(), null);
		checkForDP(data);

		data = XMLUtils.xsltTransform(dup.getCopy(), null);
		checkForDP(data);

		ByteArrayOutputStream outStream = dup.getOutputStream();
		checkForDPWithXMLHeader(outStream);

		data = XMLUtils.xsltTransform(StreamConvertor.outputToInputStream(outStream), null);
		checkForDP(data);

		outStream = StreamConvertor.inputToOutputStream(dup.getCopy());
		checkForDPWithXMLHeader(outStream);
	}

	private void checkForDPWithXMLHeader(final ByteArrayOutputStream outStream)
			throws UnsupportedEncodingException {
		String data;
		data = outStream.toString("UTF-8");
		assertTrue(data.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertTrue(data.endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	private void checkForDP(final String data) {
		assertTrue(data.startsWith("<" + GeneralXMLHelper.DP_TAG));
		assertTrue(data.endsWith("</" + GeneralXMLHelper.DP_TAG + ">"));
	}

	/**
	 * Проверка работы построителя ServerCurrentState.
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	@Test
	public void testServerCurrentStateBuilder() throws IOException, SQLException {
		ServerCurrentState state = ServerCurrentStateBuilder.build("fake");
		assertNotNull(state);
		assertNotNull(state.getAppVersion());
		assertNotNull(state.getJavaVersion());
		assertNotNull(state.getServerTime());
		assertNotNull(state.getSqlVersion());
	}
}
