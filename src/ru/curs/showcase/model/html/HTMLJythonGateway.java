package ru.curs.showcase.model.html;

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Jython шлюз для WebText.
 * 
 * @author den
 * 
 */
public class HTMLJythonGateway extends JythonQuery<JythonDTO> implements HTMLGateway {

	private static final String RESULT_FORMAT_ERROR =
		"Из Jython процедуры данные или настройки переданы в неверном формате";
	private static final String ERROR_MES = "Jython процедура не вернула данные";
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
	protected void checkErrors() {
		if (getResult() == null) {
			throw new JythonException(ERROR_MES);
		}
		super.checkErrors();
	}

	@Override
	protected void execute() {
		setResult(getProc().getRawData(context, elementInfo.getId()));
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getProcName();
	}

	@Override
	protected UserMessage getUserMessage() {
		return getResult().getUserMessage();
	}

}
