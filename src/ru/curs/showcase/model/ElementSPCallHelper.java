package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.*;

import org.slf4j.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.DBQueryException;
import ru.curs.showcase.util.XMLUtils;

/**
 * Помощник для загрузки информации об элементе инф. панели.
 * 
 * @author den
 * 
 */
public abstract class ElementSPCallHelper extends SPCallHelper {
	private static final String ELEMENTID_COLUMNNAME = "element_id";
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ElementSPCallHelper.class);
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
	private void setupGeneralElementParameters() throws SQLException {
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
	protected void handleDBQueryException(final SQLException e) {
		throw new DBQueryException(e, getProcName(), new DataPanelElementContext(getContext(),
				getElementInfo()));
	}

	/**
	 * Возвращает имя схемы для проверки свойств элемента.
	 */
	private String getSettingsSchema() {
		return getGatewayType().getSettingsSchemaName();
	}
}
