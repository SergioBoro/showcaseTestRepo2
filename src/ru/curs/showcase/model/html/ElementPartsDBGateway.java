package ru.curs.showcase.model.html;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента. Примером
 * частей являются шаблоны, трансформации, XSD схемы.
 * 
 * @author den
 * 
 */
public class ElementPartsDBGateway extends ElementSettingsDBGateway {
	@Override
	public ElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		try (ElementRawData data = super.getRawData(aContext, aElementInfo)) {
			data.prepareSettings();
			return data;
		}
	}

	@Override
	protected String getSettingsSchema() {
		return null;
	}

	@Override
	public String getProcName() {
		return getElementInfo().getTemplateName();
	}
}
