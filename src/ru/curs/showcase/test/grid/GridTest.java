package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.*;
import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.test.AbstractTest;

import com.google.gwt.dom.client.Style.Unit;

/**
 * Модульные тесты (без обращения к БД или файловой системе) грида и его
 * внутренних компонентов.
 * 
 * @author den
 * 
 */
public class GridTest extends AbstractTest {

	@Test
	public void recordShouldDetermineDifferentFontSizeFormated() {
		Record rec = new Record();

		final String fontSize = "1.1";
		rec.setFontSize(fontSize);
		final double accuracy = 0.01;
		assertEquals(Double.parseDouble(fontSize), rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.EM, rec.getFontSizeUnit());

		rec.setFontSize("1.1em");
		assertEquals(Double.parseDouble(fontSize), rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.EM, rec.getFontSizeUnit());

		rec.setFontSize("12px");
		final int fonSize2 = 12;
		assertEquals(fonSize2, rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.PX, rec.getFontSizeUnit());

		rec.setFontSize("120%");
		final int fonSize3 = 120;
		assertEquals(fonSize3, rec.getFontSizeValue(), accuracy);
		assertEquals(Unit.PCT, rec.getFontSizeUnit());
	}

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void exceptionShouldBeRaisedWhenUseWrongFontSizeFormat() {
		Record rec = new Record();

		try {
			rec.setFontSize("%");
			rec.getFontSizeValue();
		} catch (NumberFormatException e) {
			rec.setFontSize("");
			rec.getFontSizeValue();
		}
		fail();
	}

	@Test
	public void gridEventPropsShouldMatchEventsProps() {
		Event event = new GridEvent();
		ID id1 = new ID("id1");
		ID id2 = new ID("2");
		event.setId1(id1);
		event.setId2(id2);

		GridEvent gEvent = (GridEvent) event;
		assertEquals(id1, gEvent.getRecordId());
		assertEquals(id2, gEvent.getColId());

		final String recId = "recId";
		gEvent.setRecordId(recId);
		final String colId = "colId";
		gEvent.setColId(colId);
		assertEquals(recId, event.getId1().toString());
		assertEquals(colId, event.getId2().toString());
	}

	@Test
	public void gridActionForDependentElementsShouldReturnDefaultActionIfAutoSelectDisabled() {
		Grid grid = new Grid(getTestGridInfo());
		GridEvent event = generateRowClickEvent("1");
		grid.getEventManager().getEvents().add(event);
		Action action = generateTestActionForRefreshElements();
		grid.setDefaultAction(action);

		assertEquals(action, grid.getActionForDependentElements());
	}

	private GridEvent generateRowClickEvent(final String id) {
		GridEvent event = new GridEvent();
		event.setRecordId(id);
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	private GridEvent generateCellClickEvent(final String row, final String cell) {
		GridEvent event = new GridEvent();
		event.setRecordId(row);
		event.setColId(cell);
		event.setInteractionType(InteractionType.SINGLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	private GridEvent generateRowDblClickEvent(final String id) {
		GridEvent event = new GridEvent();
		event.setRecordId(id);
		event.setInteractionType(InteractionType.DOUBLE_CLICK);
		event.setAction(generateTestActionForRefreshElements());
		return event;
	}

	@Test
	public void gridActionForDependentElementsShouldReturnNullByDefault() {
		Grid grid = new Grid(getTestGridInfo());

		assertNull(grid.getActionForDependentElements());
	}

	@Test
	public void gridActionForDependentElementsShouldReturnNullIfNoEventInRecord() {
		Grid grid = new Grid(getTestGridInfo());
		Record rec = new Record();
		rec.setId("1");
		rec.setIndex(0);
		grid.setAutoSelectRecord(rec);
		GridEvent event = generateRowClickEvent("2");
		grid.getEventManager().getEvents().add(event);

		assertNull(grid.getActionForDependentElements());
	}

	@Test
	public void gridActionForDependentElementsShouldReturnAutoSelectRecordEventIfItExists() {
		Grid grid = new Grid(getTestGridInfo());
		Record rec = new Record();
		final String autoSelectRecId = "1";
		rec.setId(autoSelectRecId);
		rec.setIndex(0);
		grid.setAutoSelectRecord(rec);
		final int recCount = 10;
		GridEvent event = generateRowClickEvent(autoSelectRecId);
		grid.getEventManager().getEvents().add(event);
		Action action = event.getAction();
		for (int i = 2; i < recCount; i++) {
			event = generateRowClickEvent(String.valueOf(i));
			grid.getEventManager().getEvents().add(event);
		}

		assertEquals(action, grid.getActionForDependentElements());
	}

	@Test
	public void gridActionForDependentElementsShouldReturnAutoSelectDblClickEventIfItExists() {
		Grid grid = new Grid(getTestGridInfo());
		Record rec = new Record();
		final String autoSelectRecId = "1";
		rec.setId(autoSelectRecId);
		rec.setIndex(0);
		grid.setAutoSelectRecord(rec);
		GridEvent event = generateRowDblClickEvent(autoSelectRecId);
		grid.getEventManager().getEvents().add(event);
		Action action = event.getAction();

		assertEquals(action, grid.getActionForDependentElements());
	}

	@Test
	public void gridActionForDependentElementsShouldReturnAutoSelectCellkEventIfItExists() {
		Grid grid = new Grid(getTestGridInfo());
		Record rec = new Record();
		final String autoSelectId = "1";
		rec.setId(autoSelectId);
		rec.setIndex(0);
		grid.setAutoSelectRecord(rec);
		Column col = new Column();
		col.setId(autoSelectId);
		grid.setAutoSelectColumn(col);
		GridEvent event = generateRowClickEvent(autoSelectId);
		grid.getEventManager().getEvents().add(event);
		event = generateRowDblClickEvent(autoSelectId);
		grid.getEventManager().getEvents().add(event);
		event = generateCellClickEvent(autoSelectId, autoSelectId);
		grid.getEventManager().getEvents().add(event);
		Action action = event.getAction();

		assertEquals(action, grid.getActionForDependentElements());
	}

	@Test
	public void
			gridActionForDependentElementsShouldReturnRowClickEventIfOnlyAutoSelectRowEnabled() {
		Grid grid = new Grid(getTestGridInfo());
		Record rec = new Record();
		final String autoSelectId = "1";
		rec.setId(autoSelectId);
		rec.setIndex(0);
		grid.setAutoSelectRecord(rec);

		GridEvent event = generateRowClickEvent(autoSelectId);
		grid.getEventManager().getEvents().add(event);
		Action action = event.getAction();
		event = generateRowDblClickEvent(autoSelectId);
		grid.getEventManager().getEvents().add(event);
		event = generateCellClickEvent(autoSelectId, autoSelectId);
		grid.getEventManager().getEvents().add(event);

		assertEquals(action, grid.getActionForDependentElements());
	}

	private Action generateTestActionForRefreshElements() {
		Action action = new Action(DataPanelActionType.RELOAD_ELEMENTS);
		DataPanelLink dLink = new DataPanelLink();
		action.setContext(CompositeContext.createCurrent());
		dLink.setDataPanelId(ID.CURRENT_ID);
		dLink.setTabId(ID.CURRENT_ID);
		action.setDataPanelLink(dLink);
		return action;
	}
}
