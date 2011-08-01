package ru.curs.showcase.model.event;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.*;

/**
 * Абстрактная фабрика с шаблонным методом построения сложных объектов -
 * наследников FormattedDataPanelElement. Используется для построения таких
 * объектов, как грид, график, карта.
 * 
 * @author den
 * 
 */
public abstract class TemplateMethodFactory extends GeneralXMLHelper {

	public TemplateMethodFactory(final ElementRawData aSource) {
		super();
		source = aSource;
	}

	protected final InputStream getSettings() {
		return source.getProperties();
	}

	public final CompositeContext getCallContext() {
		return source.getCallContext();
	}

	/**
	 * Исходные сырые данные для построения элемента.
	 */
	private ElementRawData source;

	public ElementRawData getSource() {
		return source;
	}

	public void setSource(final ElementRawData aSource) {
		source = aSource;
	}

	/**
	 * Функция, возвращающая результат работы фабрики.
	 * 
	 * @return - результат работы.
	 */
	public abstract DataPanelElement getResult();

	/**
	 * Основная функция построения объекта на основе данных, уже содержащихся в
	 * фабрике.
	 * 
	 * @return - грид.
	 */
	public DataPanelElement build() throws Exception {
		initResult();
		prepareData();
		prepareSettings();
		checkForDBError();
		releaseResources();
		setupDynamicSettings();
		fillResultByData();
		getResult().actualizeActions(getCallContext());
		correctSettingsAndData();
		return getResult();
	}

	/**
	 * Метод для проверки на возврат процедурой кода ошибки. В случае ошибки
	 * процесс построения элемента прерывается.
	 */
	protected void checkForDBError() {
		// По умолчанию ничего не делает - данный функционал не всегда нужен
	}

	/**
	 * Первый шаг в процессе построения элемента - загрузка настроек.
	 * 
	 * @return - грид.
	 */
	public DataPanelElement buildStepOne() {
		initResult();
		prepareSettings();
		setupDynamicSettings();
		return getResult();
	}

	/**
	 * Второй шаг в процессе построения элемента - загрузка данных, их обработка
	 * и постобработка настроек и данных.
	 * 
	 * @return - грид.
	 */
	public DataPanelElement buildStepTwo() throws Exception {
		prepareData();
		checkForDBError();
		releaseResources();
		fillResultByData();
		getResult().actualizeActions(getCallContext());
		correctSettingsAndData();
		return getResult();
	}

	/**
	 * Загружает динамические настройки результата из settings.
	 * 
	 */
	protected abstract void setupDynamicSettings();

	/**
	 * Метод, в котором происходит загрузка в результат динамических данных.
	 * 
	 * @throws SQLException
	 */
	protected abstract void fillResultByData() throws SQLException;

	/**
	 * Выполняет настройку свойств результата работы фабрики на основании
	 * динамических данных, загруженных в методе fillResultByData.
	 */
	protected void correctSettingsAndData() {
		// По умолчанию ничего не делает - данный функционал не всегда нужен
	}

	/**
	 * Функция для инициализации результата работы фабрики. В данной функции
	 * создается объект, а также настраиваются его свойства по умолчанию.
	 * 
	 */
	protected abstract void initResult();

	/**
	 * Функция подготовки данных для создания объекта.
	 * 
	 * @throws ResultSetHandleException
	 * @throws DBQueryException
	 */
	protected abstract void prepareData();

	/**
	 * Функция подготовки настроек для создания объекта.
	 * 
	 */
	protected abstract void prepareSettings();

	/**
	 * Функция освобождения ресурсов, необходимых для создания объекта.
	 * 
	 */
	protected abstract void releaseResources();

	public DataPanelElementInfo getElementInfo() {
		return source.getElementInfo();
	}

	/**
	 * Стандартный метод для замены строк шаблонов в элементах инф. панели.
	 * 
	 * @param in
	 *            - входная строка.
	 */
	protected String replaceVariables(final String in) {
		String out = in;
		out = out.replace(ELEMENT_ID, getElementInfo().getId());
		out = AppProps.replaceVariables(out);
		return out;
	}

}
