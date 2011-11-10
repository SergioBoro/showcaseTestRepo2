package ru.curs.showcase.model.xform;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.CreateObjectError;

/**
 * Шлюз к БД для получения XForms.
 * 
 * @author den
 * 
 */
@Description(process = "Загрузка данных для XForm из БД")
public final class XFormDBGateway extends HTMLBasedSPCallHelper implements XFormGateway {

	private static final String NO_UPLOAD_PROC_ERROR =
		"Не задана процедура для загрузки файлов на сервер для linkId=";
	private static final String NO_SAVE_PROC_ERROR = "Не задана процедура для сохранения XForms";
	private static final String NO_DOWNLOAD_PROC_ERROR =
		"Не задана процедура для скачивания файлов из сервера для linkId=";

	private static final int XFORMSDATA_INDEX = 7;

	private static final int OUTPUT_INDEX = 8;

	private static final int ERROR_MES_INDEX_SAVE = 8;
	private static final int ERROR_MES_INDEX_SUBMISSION = 4;
	private static final int ERROR_MES_INDEX_FILE = 10;

	private static final int FILENAME_INDEX = 8;
	private static final int FILE_INDEX = 9;

	private static final int INPUTDATA_INDEX = 2;
	private static final int OUTPUTDATA_INDEX = 3;

	private static final int SAVE_TEMPLATE_IND = 1;
	private static final int SUBMISSION_TEMPLATE_IND = 2;
	private static final int FILE_TEMPLATE_IND = 3;

	@Override
	public HTMLBasedElementRawData getRawData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public int getOutSettingsParam() {
		return OUTPUT_INDEX;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case 0:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?)}";
		case SAVE_TEMPLATE_IND:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?)}";
		case SUBMISSION_TEMPLATE_IND:
			return "{? = call %s(?, ?, ?)}";
		case FILE_TEMPLATE_IND:
			return "{? = call %s(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
		default:
			return null;
		}
	}

	@Override
	protected DataPanelElementType getElementType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) {
		init(context, elementInfo);
		setTemplateIndex(SAVE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getSaveProc();
		if (proc == null) {
			throw new IncorrectElementException(NO_SAVE_PROC_ERROR);
		}
		setProcName(proc.getName());

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), data);
				execute();
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
	}

	@Override
	public String sqlTransform(final String aProcName, final XFormContext context) {
		String out = null;
		setProcName(aProcName);
		setContext(context);
		setTemplateIndex(SUBMISSION_TEMPLATE_IND);

		try {
			try {
				prepareStatementWithErrorMes();
				setSQLXMLParam(INPUTDATA_INDEX, context.getFormData());
				getStatement().registerOutParameter(OUTPUTDATA_INDEX, java.sql.Types.SQLXML);
				execute();
				out = getStringForXMLParam(OUTPUTDATA_INDEX);
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return out;
	}

	@Override
	public int getDataParam() {
		return XFORMSDATA_INDEX;
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final String linkId) {
		init(context, elementInfo);
		setTemplateIndex(FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_DOWNLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());
		OutputStreamDataFile result = null;

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), context.getFormData());
				getStatement().registerOutParameter(FILENAME_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_INDEX, getBinarySQLType());
				execute();
				result = getFileForBinaryStream(FILE_INDEX, FILENAME_INDEX);
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return result;
	}

	@Override
	public void uploadFile(final XFormContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final DataFile<InputStream> file) {
		init(context, elementInfo);
		setTemplateIndex(FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(NO_UPLOAD_PROC_ERROR + linkId);
		}
		setProcName(proc.getName());

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(), context.getFormData());
				setStringParam(FILENAME_INDEX, file.getName());
				setBinaryStream(FILE_INDEX, file);
				execute();
			} catch (SQLException e) {
				dbExceptionHandler(e);
			} catch (IOException e2) {
				throw new CreateObjectError(e2);
			}
		} finally {
			releaseResources();
		}
	}

	@Override
	protected int getErrorMesIndex(final int index) {
		switch (index) {
		case SAVE_TEMPLATE_IND:
			return ERROR_MES_INDEX_SAVE;
		case SUBMISSION_TEMPLATE_IND:
			return ERROR_MES_INDEX_SUBMISSION;
		case FILE_TEMPLATE_IND:
			return ERROR_MES_INDEX_FILE;
		default:
			return -1;
		}
	}

}
