package ru.curs.showcase.core.grid;

import java.sql.*;

import ru.curs.gwt.datagrid.model.Column;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.sp.*;

/**
 * Базовый класс для получения данных грида из БД. На его основе созданы классы
 * для работы с хранимыми процедурами и SQL скриптами.
 * 
 * @author den
 * 
 */
public abstract class AbstractGridDBGateway extends CompBasedElementSPQuery implements GridGateway {
	protected static final int DATA_AND_SETTINS_QUERY = 0;
	protected static final int DATA_ONLY_QUERY = 1;
	protected static final int FILE_DOWNLOAD = 2;

	public AbstractGridDBGateway() {
		super();
	}

	protected abstract int getFirstRecordIndex();

	protected abstract int getPageSizeIndex();

	protected void prepare() throws SQLException {
		switch (getTemplateIndex()) {
		case DATA_AND_SETTINS_QUERY:
			prepareForGetDataAndSettings();
			break;
		case DATA_ONLY_QUERY:
			prepareForGetData();
			break;
		default:
			break;
		}
	}

	@Override
	public RecordSetElementRawData getRawDataAndSettings(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		return templateMethodForGetData(context, elementInfo);
	}

	@Override
	public RecordSetElementRawData getRawData(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		setTemplateIndex(DATA_ONLY_QUERY);
		return templateMethodForGetData(context, elementInfo);
	}

	protected abstract void prepareForGetData() throws SQLException;

	protected abstract void prepareForGetDataAndSettings() throws SQLException;

	protected abstract int getSortColsIndex();

	private void setupSorting(final GridContext settings) throws SQLException {
		if (settings.sortingEnabled()) {
			StringBuilder builder = new StringBuilder("ORDER BY ");
			for (Column col : settings.getSortedColumns()) {
				builder.append(String.format("\"%s\" %s,", col.getId(), col.getSorting()));
			}
			String sortStatement = builder.substring(0, builder.length() - 1);
			setStringParam(getSortColsIndex(), sortStatement);
		} else {
			setStringParam(getSortColsIndex(), "");
		}
	}

	public RecordSetElementRawData templateMethodForGetData(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		init(context, elementInfo);
		setRetriveResultSets(true);
		try {
			context.normalize();

			prepare();

			setupSorting(context);
			stdGetResults();
			RecordSetElementRawData raw = new RecordSetElementRawData(this, elementInfo, context);
			raw.prepareXmlDS();
			return raw;
		} catch (SQLException e) {
			throw dbExceptionHandler(e);
		}
	}

	protected void setupRange() throws SQLException {
		int firstRecord;
		int pageSize;
		if ((getContext().getSubtype() == DataPanelElementSubType.EXT_LIVE_GRID)
				|| (getContext().getSubtype() == DataPanelElementSubType.EXT_PAGE_GRID)) {
			firstRecord = getContext().getLiveInfo().getFirstRecord();
			pageSize = getContext().getLiveInfo().getLimit();
		} else {
			firstRecord = getContext().getPageInfo().getFirstRecord();
			pageSize = getContext().getPageSize();
		}
		setIntParam(getFirstRecordIndex(), firstRecord);
		setIntParam(getPageSizeIndex(), pageSize);
	}

	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected void registerOutParameterCursor() throws SQLException {
	}

	@Override
	public void continueSession(final ElementSettingsGateway aSessionHolder) {
		setConn((Connection) aSessionHolder.getSession());
	}

}