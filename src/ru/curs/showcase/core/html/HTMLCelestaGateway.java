package ru.curs.showcase.core.html;

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.celesta.*;
import ru.curs.showcase.core.jython.JythonDTO;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Шлюз для WebText и xforms, источник данных для которого является Celesta.
 * 
 * @author bogatov
 * 
 */
public class HTMLCelestaGateway implements HTMLGateway {

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		final String elementId = elementInfo.getId().getString();
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context,
				JythonDTO.class);
		JythonDTO result = helper.runPython(elementInfo.getProcName(),
				elementId);
		if (result != null) {
			Document data = null;
			InputStream settings = null;
			try {
				data = XMLUtils.stringToDocument(result.getData());
			} catch (SAXException | IOException e) {
				throw new CelestaWorkerException("Error parse result");
			}
			if (result.getSettings() != null) {
				settings = TextUtils.stringToStream(result.getSettings());
			}
			return new HTMLBasedElementRawData(data, settings, elementInfo,
					context);
		}
		return null;
	}

}
