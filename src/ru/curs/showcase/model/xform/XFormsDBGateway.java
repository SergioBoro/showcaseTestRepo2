package ru.curs.showcase.model.xform;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.exception.*;
import ru.curs.showcase.model.*;
import ru.curs.showcase.util.*;

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
	protected String getSqlTemplate() {
		return "{call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	/**
	 * Возвращает шаблон запроса для сохранения данных.
	 * 
	 * @return - шаблон.
	 */
	protected String getSaveSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?)}";
	}

	/**
	 * Возвращает шаблон для Submission.
	 * 
	 * @return - шаблон.
	 */
	protected String getSubmissionSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?)}";
	}

	private String getFileSqlTemplate() {
		return "{? = call [dbo].[%s](?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected DataPanelElementType getGatewayType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	public void saveData(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String data) {
		init(context, elementInfo);
		DataPanelElementProc proc = elementInfo.getSaveProc();
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}

		try {
			setConn(ConnectionFactory.getConnection());
			try {
				setProcName(proc.getName());
				String sql = String.format(getSaveSqlTemplate(), getProcName());
				setStatement(getConn().prepareCall(sql));
				getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralElementParameters();
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getStatement().execute();
				checkErrorCode();
			} finally {
				releaseResources();
			}
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
	}

	@Override
	public String handleSubmission(final String aProcName, final String aInputData) {
		try {
			Connection db = ConnectionFactory.getConnection();
			String out = null;
			try {
				String sql = String.format(getSubmissionSqlTemplate(), aProcName);
				setStatement(db.prepareCall(sql));
				getStatement().setString(INPUTDATA_PARAM, aInputData);
				getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
				getStatement().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getStatement().registerOutParameter(OUTPUTDATA_PARAM, java.sql.Types.SQLXML);
				getStatement().execute();
				checkErrorCode();

				SQLXML sqlxml = getStatement().getSQLXML(OUTPUTDATA_PARAM);
				if (sqlxml != null) {
					out = sqlxml.getString();
				}
			} finally {
				db.close();
			}
			return out;
		} catch (SQLException e) {
			if (ValidateInDBException.isExplicitRaised(e)) {
				throw new ValidateInDBException(e);
			} else {
				throw new DBQueryException(e, aProcName);
			}
		}
	}

	@Override
	public String getDataParam() {
		return XFORMSDATA_PARAM;
	}

	@Override
	public DataFile<ByteArrayOutputStream> downloadFile(final CompositeContext context,
			final DataPanelElementInfo elementInfo, final String linkId, final String data) {
		init(context, elementInfo);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}

		try {
			setConn(ConnectionFactory.getConnection());
			DataFile<ByteArrayOutputStream> result;
			try {
				setProcName(proc.getName());
				String sql = String.format(getFileSqlTemplate(), getProcName());
				setStatement(getConn().prepareCall(sql));
				getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralElementParameters();
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
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

			} finally {
				releaseResources();
			}
			return result;
		} catch (SQLException e) {
			dbExceptionHandler(e);
		} catch (IOException e2) {
			throw new CreateObjectError(e2);
		}
		return null;
	}

	@Override
	public void uploadFile(final CompositeContext context, final DataPanelElementInfo elementInfo,
			final String linkId, final String data, final DataFile<InputStream> file) {
		init(context, elementInfo);
		DataPanelElementProc proc = elementInfo.getProcs().get(linkId);
		if (proc == null) {
			throw new IncorrectElementException(elementInfo.toString());
		}

		try {
			setConn(ConnectionFactory.getConnection());
			try {
				setProcName(proc.getName());
				String sql = String.format(getFileSqlTemplate(), getProcName());
				setStatement(getConn().prepareCall(sql));
				getStatement().registerOutParameter(1, java.sql.Types.INTEGER);
				setupGeneralElementParameters();
				getStatement().setString(XFORMSDATA_PARAM, data);
				getStatement().setString(FILENAME_TAG, file.getName());
				getStatement().setBinaryStream(FILE_TAG, file.getData());
				getStatement().registerOutParameter(ERROR_MES_COL, java.sql.Types.VARCHAR);
				getStatement().execute();
				checkErrorCode();
			} finally {
				releaseResources();
			}
		} catch (SQLException e) {
			dbExceptionHandler(e);
		}
	}

}
