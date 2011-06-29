package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.sql.SQLException;

import org.junit.Test;

import ru.curs.showcase.app.api.MessageType;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.*;
import ru.curs.showcase.app.server.ServiceLayerDataServiceImpl;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.util.*;

/**
 * Тесты для серверных исключений.
 * 
 * @author den
 * 
 */
public class ExceptionsTest extends AbstractTestBasedOnFiles {
	/**
	 * Имя несуществующей схемы.
	 */
	private static final String PHANTOM_XSD = "phantom26052011.xsd";

	/**
	 * Тест на считывание несуществующего параметра из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public final void testReadNotExistingValue() {
		AppProps.getRequiredValueByName("blabla");
	}

	/**
	 * Тест на считывание параметра в неверном формате из файла настроек.
	 * 
	 */
	@Test(expected = SettingsFilePropValueFormatException.class)
	public final void testReadWrongValue() {
		GridProps gp = new GridProps(AbstractGridFactory.GRID_DEFAULT_PROFILE);
		gp.stdReadIntGridValue("def.column.hor.align");
	}

	/**
	 * Тест на несуществующую информационную панель.
	 * 
	 */
	@Test(expected = SettingsFileOpenException.class)
	public final void testWrongDP() {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		gateway.getXML("verysecretandhidden.xml");
	}

