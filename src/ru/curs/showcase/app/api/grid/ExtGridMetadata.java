package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;
import ru.curs.showcase.app.api.event.*;

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
	 * Набор данных для грида. Содержит описания столбцов, строк, страниц и
	 * внешнего вида собственно грида.
	 */
	private DataSet dataSet = new DataSet();

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

	@Override
	public final GridEventManager getEventManager() {
		return (GridEventManager) super.getEventManager();
	}

	/**
	 * Возвращает столбец по его id.
	 * 
	 * @param id
	 *            - id.
	 * @return - столбец.
	 */
	public Column getColumnById(final String id) {
		for (Column current : getDataSet().getColumnSet().getColumns()) {
			if (current.getId().equals(id)) {
				return current;
			}
		}
		return null;
	}

	public final DataSet getDataSet() {
		return dataSet;
	}

	public final void setDataSet(final DataSet aDataSet) {
		dataSet = aDataSet;
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

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	@Override
	public Action getActionForDependentElements() {
		if (autoSelectRecord != null) {
			String columnId = null;

			if (autoSelectColumn != null) {
				columnId = autoSelectColumn.getId();
			}

			List<GridEvent> events =
				getEventManager().getEventForCell(autoSelectRecord.getId(), columnId,
						InteractionType.SINGLE_CLICK);
			GridEvent res = getConcreteEvent(events);
			if (res != null) {
				return res.getAction();
			}

			events =
				getEventManager().getEventForCell(autoSelectRecord.getId(), columnId,
						InteractionType.DOUBLE_CLICK);
			res = getConcreteEvent(events);
			if (res != null) {
				return res.getAction();
			}
		} else {
			return getDefaultAction();
		}

		return null;
	}

	private GridEvent getConcreteEvent(final List<GridEvent> events) {
		return (GridEvent) events.toArray()[events.size() - 1];
	}

	public final DataGridSettings getUISettings() {
		return uiSettings;
	}

	public final void setUISettings(final DataGridSettings aSettings) {
		uiSettings = aSettings;
	}

	@Override
	protected GridEventManager initEventManager() {
		return new GridEventManager();
	}

	@Override
	public long sizeEstimate() {
		long result = Integer.SIZE / Byte.SIZE;
		for (Event ev : getEventManager().getEvents()) {
			result += ev.sizeEstimate();
		}
		for (Record rec : dataSet.getRecordSet().getRecords()) {
			result += rec.getId().length();
			if (rec.getBackgroundColor() != null) {
				result += rec.getBackgroundColor().length();
			}
			if (rec.getTextColor() != null) {
				result += rec.getTextColor().length();
			}
			if (rec.getFontSize() != null) {
				result += rec.getFontSize().length();
			}
			for (java.util.Map.Entry<String, String> entry : rec.getValues().entrySet()) {
				result += entry.getKey().length();
				if (entry.getValue() != null) {
					result += entry.getValue().getBytes().length;
				}
			}

			for (java.util.Map.Entry<String, String> entry : rec.getAttributes().getValues()
					.entrySet()) {
				result += entry.getKey().length();
				if (entry.getValue() != null) {
					result += entry.getValue().getBytes().length;
				}
			}
			if (rec.getIndex() != null) {
				result += Integer.SIZE / Byte.SIZE;
			}
		}
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
