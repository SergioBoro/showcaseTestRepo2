package ru.curs.showcase.core.html.xform;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.core.jython.JythonQuery;
import ru.curs.showcase.util.*;

/**
 * Шлюз для XForms для работы с Jython. Некоторые функции - работа с файлами -
 * пока не реализованы.
 * 
 * @author den
 * 
 */
public class XFormJythonGateway implements XFormGateway {
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
	private String procName;
	private String data;

	/**
	 * Класс Jython шлюза для сохранения данных XForm.
	 * 
	 * @author den
	 * 
	 */
	class XFormSaveJythonGateway extends JythonQuery<Void> {

		@Override
		protected Object execute() {
			return getProc().save(context, elementInfo.getId(), data);
		}

		@Override
		protected String getJythonProcName() {
			return elementInfo.getSaveProc().getName();
		}

		public XFormSaveJythonGateway() {
			super(Void.class);
		}
	}

	/**
	 * Класс Jython шлюза для сохранения данных XForm.
	 * 
	 * @author den
	 * 
	 */
	class XFormTransformJythonGateway extends JythonQuery<String> {

		@Override
		protected Object execute() {
			return getProc().transform(context, data);
		}

		@Override
		protected String getJythonProcName() {
			return procName;
		}

		public XFormTransformJythonGateway() {
			super(String.class);
		}
	}

	@Override
	public String scriptTransform(final String aProcName, final XFormContext aContext) {
		context = aContext;
		procName = aProcName;
		data = aContext.getFormData();
		XFormTransformJythonGateway gateway = new XFormTransformJythonGateway();
		gateway.runTemplateMethod();
		return gateway.getResult();
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext aContext,
			final DataPanelElementInfo aElementInfo, final String aLinkId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void uploadFile(final XFormContext aContext, final DataPanelElementInfo aElementInfo,
			final String aLinkId, final DataFile<InputStream> aFile) {
		// TODO Auto-generated method stub
	}

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElementInfo) {
		HTMLGateway gateway = new HTMLJythonGateway();
		return gateway.getRawData(aContext, aElementInfo);
	}

	@Override
	public void saveData(final CompositeContext aContext, final DataPanelElementInfo aElementInfo,
			final String aData) {
		context = aContext;
		elementInfo = aElementInfo;
		data = aData;
		XFormSaveJythonGateway gateway = new XFormSaveJythonGateway();
		gateway.runTemplateMethod();
	}

}
