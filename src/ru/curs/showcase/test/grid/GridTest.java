package ru.curs.showcase.test.grid;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.gwt.datagrid.model.Record;
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

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void testFontSizeDetermine() {
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

		try {
			rec.setFontSize("%");
			rec.getFontSizeValue();
		} catch (NumberFormatException e) {
			rec.setFontSize("");
			rec.getFontSizeValue();
		}
		fail();
	}
}
