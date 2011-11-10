package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Помощник для загрузки информации об элементе инф. панели.
 * 
 * @author den
 * 
 */
public abstract class ElementSPCallHelper extends SPCallHelper {

	private static final int ELEMENTID_INDEX = 6;

	/**
	 * Информация об элементе, данные которого загружает процедура.
	 */
	private DataPanelElementInfo elementInfo = null;

	/**
	 * Проверенные (XSD) настройки элемента.
	 */
	private InputStream validatedSettings;

	/**
	 * Функция для настройки общих параметров запроса: контекста и фильтров.
	 * 
	 * @throws SQLException
	 */
	protected void setupGeneralElementParameters() throws SQLException {
		setupGeneralParameters();

		setStringParam(getElementIdIndex(getTemplateIndex()), elementInfo.getId());
	}

	/**
	 * Возвращает индекс OUT параметра с настройками элемента.
	 * 
	 * @return - индекс параметра.
	 */
	protected abstract int getOutSettingsParam();

	/**
	 * Стандартная функция подготовки CallableStatement для выборки данных для
	 * элемента информационной панели.
	 * 
	 * @throws SQLException
	 * 
	 */
	protected void prepareStdStatement() throws SQLException {
		prepareSQL();
		setupGeneralElementParameters();
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
	}

	/**
	 * Новая функция подготовки CallableStatement - с возвратом кода ошибки.
	 */
	protected void prepareElementStatementWithErrorMes() throws SQLException {
		super.prepareStatementWithErrorMes();
		setupGeneralElementParameters();
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
	}

	/**
	 * Определяет, нужно ли загружать данные и метаданные отдельно.
	 */
	protected boolean separatedMetadataLoad() {
		return elementInfo.getMetadataProc() != null;
	}

	/**
	 * Стандартная процедура инициализации.
	 * 
	 * @param aContext
	 *            - контекст.
	 * @param aElementInfo
	 *            - инф. об элементе.
	 */
	protected void init(final CompositeContext aContext, final DataPanelElementInfo aElementInfo) {
		elementInfo = aElementInfo;
		check(elementInfo);
		setContext(aContext);
		setProcName(elementInfo.getProcName());
	}

	/**
	 * Возвращает поток с настройками элемента, который можно использовать с SAX
	 * парсере.
	 */
	protected InputStream getValidatedSettings() throws SQLException {
		if (validatedSettings == null) {
			InputStream settings = getInputStreamForXMLParam(getOutSettingsParam());
			if (settings != null) {
				validatedSettings = XMLUtils.xsdValidateAppDataSafe(settings, getSettingsSchema());
			}
		}
		return validatedSettings;
	}

	protected void setValidatedSettings(final InputStream aValidatedSettings) {
		validatedSettings = aValidatedSettings;
	}

	@Override
	protected void handleDBQueryException(final SQLException e) {
		throw new DBQueryException(e, getProcName(), new DataPanelElementContext(getContext(),
				getElementInfo()), getClass());
	}

	/**
	 * Возвращает имя схемы для проверки свойств элемента.
	 */
	private String getSettingsSchema() {
		return getElementType().getSettingsSchemaName();
	}

	protected int getElementIdIndex(final int index) {
		return ELEMENTID_INDEX;
	}

	protected int getBinarySQLType() {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return java.sql.Types.BLOB;
		} else {
			return java.sql.Types.BINARY;
		}
	}

}
