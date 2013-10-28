package ru.curs.showcase.core.grid;

import java.io.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.celesta.CelestaHelper;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.core.sp.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.ServerObjectCreateCloseException;
import ru.curs.showcase.util.xml.*;

/**
 * Шлюз для получения настроек элемента grid, где источником данных является
 * celesta скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridCelestaGateway implements GridGateway {

	private static final String SAX_ERROR_MES = "обобщенные настройки (настройки плюс данные)";
	private static final String NO_DOWNLOAD_PROC_ERROR = "Не задана процедура скачивания файлов с сервера для linkId=";

	@Override
	public RecordSetElementRawData getRawData(final GridContext aContext,
			final DataPanelElementInfo aElement) {
		return getRecordSetElementRawData(aContext, aElement);
	}

	@Override
	public RecordSetElementRawData getRawDataAndSettings(
			final GridContext aContext, final DataPanelElementInfo aElement) {
		return getRecordSetElementRawData(aContext, aElement);
	}

	/**
	 * Получение RecordSetElementRawData. Приоритет у данных заданных в
	 * gridsettings
	 */
	private RecordSetElementRawData getRecordSetElementRawData(
			final GridContext context, final DataPanelElementInfo element) {
		CelestaHelper<JythonDTO> helper = new CelestaHelper<JythonDTO>(context,
				JythonDTO.class);
		String procName = element.getProcName();
		JythonDTO result;
		if (element.loadByOneProc()) {
			result = helper.runPython(procName, element.getId().getString(),
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
			result = helper.runPython(procName, element.getId().getString(),
					context.getSortedColumns(), firstrecord, pagesize);
		}

		RecordSetElementRawData rawData = new RecordSetElementRawData(element,
				context);
		fillValidatedSettings(rawData, result.getSettings());
		if (rawData.getXmlDS() == null && result.getData() != null) {
			InputStream inData = TextUtils.stringToStream(result.getData());
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

			SimpleSAX sax = new SimpleSAX(inSettings, new StreamDivider(
					osSettings, osDS), SAX_ERROR_MES);
			sax.parse();

			InputStream isSettings = StreamConvertor
					.outputToInputStream(osSettings);
			String settingsSchemaName = rawData.getElementInfo().getType()
					.getSettingsSchemaName();
			if (settingsSchemaName != null) {
				rawData.setSettings(XMLUtils.xsdValidateAppDataSafe(isSettings,
						settingsSchemaName));
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
	public OutputStreamDataFile downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final ID aLinkId,
			final String recordId) {
		CelestaHelper<JythonDownloadResult> helper = new CelestaHelper<JythonDownloadResult>(
				context, JythonDownloadResult.class);
		DataPanelElementProc proc = elementInfo.getProcs().get(aLinkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR
					+ aLinkId);
		}
		JythonDownloadResult jythonResult = helper.runPython(proc.getName(),
				elementInfo.getId().getString(), recordId);

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
		return;
	}

}
