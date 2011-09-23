package ru.curs.showcase.model.xform;

import java.io.*;
import java.sql.SQLException;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.exception.CreateObjectError;

/**
 * Шлюз к БД для получения XForms.
 * 
 * @author den
 * 
 */
public final class XFormDBGateway extends HTMLBasedSPCallHelper implements XFormGateway {

	private static final int MAIN_CONTEXT_INDEX_0 = 1;
	private static final int ADD_CONTEXT_INDEX_0 = 2;
	private static final int FILTER_INDEX_0 = 3;
	private static final int SESSION_CONTEXT_INDEX_0 = 4;
	private static final int ELEMENTID_INDEX_0 = 5;
	private static final int XFORMSDATA_INDEX_0 = 6;

	private static final int MAIN_CONTEXT_INDEX_NOT0 = 2;
	private static final int ADD_CONTEXT_INDEX_NOT0 = 3;
	private static final int FILTER_INDEX_NOT0 = 4;
	private static final int SESSION_CONTEXT_INDEX_NOT0 = 5;
	private static final int ELEMENTID_INDEX_NOT0 = 6;
	private static final int XFORMSDATA_INDEX_NOT0 = 7;

	private static final int OUTPUT_INDEX = 7;

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
			return "{call %s(?, ?, ?, ?, ?, ?, ?)}";
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
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) {
		init(context, elementInfo);
		setTemplateIndex(SAVE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getSaveProc();
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setProcName(proc.getName());

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(SAVE_TEMPLATE_IND), data);
				execute();
				checkErrorCode();
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
				checkErrorCode();
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
	public int getDataParam(final int index) {
		if (index == 0) {
			return XFORMSDATA_INDEX_0;
		} else {
			return XFORMSDATA_INDEX_NOT0;
		}
	}

	@Override
	public OutputStreamDataFile downloadFile(final XFormContext context,
			final DataPanelElementInfo elementInfo, final String linkId) {
		init(context, elementInfo);
		setTemplateIndex(FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setProcName(proc.getName());
		OutputStreamDataFile result = null;

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(FILE_TEMPLATE_IND), context.getFormData());
				getStatement().registerOutParameter(FILENAME_INDEX, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_INDEX, getBinarySQLType());
				execute();
				checkErrorCode();
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
			throw new IncorrectElementException(elementInfo.toString());
		}
		setProcName(proc.getName());

		try {
			try {
				prepareElementStatementWithErrorMes();
				setSQLXMLParam(getDataParam(FILE_TEMPLATE_IND), context.getFormData());
				setStringParam(FILENAME_INDEX, file.getName());
				setBinaryStream(FILE_INDEX, file);
				execute();
				checkErrorCode();
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
	protected int getMainContextIndex(final int index) {
		if (index == 0) {
			return MAIN_CONTEXT_INDEX_0;
		} else {
			return MAIN_CONTEXT_INDEX_NOT0;
		}
	}

	@Override
	protected int getAddContextIndex(final int index) {
		if (index == 0) {
			return ADD_CONTEXT_INDEX_0;
		} else {
			return ADD_CONTEXT_INDEX_NOT0;
		}

	}

	@Override
	protected int getFilterIndex(final int index) {
		if (index == 0) {
			return FILTER_INDEX_0;
		} else {
			return FILTER_INDEX_NOT0;
		}

	}

	@Override
	protected int getSessionContextIndex(final int index) {
		if (index == 0) {
			return SESSION_CONTEXT_INDEX_0;
		} else {
			return SESSION_CONTEXT_INDEX_NOT0;
		}

	}

	@Override
	protected int getElementIdIndex(final int index) {
		if (index == 0) {
			return ELEMENTID_INDEX_0;
		} else {
			return ELEMENTID_INDEX_NOT0;
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

	private int getBinarySQLType() {
		if (ConnectionFactory.getSQLServerType() == SQLServerType.MSSQL) {
			return java.sql.Types.BLOB;
		} else {
			return java.sql.Types.BINARY;
		}
	}

}
