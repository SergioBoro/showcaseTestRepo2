package ru.curs.showcase.model.html;

import java.io.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.model.jython.*;
import ru.curs.showcase.util.TextUtils;

/**
 * Шлюз к БД для загрузки частей, требуемых для построения элемента.
 * 
 * @author den
 * 
 */
public class ElementPartsJythonGateway extends JythonQuery<String> implements
		ElementSettingsGateway {

	private CompositeContext context;
	private DataPanelElementInfo elementInfo;

	public ElementPartsJythonGateway() {
		super(String.class);
	}

	@Override
	public ElementRawData getRawData(final CompositeContext aContext,
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
		return new ElementRawData(data, elementInfo, context);
	}

	@Override
	protected Object execute() {
		return getProc().getRawData(context, elementInfo.getId());
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getTemplateName();
	}

}
