package ru.curs.showcase.core.html;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsJythonGateway extends JythonQuery<String> implements ElementPartsGateway {
	private String sourceName;
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	public ElementPartsJythonGateway() {
		super(String.class);
	}

	@Override
	public DataFile<InputStream> getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		context = aContext;
		elementInfo = aElementInfo;
		runTemplateMethod();
		InputStream data = null;
		try {
			data = TextUtils.stringToStream(getResult());
		} catch (IOException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		return new DataFile<InputStream>(data, sourceName);
	}

	@Override
	protected Object execute() {
		return getProc().getRawData(context, elementInfo.getId().getString());
	}

	@Override
	protected String getJythonProcName() {
		return sourceName;
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
