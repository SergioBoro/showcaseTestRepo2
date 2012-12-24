package ru.curs.showcase.test.grid;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.grid.GridContext;
import ru.curs.showcase.core.grid.*;
import ru.curs.showcase.core.sp.RecordSetElementRawData;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;

/**
 * Тест шлюза для получения настроек элемента grid (простой грид) c помощью
 * выполнения Jython скрипта.
 * 
 * @author bogatov
 * 
 */
public class GridJythonGatewayTest extends AbstractTestWithDefaultUserData {

	private DataPanelElementInfo getTestGridJythonInfo() {
		DataPanelElementInfo elInfo =
			new DataPanelElementInfo("GridJython01", DataPanelElementType.GRID);
		elInfo.setPosition(1);
		elInfo.setProcName("testGridJython.py");
		generateTestTabWithElement(elInfo);
		return elInfo;
	}

	@Test
	public void testGridJythonGateway() {
		CompositeContext context = getTestContext1();
		DataPanelElementInfo elInfo = getTestGridJythonInfo();
		GridGateway gateway = new GridJythonGateway();
		GridContext gc = new GridContext(context);
		RecordSetElementRawData res = gateway.getRawData(gc, elInfo);
		assertNotNull(res);
		assertNotNull(res.getSettings());
		assertNotNull(res.getXmlDS());
	}

}
