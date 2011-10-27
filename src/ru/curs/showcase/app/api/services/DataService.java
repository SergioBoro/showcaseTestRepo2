package ru.curs.showcase.app.api.services;

import ru.curs.gwt.datagrid.model.ColumnSet;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.app.api.chart.Chart;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.app.api.navigator.Navigator;

import com.google.gwt.user.client.rpc.*;

/**
 * Основной GWT-RPC интерфейс для приложения. Основное назначение - передача
 * данных для отображения в UI.
 */
// CHECKSTYLE:OFF
@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	/**
	 * Функция, возвращающая невизуальный объект навигатора с данными для
	 * пользователя, совершившего вход в систему.
	 * 
	 * @return - объект навигатора.
	 * @throws GeneralException
	 * @param context
	 *            - контекст вызова. Содержит параметры URL.
	 */
	Navigator getNavigator(CompositeContext context) throws GeneralException;

	/**
	 * Возвращает информационную панель по переданному действию. Информационная
	 * панель формируется учитывая все контексты, переданные в действии.
	 * 
	 * @param action
	 *            - действие.
	 * @return - панель.
	 * @throws GeneralException
	 */
	DataPanel getDataPanel(Action action) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки элемента типа WebText по переданному
	 * контексту и описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - WebText.
	 * @throws GeneralException
	 */
	WebText getWebText(CompositeContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает данные для отрисовки элемента типа Grid по переданным
	 * контексту, описанию элемента и требуемым настройкам.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - грид.
	 * @throws GeneralException
	 */
	Grid getGrid(GridContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки графика по переданным контексту и
	 * описанию элемента.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - элемент.
	 * @return - chart.
	 * @throws GeneralException
	 */
	Chart getChart(CompositeContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Fake функция для того, чтобы заработала сериализация GWT для класса
	 * ColumnSet. Не удалять!
	 * 
	 * @param cs
	 *            - набор столбцов.
	 * @throws GeneralException
	 */
	void saveColumnSet(ColumnSet cs, GeoMapExportSettings settings) throws GeneralException;

	/**
	 * Возвращает данные для отрисовки карты.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - невизуальный объект карты.
	 * @throws GeneralException
	 */
	GeoMap getGeoMap(CompositeContext context, DataPanelElementInfo element)
			throws GeneralException;

	/**
	 * Возвращает данные для отрисовки формы. Передаваемые в контексте данные
	 * должны заменить данные из БД, реализуя т.об. обновление формы.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @return - логическая форма без данных.
	 * @throws GeneralException
	 */
	XForm getXForms(XFormContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Сохраняет данные карточки на основе XForms.
	 * 
	 * @param context
	 *            - контекст.
	 * @param element
	 *            - описание элемента.
	 * @throws GeneralException
	 */
	void saveXForms(XFormContext context, DataPanelElementInfo element) throws GeneralException;

	/**
	 * Возвращает информацию о текущем состоянии сервера и о текущем сеансе.
	 * 
	 * @return - объект с информацией.
	 * @throws GeneralException
	 * @param context
	 *            -контекст, содержащий параметры URL.
	 */
	ServerState getServerCurrentState(CompositeContext context) throws GeneralException;

	/**
	 * Выполняет действие на сервере.
	 * 
	 * @param action
	 *            - действие.
	 * 
	 * @throws GeneralException
	 */
	void execServerAction(Action action) throws GeneralException;

	/**
	 * Возвращает данные для формирования главной страницы.
	 * 
	 * @param context
	 *            - контекст, содержащий параметры URL.
	 * 
	 * @throws GeneralException
	 */
	MainPage getMainPage(CompositeContext context) throws GeneralException;
}
