package ru.curs.showcase.model.html.xform;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.html.*;
import ru.curs.showcase.model.jython.JythonQuery;
import ru.curs.showcase.util.*;

/**
 * Шлюз для XForms для работы с Jython.
 * 
 * @author den
 * 
 */
public class XFormJythonGateway implements XFormGateway {
	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
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

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext aContext,
			final DataPanelElementInfo aElementInfo, final String aLinkId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sqlTransform(final String aProcName, final XFormContext aContext) {
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
