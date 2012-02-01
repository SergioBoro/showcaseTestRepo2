package ru.curs.showcase.core.sp;

import java.io.*;
import java.sql.*;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.jython.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.TextUtils;

import com.ziclix.python.sql.PyConnection;

/**
 * Jython шлюз для элементов, требующих RecordSet.
 * 
 * @author den
 * 
 */
public class RecordSetElementJythonGateway extends JythonQuery<JythonDTO> implements
		RecordSetElementGateway<CompositeContext> {

	private CompositeContext context;
	private DataPanelElementInfo elementInfo;
	private Connection conn;

	public RecordSetElementJythonGateway() {
		super(JythonDTO.class);
	}

	@Override
	protected Object execute() {
		PyConnection pyConn;
		try {
			pyConn = new PyConnection(conn);
		} catch (SQLException e) {
			throw new DBConnectException(e);
		}
		return getProc().getRawData(context, elementInfo.getId().getString(), pyConn);
	}

	@Override
	protected String getJythonProcName() {
		return elementInfo.getProcName();
	}

	@Override
	public RecordSetElementRawData getRawData(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		context = aContext;
		elementInfo = aElInfo;
		conn = ConnectionFactory.getInstance().acquire();
		runTemplateMethod();
		String[] query;
		if (getResult().getData() != null) {
			String[] tmp = { getResult().getData() };
			query = tmp;
		} else {
			query = getResult().getDataArray();
		}
		InputStream settings = null;
		try {
			if (getResult().getSettings() != null) {
				settings = TextUtils.stringToStream(getResult().getSettings());
			}
		} catch (IOException e) {
			throw new JythonException(RESULT_FORMAT_ERROR);
		}
		PreparedStatement[] statement = new PreparedStatement[query.length];
		for (int i = 0; i < query.length; i++) {
			try {
				statement[i] = conn.prepareStatement(query[i]);
				statement[i].execute();
			} catch (SQLException e) {
				if (UserMessageFactory.isExplicitRaised(e)) {
					UserMessageFactory factory = new UserMessageFactory();
					throw new ValidateException(factory.build(e));
				}
				throw new DBConnectException(e);
			}
		}
		return new RecordSetElementRawData(settings, elementInfo, context, statement);
	}
}
