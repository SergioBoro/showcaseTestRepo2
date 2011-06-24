package ru.curs.showcase.test;

import static org.junit.Assert.assertTrue;

import java.io.*;

import org.junit.BeforeClass;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.navigator.Navigator;
import ru.curs.showcase.app.server.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.datapanel.*;
import ru.curs.showcase.model.navigator.NavigatorFactory;
import ru.curs.showcase.util.AppProps;

/**
 * Класс абстрактного теста, использующего тестовые файлы с данными.
 * 
 * @author den
 * 
 */
public class AbstractTestBasedOnFiles extends GeneralXMLHelper {

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	protected static final String TEST_SESSION = "testSession";
	/**
	 * Название каталога, содержащего описания навигаторов в формате XML для
	 * тестовых целей.
	 */
	static final String NAVIGATORSTORAGE = "navigatorstorage";

	/**
	 * Действия, которые должны выполняться перед запуском любых тестовых
	 * классов.
	 */
	@BeforeClass
	public static void beforeClass() {
		AppInitializer.initialize();
		if (AppInfoSingleton.getAppInfo().getUserdatas().size() == 0) {
			AppInitializer.readPathProperties();
		}
		initTestSession();
	}

	private static void initTestSession() {
		AppInfoSingleton.getAppInfo().clearSessions();
		AppInfoSingleton.getAppInfo().addSession(TEST_SESSION);
	}

	public AbstractTestBasedOnFiles() {
		super();
	}

	/**
	 * Возвращает элемент информационной панели для тестов.
	 * 
	 * @param fileName
	 *            - файл панели.
	 * @param tabID
	 *            - идентификатор вкладки.
	 * @param elID
	 *            - идентификатор элемента.
	 * @return элемент.
	 */
	protected DataPanelElementInfo getDPElement(final String fileName, final String tabID,
			final String elID) {
		DataPanelGateway gateway = new DataPanelXMLGateway();
		DataFile<InputStream> file = gateway.getXML(fileName);
		DataPanelFactory dpFactory = new DataPanelFactory();
		DataPanel panel = dpFactory.fromStream(file);
		DataPanelElementInfo element = panel.getTabById(tabID).getElementInfoById(elID);
		assertTrue(element.isCorrect());
		return element;
	}

	/**
	 * Генерирует описание грида для тестов.
	 * 
	 * @return DataPanelElementInfo
	 * 
	 */
	protected DataPanelElementInfo getTestGridInfo() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("2", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("grid_bal");
		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание графика для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestChartInfo() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("3", DataPanelElementType.CHART);
		elInfo.setPosition(2);
		elInfo.setProcName("chart_bal");
		elInfo.setHideOnLoad(true);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание xforms для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestXForms1Info() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("08", DataPanelElementType.XFORMS);
		final int position = 6;
		elInfo.setPosition(position);
		elInfo.setProcName("xforms_proc1");
		elInfo.setTemplateName("Showcase_Template.xml");

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("proc1");
		proc.setName("xforms_saveproc1");
		proc.setType(DataPanelElementProcType.SAVE);
		elInfo.getProcs().put(proc.getId(), proc);
		proc = new DataPanelElementProc();
		proc.setId("proc2");
		proc.setName("xforms_submission1");
		proc.setType(DataPanelElementProcType.SUBMISSION);
		elInfo.getProcs().put(proc.getId(), proc);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Генерирует описание xforms для тестов.
	 * 
	 * @return DataPanelElementInfo
	 */
	protected DataPanelElementInfo getTestXForms2Info() {
		DataPanelElementInfo elInfo = new DataPanelElementInfo("09", DataPanelElementType.XFORMS);
		final int position = 7;
		elInfo.setPosition(position);
		elInfo.setProcName("xforms_proc1");
		elInfo.setTemplateName("Showcase_Template.xml");

		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId("proc3");
		proc.setName("xforms_save_error_proc1");
		proc.setType(DataPanelElementProcType.SAVE);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc4");
		proc.setName("xforms_download1");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc5");
		proc.setName("xforms_upload1");
		proc.setType(DataPanelElementProcType.UPLOAD);
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc6");
		proc.setName("xforms_download2");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName("test_good_small.xsd");
		proc.setTransformName("test_good.xsl");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc7");
		proc.setName("xforms_upload1");
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName("test_good.xsd");
		proc.setTransformName("test_good.xsl");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc8");
		proc.setName("xforms_upload1");
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setSchemaName("test_bad.xsd");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc9");
		proc.setName("xforms_upload1");
		proc.setType(DataPanelElementProcType.UPLOAD);
		proc.setTransformName("test_good.xsl");
		elInfo.getProcs().put(proc.getId(), proc);

		proc = new DataPanelElementProc();
		proc.setId("proc10");
		proc.setName("xforms_download3_wrong");
		proc.setType(DataPanelElementProcType.DOWNLOAD);
		proc.setSchemaName("test_good_small.xsd");
		elInfo.getProcs().put(proc.getId(), proc);

		assertTrue(elInfo.isCorrect());
		return elInfo;
	}

	/**
	 * Возвращает контекст для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 * @throws IOException
	 */
	protected CompositeContext
			getContext(final String fileName, final int groupID, final int elID)
					throws IOException {
		CompositeContext context =
			getAction(fileName, groupID, elID).getDataPanelLink().getContext();
		return context;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext1() {
		CompositeContext context = new CompositeContext();
		context.setMain("Ввоз, включая импорт - Всего");
		return context;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext2() {
		CompositeContext context = new CompositeContext();
		context.setMain("Алтайский край");
		return context;
	}

	/**
	 * Возвращает контекст для тестов.
	 * 
	 * @return - контекст.
	 */
	protected CompositeContext getTestContext3() {
		CompositeContext context = new CompositeContext();
		context.setMain("Межрегиональный обмен - Всего");
		context.setAdditional("Алтайский край");
		return context;
	}

	/**
	 * Возвращает действие для тестов из файла навигатора.
	 * 
	 * @param groupID
	 *            - номер группы в файле.
	 * @param elID
	 *            - номер элемента в группе.
	 * @param fileName
	 *            - имя файла с навигатором.
	 * @return - контекст.
	 * @throws IOException
	 */
	protected Action getAction(final String fileName, final int groupID, final int elID)
			throws IOException {
		InputStream stream1 = AppProps.loadUserDataToStream(NAVIGATORSTORAGE + "\\" + fileName);
		NavigatorFactory navFactory = new NavigatorFactory();
		Navigator nav = navFactory.fromStream(stream1);
		Action action = nav.getGroups().get(groupID).getElements().get(elID).getAction();
		return action;
	}

}