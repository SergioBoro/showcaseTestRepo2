package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.util.XMLUtils;

/**
 * Помощник для загрузки информации об элементе инф. панели.
 * 
 * @author den
 * 
 */
public abstract class ElementSPCallHelper extends SPCallHelper {
	/**
	 * LOGGER.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(ElementSPCallHelper.class);
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

		getStatement().setString(ELEMENTID_COLUMNNAME, elementInfo.getId());
		LOGGER.info("elementInfo=" + elementInfo.toString());
	}

	/**
	 * Возвращает имя OUT параметра с настройками элемента.
	 * 
	 * @return - имя параметра.
	 */
	protected abstract String getOutSettingsParam();

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
	@Override
	protected void prepareStdStatementWithErrorMes() throws SQLException {
		super.prepareStdStatementWithErrorMes();
		setupGeneralElementParameters();
		getStatement().registerOutParameter(getOutSettingsParam(), java.sql.Types.SQLXML);
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
			SQLXML xml;
			xml = getStatement().getSQLXML(getOutSettingsParam());
			if (xml != null) {
				InputStream settings = xml.getBinaryStream();
				validatedSettings = XMLUtils.xsdValidateAppDataSafe(settings, getSettingsSchema());
			}
		}
		return validatedSettings;
	}

	protected void setValidatedSettings(final InputStream aValidatedSettings) {
		validatedSettings = aValidatedSettings;
	}

	@Override
	protected void dbExceptionHandler(final SQLException e) {
		if (ValidateInDBException.isExplicitRaised(e)) {
			throw new ValidateInDBException(e);
		} else {
			if (!checkProcExists()) {
				throw new SPNotExistsException(getProcName());
			}
			if (getElementInfo() != null) {
				throw new DBQueryException(e, getElementInfo(), getContext());
			} else {
				throw new DBQueryException(e, getProcName());
			}
		}
	}

	/**
	 * Возвращает имя схемы для проверки свойств элемента.
	 */
	private String getSettingsSchema() {
		return getGatewayType().getSettingsSchemaName();
	}
}
