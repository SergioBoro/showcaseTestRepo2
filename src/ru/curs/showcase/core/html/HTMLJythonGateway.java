package ru.curs.showcase.core.html;

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Jython шлюз для WebText.
 * 
 * @author den
 * 
 */
public class HTMLJythonGateway extends JythonQuery<JythonDTO> implements HTMLGateway {

	public HTMLJythonGateway() {
		super(JythonDTO.class);
	}

	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		context = aContext;
		elementInfo = aElementInfo;
		runTemplateMethod();
		Document data = null;
		InputStream settings = null;
		try {
			data = XMLUtils.stringToDocument(getResult().getData());
			if (getResult().getSettings() != null) {
				settings = TextUtils.stringToStream(getResult().getSettings());
			}
		} catch (SAXException | IOException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		HTMLBasedElementRawData rawData =
			new HTMLBasedElementRawData(data, settings, elementInfo, context);
		return rawData;
	}

	@Override
	protected Object execute() {
		return getProc().getRawData(context, elementInfo.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getProcName();
	}

}