	/**
	 * Проверка GeneralServerException, вызванного
	 * DataPanelFileNotFoundException.
	 */
	@Test
	public final void testWrongDPByServiceLayer() {
		Action action = new Action();
		action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
		action.setNavigatorActionType(NavigatorActionType.DO_NOTHING);
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("verysecretandhidden.xml");
		dpLink.setTabId("1");
		dpLink.setContext(CompositeContext.createCurrent());
		action.setDataPanelLink(dpLink);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getDataPanel(action);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(SettingsFileOpenException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertNotNull(((GeneralServerException) e).getOriginalTrace());
			assertNotNull(((GeneralServerException) e).getOriginalMessage());
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за несуществующей хранимой процедуры.
	 */
	@Test
	public final void testPhantomChartSP() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement("test2.xml", "3", "31");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getChart(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(SPNotExistsException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры c неверными параметрами.
	 */
	@Test
	public final void testWrongChartSP() {
		CompositeContext context = getTestContext2();

		DataPanelElementInfo element = getDPElement("test2.xml", "3", "33");
		final String procName = "chart_pas_wrong_param";

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getChart(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(((GeneralServerException) e).getOriginalMessage().indexOf(procName) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры, не вернувшей данные.
	 * 
	 */
	@Test
	public final void testWrongChartSPWithNoResult() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = getDPElement("test2.xml", "3", "32");

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getChart(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf(SPCallHelper.NO_RESULTSET_ERROR) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку из-за хранимой процедуры, не вернувшей данные.
	 */
	@Test
	public final void testWrongChartSPForSubmission() {

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.handleSQLSubmission("no_exist_proc", "fake_data");
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			assertTrue(((GeneralServerException) e).getOriginalMessage().indexOf("no_exist_proc") > -1);
			return;
		}
		fail();
	}

	/**
	 * Проверка на ошибку при передаче WebText с неполной информацией.
	 * 
	 */
	@Test
	public final void testWrongElement() {
		CompositeContext context = getTestContext2();
		DataPanelElementInfo element = new DataPanelElementInfo();
		element.setId("11");
		element.setType(DataPanelElementType.WEBTEXT);

		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		try {
			serviceLayer.getWebText(context, element);
		} catch (Exception e) {
			assertEquals(GeneralServerException.class, e.getClass());
			assertEquals(IncorrectElementException.class.getName(),
					((GeneralServerException) e).getOriginalExceptionClass());
			return;
		}
		fail();
	}

	/**
	 * Тест на срабатывание проверки на ввод неверного autoSelectRecordId.
	 * 
	 * @throws Exception
	 */
	@Test(expected = InconsistentSettingsFromDBException.class)
	public void testInconsistentSettings() throws Exception {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "3", "5");

		GridGateway gateway = new GridDBGateway();
		ElementRawData raw = gateway.getFactorySource(context, element);
		GridDBFactory factory = new GridDBFactory(raw, null);
		factory.build();
	}

	/**
	 * Тест проверки схемы XSD для неверного элемента.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testXSDValidateException() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "3", "6");

		WebTextGateway gateway = new WebTextDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Пытается проверить XML несуществующей пользовательской схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testUserXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(AppProps.loadResToStream("log4j.xml"), PHANTOM_XSD);
	}

	/**
	 * Пытается проверить XML несуществующей системной схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(AppProps.loadResToStream("log4j.xml"), PHANTOM_XSD);
	}

	/**
	 * Функция проверки функционала SolutionDBException.
	 */
	@Test
	public void testSolutionException() {
		SQLException exc = new SQLException(ValidateInDBException.SOL_MES_PREFIX);
		assertFalse(ValidateInDBException.isSolutionDBException(exc));
		exc =
			new SQLException(String.format("%stest1%s", ValidateInDBException.SOL_MES_PREFIX,
					ValidateInDBException.SOL_MES_SUFFIX));
		assertTrue(ValidateInDBException.isSolutionDBException(exc));
		ValidateInDBException solEx = new ValidateInDBException(exc);
		assertNotNull(solEx.getUserMessage());
		assertEquals("test1", solEx.getUserMessage().getId());
		assertEquals(MessageType.ERROR, solEx.getUserMessage().getType());
		assertEquals("Ошибка", solEx.getUserMessage().getText());
		exc =
			new SQLException(String.format("%stest2%s", ValidateInDBException.SOL_MES_PREFIX,
					ValidateInDBException.SOL_MES_SUFFIX));
		solEx = new ValidateInDBException(exc);
		assertEquals("Предупреждение", solEx.getUserMessage().getText());
	}

	/**
	 * Проверка случая, когда из БД приходит ссылка на несуществующее сообщение
	 * решения.
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public void testSolutionExceptionMesNotFound() {
		SQLException exc =
			new SQLException(String.format("%stestN%s", ValidateInDBException.SOL_MES_PREFIX,
					ValidateInDBException.SOL_MES_SUFFIX));
		new ValidateInDBException(exc);
	}

	/**
	 * Проверка обработки пользовательского исключения в БД на сервисном уровне.
	 */
	@Test
	public void testSolutionExceptionBySL() {
		SQLException exc =
			new SQLException(String.format("%stest1%s", ValidateInDBException.SOL_MES_PREFIX,
					ValidateInDBException.SOL_MES_SUFFIX));
		ValidateInDBException exc2 = new ValidateInDBException(exc);
		GeneralServerException gse = GeneralServerExceptionFactory.build(exc2);
		assertFalse(GeneralServerException.needDetailedInfo(gse));
		assertEquals("Ошибка", exc2.getUserMessage().getText());
		GeneralServerException.checkExeptionTypeAndCreateDetailedTextOfException(gse);
	}

	/**
	 * Проверка создания DBQueryException через SL.
	 */
	@Test
	public void testDBQueryExceptionBySL() {
		CompositeContext context = getTestContext1();
		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML("test.xml");
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel dp = factory.fromStream(file);
		DBQueryException dbqe =
			new DBQueryException(dp.getTabById("2").getElementInfoById("2"), context, "error");
		GeneralServerException gse = GeneralServerExceptionFactory.build(dbqe);

		final String errorMes =
			"Произошла ошибка при выполнении хранимой процедуры grid_bal. Текст ошибки: : error.";
		assertEquals(errorMes, gse.getMessage());
		assertNull(gse.getOriginalMessage());
		assertEquals(DBQueryException.class.getName(), gse.getOriginalExceptionClass());
		assertNotNull(gse.getStackTrace());
		assertEquals(MessageType.ERROR, gse.getMessageType());
		assertNotNull(gse.getContext());
		assertEquals("Ввоз, включая импорт - Всего", gse.getContext().getCompositeContext()
				.getMain());
		assertTrue(GeneralServerException.needDetailedInfo(gse));
		GeneralServerException.checkExeptionTypeAndCreateDetailedTextOfException(gse);
	}

	/**
	 * Тесты для статических функций GeneralServerException, работающих с любыми
	 * исключениями.
	 */
	@Test
	public void testGSEStaticFunctions() {
		Exception exc = new Exception();
		assertEquals(MessageType.ERROR, GeneralServerException.getMessageType(exc));
		assertEquals(ExceptionType.JAVA, GeneralServerException.getType(exc));
		assertTrue(GeneralServerException.needDetailedInfo(exc));
		assertNotNull(GeneralServerException
				.checkExeptionTypeAndCreateDetailedTextOfException(exc));
	}

	/**
	 * Проверяет на ошибку при передаче в БД "неверного" параметра userdata в
	 * sessionContext.
	 * 
	 * @throws GeneralServerException
	 */
	@Test
	public void testForUserDataToGridProcSuccessfull() throws GeneralServerException {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo dpei = new DataPanelElementInfo("1", DataPanelElementType.GRID);
		dpei.setProcName("grid_by_userdata");
		ServiceLayerDataServiceImpl serviceLayer = new ServiceLayerDataServiceImpl(TEST_SESSION);
		serviceLayer.getGrid(context, dpei, null);
	}

}
