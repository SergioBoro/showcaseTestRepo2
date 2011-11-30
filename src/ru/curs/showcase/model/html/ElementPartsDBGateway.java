package ru.curs.showcase.model.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента. Примером
 * частей являются шаблоны, трансформации, XSD схемы.
 * 
 * @author den
 * 
 */
public class ElementPartsDBGateway implements ElementPartsGateway {
	private String sourceName;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		if (isEmpty()) {
			return null;
		}
		ElementSettingsGateway gateway = new ElementSettingsDBGateway() {
			@Override
			protected String getSettingsSchema() {
				return null;
			}

			@Override
			public String getProcName() {
				return sourceName;
			}
		};

		try (ElementRawData data = gateway.getRawData(aContext, aElementInfo)) {
			data.prepareSettings();
			return new DataFile<InputStream>(data.getSettings(), sourceName);
		}
	}

	private boolean isEmpty() {
		return (sourceName == null) || sourceName.isEmpty();
	}

	@Override
	public void setSource(final String aSourceName) {
		sourceName = aSourceName;
	}

	@Override
	public void setType(final SettingsFileType aType) {
		// не используется
	}

}
