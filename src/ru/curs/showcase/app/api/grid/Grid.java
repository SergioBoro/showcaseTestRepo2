package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelCompBasedElement;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс грида с данными и настройками оформления. Содержит всю информацию,
 * необходимую для отрисовки грида в UI.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grid extends DataPanelCompBasedElement implements SizeEstimate {

	private static final long serialVersionUID = -8875148764868361032L;

	/**
	 * Набор данных для грида. Содержит описания столбцов, строк, страниц и
	 * внешнего вида собственно грида.
	 */
	private DataSet dataSet = new DataSet();

	private LiveInfo liveInfo = new LiveInfo();

	private JSInfo jsInfo = new JSInfo();

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
	private Column autoSelectColumn = null;

	public Grid() {
		super();
	}

	public Grid(final DataPanelElementInfo aElInfo) {
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

	public final LiveInfo getLiveInfo() {
		return liveInfo;
	}

	public final void setLiveInfo(final LiveInfo aLiveInfo) {
		this.liveInfo = aLiveInfo;
	}

	public JSInfo getJSInfo() {
		return jsInfo;
	}

	public void setJSInfo(final JSInfo aJsInfo) {
		jsInfo = aJsInfo;
	}

	public final Column getAutoSelectColumn() {
		return autoSelectColumn;
	}

	public final void setAutoSelectColumn(final Column aAutoSelectColumn) {
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

			Action res = getActionForClickType(columnId, InteractionType.SINGLE_CLICK);
			if (res != null) {
				return res;
			}
			res = getActionForClickType(columnId, InteractionType.DOUBLE_CLICK);
			if (res != null) {
				return res;
			}
		} else {
			return getDefaultAction();
		}
		return null;

	}

	private Action getActionForClickType(final String columnId, final InteractionType aClickType) {
		List<GridEvent> events =
			getEventManager().getEventForCell(autoSelectRecord.getId(), columnId, aClickType);
		GridEvent res = getConcreteEvent(events);
		if (res != null) {
			return res.getAction();
		}
		return null;
	}

	private GridEvent getConcreteEvent(final List<GridEvent> events) {
		if (events.size() > 0) {
			return events.get(events.size() - 1);
		}
		return null;
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
}
