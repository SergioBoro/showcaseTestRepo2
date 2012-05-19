package ru.curs.showcase.app.api.grid;

import java.util.List;

import javax.xml.bind.annotation.*;

import ru.curs.showcase.app.api.SizeEstimate;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.event.*;

/**
 * Класс грида из GXT с метаданными.
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LiveGridExtradata extends DataPanelElement implements SizeEstimate {

	private static final long serialVersionUID = -2429397174443684120L;

	private GridEventManager gridEventManager = null;

	/**
	 * Идентификатор строки, которая должна быть выделена автоматически при
	 * перерисовке грида с новыми данными. Если null - ничего делать не надо.
	 */
	private String autoSelectRecordId = null;

	/**
	 * Идентификатор столбца, который должен быть выделен автоматически при
	 * перерисовке грида с новыми данными. Если null - ничего делать не надо.
	 */
	private String autoSelectColumnId = null;

	public LiveGridExtradata() {
		super();
	}

	public LiveGridExtradata(final DataPanelElementInfo aElInfo) {
		super(aElInfo);
	}

	@Override
	public final GridEventManager getEventManager() {
		return gridEventManager;
	}

	public void setGridEventManager(final GridEventManager aGridEventManager) {
		gridEventManager = aGridEventManager;
	}

	public final String getAutoSelectColumnId() {
		return autoSelectColumnId;
	}

	public final void setAutoSelectColumnId(final String aAutoSelectColumnId) {
		autoSelectColumnId = aAutoSelectColumnId;
	}

	public final String getAutoSelectRecordId() {
		return autoSelectRecordId;
	}

	public final void setAutoSelectRecordId(final String aAutoSelectRecordId) {
		autoSelectRecordId = aAutoSelectRecordId;
	}

	/**
	 * Возвращает действие для отрисовки зависимого элемента.
	 * 
	 * @return - действие.
	 */
	@Override
	public Action getActionForDependentElements() {
		if (autoSelectRecordId != null) {
			String columnId = null;

			if (autoSelectColumnId != null) {
				columnId = autoSelectColumnId;
			}

			List<GridEvent> events =
				getEventManager().getEventForCell(autoSelectRecordId, columnId,
						InteractionType.SINGLE_CLICK);
			GridEvent res = getConcreteEvent(events);
			if (res != null) {
				return res.getAction();
			}

			events =
				getEventManager().getEventForCell(autoSelectRecordId, columnId,
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
