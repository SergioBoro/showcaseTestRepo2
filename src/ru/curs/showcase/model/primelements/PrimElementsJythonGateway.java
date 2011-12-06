package ru.curs.showcase.model.primelements;

import java.io.*;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.jython.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.MemoryResourcesError;

/**
 * Jython шлюз для основных элементов - навигатора и инф. панели.
 * 
 * @author den
 * 
 */
public class PrimElementsJythonGateway extends JythonQuery<String> implements
		PrimElementsGateway {

	private static final String JYTHON_PROC_NODATA_ERROR = "Jython процедура не вернула данные";
	private String sourceName;
	private CompositeContext context;
	private InputStream stream = null;

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext) {
		context = aContext;
		runTemplateMethod();
		try {
			stream = TextUtils.stringToStream(getResult());
		} catch (UnsupportedEncodingException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		if (stream == null) {
			throw new JythonException(JYTHON_PROC_NODATA_ERROR);
		}
		return new DataFile<InputStream>(stream, sourceName);
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final String aSourceName) {
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
	protected Object execute() {
		return getProc().getRawData(context);
	}

	public PrimElementsJythonGateway() {
		super(String.class);
	}

	@Override
	protected String getJythonProcName() {
		return sourceName;
	}

}
