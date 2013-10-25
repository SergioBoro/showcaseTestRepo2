package ru.curs.showcase.core.html.xform;

import java.io.InputStream;

import javax.naming.OperationNotSupportedException;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.*;

/**
 * Шлюз для XForms для работы с Celesta.
 * 
 * @author bogatov
 * 
 */
public class XFormCelestaGateway implements HTMLAdvGateway {

	@Override
	public String scriptTransform(final String procName,
			final XFormContext context) {
		CelestaHelper<String> helper = new CelestaHelper<String>(context,
				String.class);
		String data = context.getFormData();
		String result = helper.runPython(procName, data);
		return result;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext aContext,
			final DataPanelElementInfo aElementInfo, final ID aLinkId) {
		throw new RuntimeException(new OperationNotSupportedException(
				"Not supported yet"));
	}

	@Override
	public void uploadFile(final XFormContext aContext,
			final DataPanelElementInfo aElementInfo, final ID aLinkId,
			final DataFile<InputStream> aFile) {
		throw new RuntimeException(new OperationNotSupportedException(
				"Not supported yet"));
	}

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		HTMLGateway gateway = new HTMLCelestaGateway();
		return gateway.getRawData(aContext, aElementInfo);
	}

	@Override
	public void saveData(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String data) {
		final String elementId = elementInfo.getId().getString();
		CelestaHelper<Void> helper = new CelestaHelper<Void>(context,
				Void.class);
		helper.runPython(elementInfo.getSaveProc().getName(), elementId, data);
	}

}
