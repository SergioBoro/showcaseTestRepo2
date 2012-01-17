package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.transform.*;

import org.junit.Test;

import ru.curs.showcase.runtime.*;

/**
 * Модуль для тестирования пулов: соединений, трансформаций...
 * 
 * @author den
 * 
 */
public class PoolsTest extends AbstractTestWithDefaultUserData {

	private static final String BAL_XSL = "bal.xsl";
	private static final String PAS_XSL = "pas.xsl";

	@Test
	public void testXSLTransformerPool() throws TransformerConfigurationException, IOException {
		checkPool(XSLTransformerFactory.getInstance());
	}

	@Test
	public void testXSLTransformerPoolByFile() throws TransformerConfigurationException,
			IOException {
		XSLTransformerFactory factory = XSLTransformerFactory.getInstance();
		factory.clear();
		Transformer xf = factory.acquire(PAS_XSL);
		factory.release(xf, PAS_XSL);
		Transformer xf2 = factory.acquire(BAL_XSL);
		assertNotSame(xf, xf2);
		factory.release(xf2, BAL_XSL);
		assertEquals(2, factory.getAllCount());
		Transformer xf3 = factory.acquire(PAS_XSL);
		assertEquals(xf, xf3);
		Transformer xf4 = factory.acquire(BAL_XSL);
		assertEquals(xf2, xf4);
		factory.release(xf3, PAS_XSL);
		factory.release(xf4, BAL_XSL);
	}

	@Test(expected = IOException.class)
	public void testXSLTransformationPoolWrongFile() throws TransformerConfigurationException,
			IOException {
		XSLTransformerFactory factory = XSLTransformerFactory.getInstance();
		factory.acquire("fake_trans.xsl");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkPool(final Pool pool) throws TransformerConfigurationException, IOException {
		assertNotNull(pool);
		pool.clear();
		assertEquals(0, pool.getAllCount());
		Object tr1 = pool.acquire();
		Object tr2 = pool.acquire();
		assertNotNull(tr1);
		assertNotNull(tr2);
		assertNotSame(tr1, tr2);
		assertEquals(0, pool.getAllCount());
		pool.release(tr1);
		assertEquals(1, pool.getAllCount());
		pool.release(tr2);
		assertEquals(2, pool.getAllCount());
		Object tr3 = pool.acquire();
		assertEquals(tr1, tr3);
		Object tr4 = pool.acquire();
		assertEquals(tr2, tr4);
		Object tr5 = pool.acquire();
		assertNotSame(tr1, tr5);
	}

	@Test
	public void testJythonInterpreterPool() throws TransformerConfigurationException, IOException {
		checkPool(JythonIterpretatorFactory.getInstance());
	}

	@Test
	public void testJythonInterpreterPoolWith2Userdatas()
			throws TransformerConfigurationException, IOException {
		checkUserdatasPool(JythonIterpretatorFactory.getInstance());
	}

	@SuppressWarnings("unchecked")
	private void checkUserdatasPool(@SuppressWarnings("rawtypes") final Pool pool)
			throws TransformerConfigurationException, IOException {
		Object pi = pool.acquire();
		pool.release(pi);
		AppInfoSingleton.getAppInfo().setCurUserDataId(TEST1_USERDATA);
		Object pi2 = pool.acquire();
		assertNotSame(pi, pi2);
		pool.release(pi2);
	}

	@Test
	public void testDBConnectionPool() throws TransformerConfigurationException, IOException {
		checkPool(ConnectionFactory.getInstance());
	}

	@Test
	public void testDBConnectionPoolWith2Userdatas() throws TransformerConfigurationException,
			IOException {
		checkUserdatasPool(ConnectionFactory.getInstance());
	}
}
