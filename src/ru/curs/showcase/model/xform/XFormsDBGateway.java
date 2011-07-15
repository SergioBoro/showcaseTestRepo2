package ru.curs.showcase.model.xform;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.StreamConvertor;

/**
 * Шлюз к БД для получения XForms.
 * 
 * @author den
 * 
 */
public final class XFormsDBGateway extends HTMLBasedSPCallHelper implements XFormsGateway {
	public static final String OUTPUT_COLUMNNAME = "xformssettings";
	static final String OUTPUTDATA_PARAM = "outputdata";
	static final String INPUTDATA_PARAM = "inputdata";
	static final String XFORMSDATA_PARAM = "xformsdata";
	static final int SAVE_TEMPLATE_IND = 1;
	static final int SUBMISSION_TEMPLATE_IND = 2;
	static final int FILE_TEMPLATE_IND = 3;

	@Override
	public HTMLBasedElementRawData getInitialData(final CompositeContext context,
			final DataPanelElementInfo elementInfo) {
		return stdGetData(context, elementInfo);
	}

	@Override
	public String getOutSettingsParam() {
		return OUTPUT_COLUMNNAME;
	}

	@Override
	protected String getSqlTemplate(final int index) {
		switch (index) {
		case 0:
			return "{call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
		case SAVE_TEMPLATE_IND:
			return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
		case SUBMISSION_TEMPLATE_IND:
			return "{? = call [dbo].[%s](?, ?, ?)}";
		case FILE_TEMPLATE_IND:
			return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?, ?, ?)}";
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
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().execute();
				checkErrorCode();
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
	}

	@Override
	public String handleSubmission(final String aProcName, final String aInputData) {
		String out = null;
		setProcName(aProcName);
		setTemplateIndex(SUBMISSION_TEMPLATE_IND);

		try {
			try {
				prepareStatementWithErrorMes();
				getStatement().setString(INPUTDATA_PARAM, aInputData);
				getStatement().registerOutParameter(OUTPUTDATA_PARAM, java.sql.Types.SQLXML);
				getStatement().execute();
				checkErrorCode();

				SQLXML sqlxml = getStatement().getSQLXML(OUTPUTDATA_PARAM);
				if (sqlxml != null) {
					out = sqlxml.getString();
				}
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
		return out;
	}

	@Override
	public String getDataParam() {
		return XFORMSDATA_PARAM;
	}

	@Override
	public DataFile<ByteArrayOutputStream> downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data) {
		init(context, elementInfo);
		setTemplateIndex(FILE_TEMPLATE_IND);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}
		setProcName(proc.getName());
		DataFile<ByteArrayOutputStream> result = null;

		try {
			try {
				prepareElementStatementWithErrorMes();
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().registerOutParameter(FILENAME_TAG, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(FILE_TAG, java.sql.Types.BLOB);
				getStatement().execute();
				checkErrorCode();

				String fileName = getStatement().getString(FILENAME_TAG);
				Blob blob = getStatement().getBlob(FILE_TAG);
				InputStream blobIs = blob.getBinaryStream();
				StreamConvertor dup = new StreamConvertor(blobIs);
				ByteArrayOutputStream os = dup.getOutputStream();
				result = new DataFile<ByteArrayOutputStream>(os, fileName);
			} catch (SQLException e) {
				dbExceptionHandler(e);
			} catch (IOException e2) {
				throw new CreateObjectError(e2);
			}
		} finally {
			releaseResources();
		}
		return result;
	}

	@Override
	public void uploadFile(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final String data, final DataFile<InputStream> file) {
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
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().setString(FILENAME_TAG, file.getName());
				getStatement().setBinaryStream(FILE_TAG, file.getData());
				getStatement().execute();
				checkErrorCode();
			} catch (SQLException e) {
				dbExceptionHandler(e);
			}
		} finally {
			releaseResources();
		}
	}

}
