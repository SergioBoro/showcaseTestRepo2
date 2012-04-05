/**
 * 
 */
package ru.curs.showcase.app.client;

import java.util.*;

import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.datapanel.DataPanel;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * Текущий контекст приложения (клиентской части) - СИНГЛЕТОН.
 * 
 * @author anlug
 * 
 */
public final class AppCurrContext extends ActionTransformer {

	/**
	 * Список id элементов, для которых были добавлены js и css из внешних
	 * файлов в DOM-модель главной страницы index.jsp (например, элементу
	 * информационной панели Plugin для работы внешнего компонента FleshD
	 * требуется подключить внешние js и css файлы). Данная переменная
	 * необходима для исключения дублирования кода в index.jsp
	 */
	private static List<String> listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel =
		new ArrayList<String>();

	/**
	 * Переменная хранящая в структуре Map закэшированные элементы
	 * BasicElementPanelBasis, которые "закэшированы".
	 */
	private static HashMap<String, BasicElementPanelBasis> mapOfDataPanelElementsToBeCached =
		new HashMap<String, BasicElementPanelBasis>();

	/**
	 * @return the mapOfDataPanelElements
	 */
	public HashMap<String, BasicElementPanelBasis> getMapOfDataPanelElements() {
		return mapOfDataPanelElementsToBeCached;
	}

	/**
	 * @param amapOfDataPanelElements
	 *            the mapOfDataPanelElements to set
	 */
	public void setMapOfDataPanelElements(
			final HashMap<String, BasicElementPanelBasis> amapOfDataPanelElements) {
		AppCurrContext.mapOfDataPanelElementsToBeCached = amapOfDataPanelElements;
	}

	/**
	 * Переменная хранящая объект MainPage приложения (в нем находится
	 * информация о главной странице, например высота заголовка и пр.).
	 */
	private static MainPage mainPage;

	/**
	 * Переменная хранящая текущее состояние приложение (версия, текущее имя
	 * пользователя и т.п.).
	 */
	private static ServerState serverCurrentState;

	/**
	 * Синглетон клиентской части приложения.
	 */
	private static AppCurrContext appCurrContext;

	/**
	 * Содержит текущий открытый ProgressWindow (окно прогресса). Если свойство
	 * равно null, то нет открытого окна прогресса.
	 */
	private static ProgressWindow progressWindow;

	/**
	 * MainPanel для текущей сессии.
	 */
	private static MainPanel mainPanel;

	/**
	 * Переменная которая хранит в себе ссылку на текущее открытое окно (скорее
	 * всего модальное) с элементами информационной панели. Если ссылка равно
	 * null то активного открытого окна не открыто.
	 */
	private static WindowWithDataPanelElement currentOpenWindowWithDataPanelElement;

	/**
	 * Переменная регистрации обработчика onSelect на TabPanel.
	 */
	private HandlerRegistration regTabPanelSelectionHandler;

	/**
	 * @return the regTabPanelSelectionHandler
	 */
	public HandlerRegistration getRegTabPanelSelectionHandler() {
		return regTabPanelSelectionHandler;
	}

	/**
	 * @param aregTabPanelSelectionHandler
	 *            the regTabPanelSelectionHandler to set
	 */
	public void setRegTabPanelSelectionHandler(
			final HandlerRegistration aregTabPanelSelectionHandler) {
		this.regTabPanelSelectionHandler = aregTabPanelSelectionHandler;
	}

	/**
	 * Метаданные информационной панели.
	 */
	private static DataPanel dataPanelMetaData;

	/**
	 * Коллекция объектов, которая связывает UI элементы DataPanel (вкладки и
	 * элементы на вкладках) c метаданными.
	 */
	private static List<UIDataPanelTab> uiDataPanel = new ArrayList<UIDataPanelTab>();

	/**
	 * @return the uiWidgetsAndData
	 */
	public List<UIDataPanelTab> getUiDataPanel() {
		return uiDataPanel;
	}

	/**
	 * @param auiDataPanel
	 *            the List<UIDataPanelTab> to set
	 */
	public void setUiDataPanel(final List<UIDataPanelTab> auiDataPanel) {
		uiDataPanel = auiDataPanel;
	}

	private AppCurrContext() {
		super();
	}

	/**
	 * @return возвращает синглетон AppCurrContext.
	 */
	public static AppCurrContext getInstance() {
		if (appCurrContext == null) {
			appCurrContext = new AppCurrContext();
			AppCurrContext.currentOpenWindowWithDataPanelElement = null;
			AppCurrContext.serverCurrentState = null;
			AppCurrContext.mainPage = null;
		}
		return appCurrContext;
	}

	/**
	 * @param acurrentOpenWindowWithDataPanelElement
	 *            the currentOpenWindowWithDataPanelElement to set
	 */
	public void setCurrentOpenWindowWithDataPanelElement(
			final WindowWithDataPanelElement acurrentOpenWindowWithDataPanelElement) {
		AppCurrContext.currentOpenWindowWithDataPanelElement =
			acurrentOpenWindowWithDataPanelElement;
	}

	/**
	 * @return the currentOpenWindowWithDataPanelElement
	 */
	public WindowWithDataPanelElement getCurrentOpenWindowWithDataPanelElement() {
		return currentOpenWindowWithDataPanelElement;
	}

	/**
	 * @param adataPanelMetaData
	 *            the dataPanelMetaData to set
	 */
	public void setDataPanelMetaData(final DataPanel adataPanelMetaData) {
		dataPanelMetaData = adataPanelMetaData;
	}

	/**
	 * @return the dataPanelMetaData
	 */
	public DataPanel getDataPanelMetaData() {
		return dataPanelMetaData;
	}

	/**
	 * @param aserverCurrentState
	 *            the serverCurrentState to set
	 */
	public void setServerCurrentState(final ServerState aserverCurrentState) {
		serverCurrentState = aserverCurrentState;
	}

	/**
	 * @return the serverCurrentState
	 */
	public ServerState getServerCurrentState() {
		return serverCurrentState;
	}

	/**
	 * @param amainPanel
	 *            the mainPanel to set
	 */
	public void setMainPanel(final MainPanel amainPanel) {
		AppCurrContext.mainPanel = amainPanel;
	}

	/**
	 * @return the mainPanel
	 */
	public MainPanel getMainPanel() {
		return mainPanel;
	}

	/**
	 * @param aprogressWindow
	 *            the progressWindow to set
	 */
	public void setProgressWindow(final ProgressWindow aprogressWindow) {
		AppCurrContext.progressWindow = aprogressWindow;
	}

	/**
	 * @return the progressWindow
	 */
	public ProgressWindow getProgressWindow() {
		return progressWindow;
	}

	/**
	 * @param amainPage
	 *            the mainPage to set
	 */
	public void setMainPage(final MainPage amainPage) {
		AppCurrContext.mainPage = amainPage;
	}

	/**
	 * @return the mainPage
	 */
	public MainPage getMainPage() {
		return mainPage;
	}

	/**
	 * @return the listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 */
	public List<String> getListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel() {
		return listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel;
	}

	/**
	 * @param listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 *            the listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel
	 *            to set
	 */
	public void setListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel(
			final List<String> aListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel) {
		AppCurrContext.listOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel =
			aListOfElementsIdWhichAlreadyAddSomeJSFileandCSSToDomModel;
	}

}
