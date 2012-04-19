package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.gwt.datagrid.model.Record;
import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс грида из ExtGWT с метаданными.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtGridExtradata extends DataPanelElement implements SizeEstimate {

	private static final long serialVersionUID = -2429397174443684120L;

	private GridEventManager gridEventManager = null;

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

	public ExtGridExtradata() {
		super();
	}

	public ExtGridExtradata(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	@Override
	public final GridEventManager getEventManager() {
		return gridEventManager;
	}

	public void setGridEventManager(final GridEventManager aGridEventManager) {
		gridEventManager = aGridEventManager;
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
		return result;
	}

}
