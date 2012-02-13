package ru.curs.showcase.test.servlets;

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.*;

import ru.curs.showcase.app.server.*;
import ru.curs.showcase.test.AbstractTest;
import ru.curs.showcase.util.TextUtils;

/**
 * Базовый класс для тестов сервлетов с помощью Mock объектов. Необходим для
 * инициализации.
 * 
 * @author den
 * 
 */
public abstract class AbstractServletTest extends AbstractTest {

	public AbstractServletTest() {
		super();
	}

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private static ServletContext servletContext;

	@Before
	public void setUp() {
		request = new MockHttpServletRequest(servletContext);
		response = new MockHttpServletResponse();
	}

	@After
	public void tearDown() {
		resetUserData();
	}

	@BeforeClass
	public static void setUpCLass() {
		servletContext = new MockServletContext("/WebContent", new FileSystemResourceLoader());
		AppInitializer.initialize();
		ProductionModeInitializer.initialize(servletContext);
	}

	/**
	 * Отключать кэш и JDBC драйвера нельзя. В первом случае потому что нет
	 * функции включения. А во втором - потому что нужно будет заново загружать.
	 */
	@AfterClass
	public static void tearDownClass() {
		JMXBeanRegistrator.unRegister();
	}

	public MockHttpServletRequest request() {
		return request;
	}

	public MockHttpServletResponse response() {
		return response;
	}

	protected void checkOkResponse(final String ctype) {
		assertEquals(HttpServletResponse.SC_OK, response().getStatus());
		assertEquals(ctype, response().getContentType());
		assertEquals(TextUtils.DEF_ENCODING, response().getCharacterEncoding());
	}

}