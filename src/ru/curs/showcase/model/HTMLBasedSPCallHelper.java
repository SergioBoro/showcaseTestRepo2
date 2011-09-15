package ru.curs.showcase.model;

import java.io.InputStream;
import java.sql.SQLException;

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
	 * Возвращает индекс OUT параметра с данными элемента. Необходим только для
	 * HTML-based элементов.
	 * 
	 * @param index
	 *            TODO
	 * 
	 * @return - индекс параметра.
	 */
	public abstract int getDataParam(int index);

	@Override
	protected void prepareStdStatement() throws SQLException {
		super.prepareStdStatement();
		getStatement().registerOutParameter(getDataParam(getTemplateIndex()),
				java.sql.Types.SQLXML);
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
			return new HTMLBasedElementRawData(getElementInfo(), getContext());
		}

		try {
			try {
				prepareStdStatement();
				execute();
				Document data = getDocumentForXMLParam(getDataParam(getTemplateIndex()));
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
