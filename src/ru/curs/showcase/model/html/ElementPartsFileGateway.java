package ru.curs.showcase.model.html;

import java.io.IOException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.exception.*;

/**
 * Шлюз к файлу для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsFileGateway implements ElementSettingsGateway {

	@Override
	public ElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		String file = String.format("%s/%s", AppProps.XFORMS_DIR, elementInfo.getTemplateName());
		try {
			return new ElementRawData(AppProps.loadUserDataToStream(file), elementInfo, context);
		} catch (IOException e) {
			throw new SettingsFileOpenException(e, elementInfo.getTemplateName(),
					SettingsFileType.XFORM);
		}
	}

}
