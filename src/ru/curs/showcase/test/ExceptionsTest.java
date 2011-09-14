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
import ru.curs.showcase.model.chart.ChartGetCommand;
import ru.curs.showcase.model.command.GeneralServerExceptionFactory;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.frame.*;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.model.webtext.*;
import ru.curs.showcase.model.xform.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;
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
		GridProps gp = new GridProps(GridServerState.GRID_DEFAULT_PROFILE);
		gp.stdReadIntGridValue("def.column.hor.align");
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(SettingsFileOpenException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
			assertNotNull(((GeneralException) e).getOriginalTrace());
			assertNotNull(((GeneralException) e).getOriginalMessage());
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(SPNotExistsException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
			assertTrue(((GeneralException) e).getOriginalMessage().indexOf(procName) > -1);
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(DBQueryException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
			assertTrue(e.getMessage().indexOf(CompBasedElementSPCallHelper.NO_RESULTSET_ERROR) > -1);
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(SPNotExistsException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
			assertTrue(((GeneralException) e).getMessage().indexOf("no_exist_proc") > -1);
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
		} catch (Exception e) {
			assertEquals(GeneralException.class, e.getClass());
			assertEquals(IncorrectElementException.class.getName(),
					((GeneralException) e).getOriginalExceptionClass());
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
		assertFalse(ValidateInDBException.isExplicitRaised(exc));
		exc =
			new SQLException(String.format("%stest1%s", ValidateInDBException.SOL_MES_PREFIX,
					ValidateInDBException.SOL_MES_SUFFIX));
		assertTrue(ValidateInDBException.isExplicitRaised(exc));
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
		throw new ValidateInDBException(exc);
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
		GeneralException gse = GeneralServerExceptionFactory.build(exc2);
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
		GeneralException gse = GeneralServerExceptionFactory.build(dbqe);

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
	 * 
	 * @throws GeneralException
	 */
	@Test
	public void testForUserDataToGridProcSuccessfull() throws GeneralException {
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
	public void testForUserDataToGridProcFault() throws GeneralException {
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
		} catch (Exception e) {
			assertEquals(ValidateInDBException.class, e.getClass());
			assertEquals("Ошибка, переданная через @error_mes (1)", ((ValidateInDBException) e)
					.getUserMessage().getText());
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
		Collection<Column> aSortedColumns = new ArrayList<Column>();
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
}
