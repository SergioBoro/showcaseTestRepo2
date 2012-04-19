package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;

/**
 * Класс грида из ExtGWT с метаданными.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtGridMetadata extends DataPanelCompBasedElement implements SizeEstimate {

	private static final long serialVersionUID = 2492137452715570464L;

	private List<ExtGridColumnConfig> columns = null;

	private ExtGridLiveInfo liveInfo = new ExtGridLiveInfo();

	private ColumnSet originalColumnSet = null;

	/**
	 * Настройки UI для грида. Как правило, задаются по умолчанию для всех
	 * гридов в файле настроек приложения.
	 */
	private DataGridSettings uiSettings = new DataGridSettings();

	/**
	 * Строка, которая должна быть выделена автоматически при перерисовке грида
	 * с новыми данными. Если null - ничего делать не надо.
	 */
	private Record autoSelectRecord = null;

	/**
	 * Столбец, который должен быть выделен автоматически при перерисовке грида
	 * с новыми данными. Если null - ничего делать не надо.
	 */
	private ExtGridColumnConfig autoSelectColumn = null;

	public ExtGridMetadata() {
		super();
	}

	public ExtGridMetadata(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	public final ExtGridColumnConfig getAutoSelectColumn() {
		return autoSelectColumn;
	}

	public final void setAutoSelectColumn(final ExtGridColumnConfig aAutoSelectColumn) {
		autoSelectColumn = aAutoSelectColumn;
	}

	public final Record getAutoSelectRecord() {
		return autoSelectRecord;
	}

	public final void setAutoSelectRecord(final Record aAutoSelectRecord) {
		autoSelectRecord = aAutoSelectRecord;
	}

	public final DataGridSettings getUISettings() {
		return uiSettings;
	}

	public final void setUISettings(final DataGridSettings aSettings) {
		uiSettings = aSettings;
	}

	@Override
	protected GridEventManager initEventManager() {
		return null;
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		return result;
	}

	public List<ExtGridColumnConfig> getColumns() {
		return columns;
	}

	public void setColumns(final List<ExtGridColumnConfig> aColumns) {
		columns = aColumns;
	}

	public ExtGridLiveInfo getLiveInfo() {
		return liveInfo;
	}

	public void setLiveInfo(final ExtGridLiveInfo aLiveInfo) {
		liveInfo = aLiveInfo;
	}

	public ColumnSet getOriginalColumnSet() {
		return originalColumnSet;
	}

	public void setOriginalColumnSet(final ColumnSet aOriginalColumnSet) {
		originalColumnSet = aOriginalColumnSet;
	}

}
