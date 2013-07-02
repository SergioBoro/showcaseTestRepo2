package ru.curs.showcase.core.grid;

import java.io.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.FileIsAbsentInDBException;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз для получения настроек элемента grid c помощью выполнения Jython
 * скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridJythonGateway extends JythonQuery<JythonDTO> implements GridGateway {

	private static final String SAX_ERROR_MES = "обобщенные настройки (настройки плюс данные)";

	private GridContext context;
	private DataPanelElementInfo element;

	public GridJythonGateway() {
		super(JythonDTO.class);
	}

	@Override
	public RecordSetElementRawData getRawData(final GridContext aContext,
			final DataPanelElementInfo aElement) {
		return getRecordSetElementRawData(aContext, aElement);
	}

	@Override
	public RecordSetElementRawData getRawDataAndSettings(final GridContext aContext,
			final DataPanelElementInfo aElement) {
		return getRecordSetElementRawData(aContext, aElement);
	}

	/**
	 * Получение RecordSetElementRawData. Приоритет у данных заданных в
	 * gridsettings
	 * 
	 * @param aContext
	 * @param aElement
	 * @return
	 */
	private RecordSetElementRawData getRecordSetElementRawData(final GridContext aContext,
			final DataPanelElementInfo aElement) {
		this.context = aContext;
		this.element = aElement;

		RecordSetElementRawData rawData = new RecordSetElementRawData(aElement, aContext);
		runTemplateMethod();
		fillValidatedSettings(rawData, getResult().getSettings());
		if (rawData.getXmlDS() == null && getResult().getData() != null) {
			InputStream inData = TextUtils.stringToStream(getResult().getData());
			rawData.setXmlDS(inData);
		}
		return rawData;
	}

	protected void fillValidatedSettings(final RecordSetElementRawData rawData,
			final String settings) {
		if (settings != null) {
			InputStream inSettings = TextUtils.stringToStream(settings);

			ByteArrayOutputStream osSettings = new ByteArrayOutputStream();
			ByteArrayOutputStream osDS = new ByteArrayOutputStream();

			SimpleSAX sax =
				new SimpleSAX(inSettings, new StreamDivider(osSettings, osDS), SAX_ERROR_MES);
			sax.parse();

			InputStream isSettings = StreamConvertor.outputToInputStream(osSettings);
			String settingsSchemaName = rawData.getElementInfo().getType().getSettingsSchemaName();
			if (settingsSchemaName != null) {
				rawData.setSettings(XMLUtils
						.xsdValidateAppDataSafe(isSettings, settingsSchemaName));
			} else {
				rawData.setSettings(isSettings);
			}

			if (osDS.size() == 0) {
				rawData.setXmlDS(null);
			} else {
				rawData.setXmlDS(StreamConvertor.outputToInputStream(osDS));
			}
		}
	}

	@Override
	public OutputStreamDataFile downloadFile(final CompositeContext aContext,
			final DataPanelElementInfo aElement, final ID aLinkId, final String recordId) {
		GridJythonDownloadHelper gjdh =
			new GridJythonDownloadHelper(aContext, aElement, aLinkId, recordId);
		gjdh.runTemplateMethod();
		JythonDownloadResult jythonResult = gjdh.getResult();

		InputStream is = jythonResult.getInputStream();
		if (is == null) {
			throw new FileIsAbsentInDBException();
		}
		String fileName = jythonResult.getFileName();
		try {
			StreamConvertor dup = new StreamConvertor(is);
			ByteArrayOutputStream os = dup.getOutputStream();
			OutputStreamDataFile result = new OutputStreamDataFile(os, fileName);
			result.setEncoding(TextUtils.JDBC_ENCODING);
			return result;
		} catch (IOException e) {
			throw new ServerObjectCreateCloseException(e);
		}
	}

	@Override
	public void continueSession(final ElementSettingsGateway aSessionHolder) {
		// setConn((Connection) aSessionHolder.getSession());
		return;

	}

	@Override
	protected Object execute() {
		if (element.loadByOneProc()) {
			return getProc().getRawData(context, element.getId().getString(),
					context.getSortedColumns());
		} else {

			int firstrecord;
			int pagesize;

			if ((context.getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID)
					|| (context.getSubtype() == DataPanelElementSubType.EXT_PAGE_GRID)) {
				firstrecord = context.getLiveInfo().getFirstRecord();
				pagesize = context.getLiveInfo().getLimit();
			} else {
				firstrecord = context.getPageInfo().getFirstRecord();
				pagesize = context.getPageSize();
			}

			return getProc().getRawData(context, element.getId().getString(),
					context.getSortedColumns(), firstrecord, pagesize);
		}
	}

	@Override
	protected String getJythonProcName() {
		return element.getProcName();
	}

}
