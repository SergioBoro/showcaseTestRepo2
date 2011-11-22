package ru.curs.showcase.model.navigator;

import java.io.*;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.jython.*;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.MemoryResourcesError;

/**
 * Jython шлюз для навигатора.
 * 
 * @author den
 * 
 */
public class NavigatorJythonGateway extends JythonQuery<JythonDTO> implements
		PrimaryElementsGateway {

	private static final String JYTHON_PROC_NODATA_ERROR = "Jython процедура не вернула данные";
	private String sourceName;
	private CompositeContext context;
	private InputStream stream = null;

	@Override
	public InputStream getRawData(final CompositeContext aContext) {
		context = aContext;
		runTemplateMethod();
		try {
			stream = TextUtils.stringToStream(getResult().getData());
		} catch (UnsupportedEncodingException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		if (stream == null) {
			throw new JythonException(JYTHON_PROC_NODATA_ERROR);
		}
		return stream;
	}

	@Override
	public InputStream getRawData(final CompositeContext aContext, final String aSourceName) {
		sourceName = aSourceName;
		return getRawData(aContext);
	}

	@Override
	public void close() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			throw new MemoryResourcesError(e);
		}
	}

	@Override
	public void setSourceName(final String aSourceName) {
		sourceName = aSourceName;
	}

	@Override
	protected void execute() {
		setResult(getProc().getRawData(context));
	}

	@Override
	protected String getJythonProcName() {
		return sourceName;
	}

	@Override
	protected UserMessage getUserMessage() {
		return getResult().getUserMessage();
	}

}
