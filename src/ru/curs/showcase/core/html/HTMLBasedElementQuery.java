package ru.curs.showcase.core.html;

import java.io.InputStream;
import java.sql.SQLException;

import org.w3c.dom.Document;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.sp.*;

/**
 * Вспомогательный класс для работы с хранимыми процедурами получения данных для
 * построения основанных на HTML элементах.
 * 
 * @author den
 * 
 */
public abstract class HTMLBasedElementQuery extends ElementSPQuery {
	/**
	 * Возвращает индекс OUT параметра с данными элемента. Необходим только для
	 * HTML-based элементов.
	 * 
	 * @return - индекс параметра.
	 */
	public abstract int getDataParam();

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
		if (getProcName() == null) {
			return new HTMLBasedElementRawData(getElementInfo(), getContext());
		}

		try (SPQuery query = this) {
			try {
				prepareStdStatement();
				execute();
				Document data = getDocumentForXMLParam(getDataParam());
				InputStream validatedSettings = getValidatedSettings();
				return new HTMLBasedElementRawData(data, validatedSettings, getElementInfo(),
						getContext());
			} catch (SQLException e) {
				throw dbExceptionHandler(e);
			}
		}
	}

}
