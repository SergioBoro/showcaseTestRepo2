package ru.curs.showcase.test.servlets;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.*;

import ru.curs.showcase.app.server.StateServlet;
import ru.curs.showcase.test.util.BaseObjectsTest;

/**
 * Тесты для StateServlet.
 * 
 * @author den
 * 
 */
public class StateServletTest extends AbstractServletTest {

	private StateServlet servlet;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		servlet = new StateServlet();
	}

	@Test
	public void testDoGet() throws ServletException, IOException {
		request().addHeader("User-Agent", BaseObjectsTest.FIREFOX_UA);
		servlet.doGet(request(), response());

		checkOkResponse("text/xml");

		assertTrue(response().getContentAsString().contains("<isNativeUser>false</isNativeUser>"));
		assertTrue(response().getContentAsString().contains("<browserType>FIREFOX</browserType>"));
		assertTrue(response().getContentAsString().contains(
				"<javaVersion>" + System.getProperty("java.version") + "</javaVersion>"));
	}

}
