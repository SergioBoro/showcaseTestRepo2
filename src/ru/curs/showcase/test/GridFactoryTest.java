package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.ElementRawData;
import ru.curs.showcase.model.grid.*;

/**
 * Тестовый класс для фабрики гридов.
 * 
 * @author den
 * 
 */
public class GridFactoryTest extends AbstractTestWithDefaultUserData {
	private static final String GRIDBAL_TEST_PROPERTIES = "gridbal.test.properties";

	/**
	 * Тестирует задание профайла настроек из хранимой процедуры.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProfileSelection() throws Exception {
		GridContext context = getTestGridContext1();
		DataPanelElementInfo element = getDPElement("test.xml", "2", "4");

		GridGateway gateway = new GridDBGateway();
		ElementRawData raw = gateway.getRawDataAndSettings(context, element);
		GridDBFactory factory = new GridDBFactory(raw);
		Grid grid = factory.build();
		assertEquals(GRIDBAL_TEST_PROPERTIES, factory.serverState().getProfile());

		assertEquals(1, grid.getDataSet().getRecordSet().getPageNumber());

		GridProps gp = new GridProps(GRIDBAL_TEST_PROPERTIES);

		Boolean defSelectRecord =
			gp.stdReadBoolGridValue(DefaultGridUIStyle.DEF_SELECT_WHOLE_RECORD);
		assertEquals(defSelectRecord, grid.getUISettings().isSelectOnlyRecords());
		final int fontWidth = 27;
		assertEquals(fontWidth, grid.getDataSet().getRecordSet().getRecords().get(0).getFontSize());
	}

	/**
	 * Проверка работы функции
	 * {@link ru.curs.showcase.model.grid.GridDBFactory#replaceXMLServiceSymbols}
	 * .
	 */
	@Test
	public void testGridLinkReplaceXMLServiceSymbols() {
		assertEquals("<link href=\"ya.ru?search=aa&amp;bla&amp;ab\" "
				+ "image=\"xxx.jpg\"  text=\"&lt;&quot; &lt;&gt; &gt; a&apos;&quot;\"  />",
				GridDBFactory.makeSafeXMLAttrValues("<link href=\"ya.ru?search=aa&amp;bla&ab\" "
						+ "image=\"xxx.jpg\"  text=\"<&quot; &lt;&gt; > a'\"\"  />"));
	}
}
