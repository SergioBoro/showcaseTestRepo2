package ru.curs.showcase.test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.chart.*;
import ru.curs.showcase.model.command.GeneralExceptionFactory;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.html.*;
import ru.curs.showcase.model.html.webtext.*;
import ru.curs.showcase.model.html.xform.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Тесты для серверных исключений.
 * 
 * @author den
 * 
 */
public class ExceptionsTest extends AbstractTestWithDefaultUserData {
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
		ProfileReader gp =
			new ProfileReader(GridServerState.GRID_DEFAULT_PROFILE,
					SettingsFileType.GRID_PROPERTIES);
		gp.init();
		gp.getIntValue("def.column.hor.align");
	}

	/**
	 * Тест на несуществующую информационную панель.
	 * 
	 */
	@Test(expected = SettingsFileOpenException.class)
	public final void testWrongDP() {
		DataPanelGateway gateway = new DataPanelFileGateway();
		gateway.getRawData(new CompositeContext(), "verysecretandhidden.xml");
	}

	/**
	 * Проверка GeneralServerException, вызванного
	 * DataPanelFileNotFoundException.
	 */
	@Test
	public final void testWrongDPByServiceLayer() {
		Action action = new Action();
		action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
		action.setContext(CompositeContext.createCurrent());
		DataPanelLink dpLink = new DataPanelLink();
		dpLink.setDataPanelId("verysecretandhidden.xml");
		dpLink.setTabId("1");
		action.setDataPanelLink(dpLink);

		try {
			DataPanelGetCommand command = new DataPanelGetCommand(action);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SettingsFileOpenException.class.getName(), e.getOriginalExceptionClass());
			assertNotNull(e.getOriginalMessage());
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
		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "31");

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SPNotExistsException.class.getName(), e.getOriginalExceptionClass());
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

		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "33");
		final String procName = "chart_pas_wrong_param";

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(DBQueryException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getOriginalMessage().indexOf(procName) > -1);
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
		DataPanelElementInfo element = getDPElement(TEST2_XML, "3", "32");

		try {
			ChartGetCommand command = new ChartGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(DBQueryException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf(CompBasedElementSPQuery.NO_RESULTSET_ERROR) > -1);
			return;
		}
		fail();
	}

	/**
	 * Тест на ошибку для несуществующей хранимой процедуру для Submission.
	 */
	@Test
	public final void testWrongChartSPForSubmission() {

		try {
			XFormContext context = new XFormContext();
			DataPanelElementInfo elInfo =
				XFormInfoFactory.generateXFormsSQLSubmissionInfo("no_exist_proc");
			XFormSQLTransformCommand command = new XFormSQLTransformCommand(context, elInfo);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(SPNotExistsException.class.getName(), e.getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf("no_exist_proc") > -1);
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

		try {
			WebTextGetCommand command = new WebTextGetCommand(context, element);
			command.execute();
		} catch (GeneralException e) {
			assertEquals(IncorrectElementException.class.getName(), e.getOriginalExceptionClass());
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
		GridContext gc = getTestGridContext1();
		DataPanelElementInfo element = getDPElement(TEST_XML, "3", "5");

		GridGateway gateway = new GridDBGateway();
		ElementRawData raw = gateway.getRawDataAndSettings(gc, element);
		GridDBFactory factory = new GridDBFactory(raw);
		factory.build();
	}

	/**
	 * Тест проверки схемы XSD для неверного элемента.
	 * 
	 */
	@Test(expected = XSDValidateException.class)
	public void testXSDValidateException() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo element = getDPElement(TEST_XML, "3", "6");

		HTMLGateway gateway = new WebTextDBGateway();
		gateway.getRawData(context, element);
	}

	/**
	 * Пытается проверить XML несуществующей пользовательской схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testUserXSDNotFoundException() {
		XMLUtils.xsdValidateUserData(FileUtils.loadResToStream(TEST_XML_FILE), PHANTOM_XSD);
	}

	/**
	 * Пытается проверить XML несуществующей системной схемой.
	 */
	@Test(expected = SettingsFileOpenException.class)
	public void testXSDNotFoundException() {
		XMLUtils.xsdValidateAppDataSafe(FileUtils.loadResToStream(TEST_XML_FILE), PHANTOM_XSD);
	}

	/**
	 * Функция проверки функционала SolutionDBException.
	 */
	@Test
	public void testSolutionException() {
		SQLException exc = new SQLException(UserMessageFactory.SOL_MES_PREFIX);
		assertFalse(UserMessageFactory.isExplicitRaised(exc));
		exc =
			new SQLException(String.format("%stest1%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		assertTrue(UserMessageFactory.isExplicitRaised(exc));
		UserMessageFactory factory = new UserMessageFactory();
		ValidateException solEx = new ValidateException(factory.build(exc));
		assertNotNull(solEx.getUserMessage());
		assertEquals("test1", solEx.getUserMessage().getId());
		assertEquals(MessageType.ERROR, solEx.getUserMessage().getType());
		assertEquals("Ошибка", solEx.getUserMessage().getText());
		exc =
			new SQLException(String.format("%stest2%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		factory = new UserMessageFactory();
		solEx = new ValidateException(factory.build(exc));
		assertEquals("Предупреждение", solEx.getUserMessage().getText());
	}

	/**
	 * Проверка случая, когда из БД приходит ссылка на несуществующее сообщение
	 * решения.
	 */
	@Test(expected = SettingsFileRequiredPropException.class)
	public void testSolutionExceptionMesNotFound() {
		SQLException exc =
			new SQLException(String.format("%stestN%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		UserMessageFactory factory = new UserMessageFactory();
		throw new ValidateException(factory.build(exc));
	}

	/**
	 * Проверка обработки пользовательского исключения в БД на сервисном уровне.
	 */
	@Test
	public void testSolutionExceptionBySL() {
		SQLException exc =
			new SQLException(String.format("%stest1%s", UserMessageFactory.SOL_MES_PREFIX,
					UserMessageFactory.SOL_MES_SUFFIX));
		UserMessageFactory factory = new UserMessageFactory();
		ValidateException exc2 = new ValidateException(factory.build(exc));
		GeneralException gse = GeneralExceptionFactory.build(exc2);
		assertFalse(GeneralException.needDetailedInfo(gse));
		assertEquals("Ошибка", exc2.getUserMessage().getText());
		GeneralException.generateDetailedInfo(gse);
	}

	/**
	 * Проверка создания DBQueryException через SL.
	 */
	@Test
	public void testDBQueryExceptionBySL() {
		CompositeContext context = getTestContext1();
		DataPanelGateway gateway = new DataPanelFileGateway();
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel dp = factory.fromStream(file);

		DBQueryException dbqe =
			new DBQueryException(dp.getTabById("2").getElementInfoById("2"), context, "error");
		GeneralException gse = GeneralExceptionFactory.build(dbqe);

		final String errorMes =
			"Произошла ошибка при выполнении хранимой процедуры grid_bal. Подробности: error.";
		assertEquals(errorMes, gse.getMessage());
		assertNull(gse.getOriginalMessage());
		assertEquals(DBQueryException.class.getName(), gse.getOriginalExceptionClass());
		assertNotNull(gse.getStackTrace());
		assertEquals(MessageType.ERROR, gse.getMessageType());
		assertNotNull(gse.getContext());
		assertEquals("Ввоз, включая импорт - Всего", gse.getContext().getCompositeContext()
				.getMain());
		assertTrue(GeneralException.needDetailedInfo(gse));
		GeneralException.generateDetailedInfo(gse);
	}

	/**
	 * Тесты для статических функций GeneralServerException, работающих с любыми
	 * исключениями.
	 */
	@Test
	public void testGSEStaticFunctions() {
		Exception exc = new Exception();
		assertEquals(MessageType.ERROR, GeneralException.getMessageType(exc));
		assertEquals(ExceptionType.JAVA, GeneralException.getType(exc));
		assertTrue(GeneralException.needDetailedInfo(exc));
		assertNotNull(GeneralException.generateDetailedInfo(exc));
	}

	/**
	 * Проверяет на отсутствие ошибки при передаче в БД "правильного" параметра
	 * userdata в sessionContext.
	 */
	@Test
	public void testForUserDataToGridProcSuccessfull() {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo dpei = new DataPanelElementInfo("1", DataPanelElementType.GRID);
		dpei.setProcName("grid_by_userdata");
		generateTestTabWithElement(dpei);

		GridGetCommand command = new GridGetCommand(context, dpei, true);
		command.execute();
	}

	/**
	 * Проверяет на ошибку при передаче в БД "неверного" параметра userdata в
	 * sessionContext.
	 * 
	 * @throws GeneralException
	 */
	@Test(expected = GeneralException.class)
	public void testForUserDataToGridProcFault() {
		GridContext context = getTestGridContext1();
		context.setSessionParamsMap(generateTestURLParamsForSL("test1"));
		DataPanelElementInfo dpei = new DataPanelElementInfo("1", DataPanelElementType.GRID);
		dpei.setProcName("grid_by_userdata");

		GridGetCommand command = new GridGetCommand(context, dpei, true);
		command.execute();
	}

	/**
	 * Проверка возврата ошибки с кодом из БД.
	 */
	@Test
	public void testReturnErrorFromDB() {
		MainPageFrameGateway gateway = new MainPageFrameDBGateway();
		CompositeContext context = getTestContext1();

		try {
			gateway.getRawData(context, "header_proc_with_error");
		} catch (ValidateException e) {
			assertEquals("Ошибка, переданная через @error_mes (1)", e.getUserMessage().getText());
			return;
		}
		fail();
	}

	/**
	 * Проверка на исключение при неверном номере вкладки инф. панели в
	 * действии.
	 */
	@Test(expected = IncorrectElementException.class)
	public void testWrongTab() {
		final int elID = 3;
		getAction("tree_multilevel.wrong.xml", 0, elID);
	}

	/**
	 * Проверка на исключение при неверном столбце сортировки в гриде.
	 */
	@Test(expected = DBQueryException.class)
	public void testDBQueryExceptionWithWrongGridSorting() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridContext gc = new GridContext();
		List<Column> aSortedColumns = new ArrayList<>();
		Column col = new Column();
		col.setId("Name111");
		col.setSorting(Sorting.ASC);
		aSortedColumns.add(col);
		gc.setSortedColumns(aSortedColumns);
		gc.apply(context);

		GridGateway gateway = new GridDBGateway();
		gateway.getRawData(gc, elInfo);
	}

	/**
	 * Проверка на исключение при попытке получить настройки элемента при
	 * загрузке только данных.
	 */
	@Test(expected = ResultSetHandleException.class)
	public void testErrorWhenGetSettingsForDataOnlyProc() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridInfo2();
		GridGateway gateway = new GridDBGateway();
		GridContext gc = new GridContext();
		gc.apply(context);
		ElementRawData res = gateway.getRawData(gc, elInfo);
		res.prepareSettings();
	}

	@Test(expected = IncorrectElementException.class)
	public void testElementActionWrong() throws Exception {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("id", DataPanelElementType.XFORMS);
		elInfo.setProcName("xforms_proc_wrong_1");
		generateTestTabWithElement(elInfo);
		XFormContext xContext = new XFormContext(getTestContext1());
		XFormGateway gateway = new XFormDBGateway();
		HTMLBasedElementRawData raw = gateway.getRawData(xContext, elInfo);
		XFormFactory factory = new XFormFactory(raw);
		factory.build();
	}

	@Test
	public void testErrorCodeReturn() {
		XFormContext context = new XFormContext();
		context.setMain(MAIN_CONTEXT_TAG);
		context.setAdditional(ADD_CONDITION);
		XFormGateway gateway = new XFormDBGateway();
		final String procName = "xforms_submission_ec";

		try {
			gateway.sqlTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Ошибка в SP (-1)", e.getMessage());
		}

		try {
			context.setFormData("<mesid>555</mesid>");
			gateway.sqlTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Отформатированное сообщение: Ошибка в SP. Спасибо!", e.getMessage());
		}

		try {
			context.setFormData("<mesid>556</mesid>");
			gateway.sqlTransform(procName, context);
		} catch (ValidateException e) {
			assertEquals("Составное сообщение + Ошибка в SP", e.getMessage());
		}
	}

	@Test
	public void testGeoMapErrorCodeReturn() {
		CompositeContext context = new CompositeContext();
		context.setMain(MAIN_CONTEXT_TAG);
		context.setAdditional("<mesid>556</mesid>");
		ChartGateway gateway = new ChartDBGateway();
		DataPanelElementInfo dpei = new DataPanelElementInfo("id", DataPanelElementType.CHART);
		dpei.setProcName("geomap_ec");
		try {
			gateway.getRawData(context, dpei);
		} catch (ValidateException e) {
			assertEquals("Составное сообщение + ", e.getMessage());
		}
	}

}
