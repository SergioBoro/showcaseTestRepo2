package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.*;

import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Вспомогательный класс для работы с хранимыми процедурами получения данных для
 * построения основанных на HTML элементах.
 * 
 * @author den
 * 
 */
public abstract class HTMLBasedSPCallHelper extends ElementSPCallHelper {
	/**
	 * Возвращает имя OUT параметра с данными элемента. Необходим только для
	 * HTML-based элементов.
	 * 
	 * @return - имя параметра.
	 */
	public abstract String getDataParam();

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();
		getStatement().registerOutParameter(getDataParam(), java.sql.Types.SQLXML);
	}

	/**
	 * Стандартный метод возврата данных.
	 * 
	 * @param context
	 *            - контекст.
	 * @param elementInfo
	 *            - инф. об элементе.
	 */
	protected HTMLBasedElementRawData stdGetData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		if (getElementInfo().getProcName() == null) {
			return new HTMLBasedElementRawData(getElementInfo());
		}

		try {
			try {
				prepareStdStatement();
				Document data = null;
				getStatement().execute();
				SQLXML xml = getStatement().getSQLXML(getDataParam());
				if (xml != null) {
					DOMSource domSource = xml.getSource(DOMSource.class);
					data = (Document) domSource.getNode();
				}
				InputStream validatedSettings = getValidatedSettings();
				return new HTMLBasedElementRawData(data, validatedSettings, getElementInfo(),
						getContext());
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return null;
	}

}
