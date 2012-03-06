package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.core.html.plugin.PluginCommand;
import ru.curs.showcase.test.AbstractTest;

/**
 * Тесты для команды создания UI плагина.
 * 
 * @author den
 * 
 */
public class PluginSLTest extends AbstractTest {

	@Test
	public void pluginCommandCanRunPostProcessAndShouldAddComponentsJSAndCSS() {
		PluginInfo elInfo = new PluginInfo("id", "radar", "pluginRadarInfo");
		String jythonProcName = "plugin/handleRadar.py";
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		Plugin plugin = command.execute();

		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertEquals("[{name: 'Россия', data1: 63.82, data2: 17.18, data3: 7.77}"
				+ "{name: 'Москва', data1: 47.22, data2: 19.12, data3: 20.21}"
				+ "{name: 'Питер', data1: 58.77, data2: 13.06, data3: 15.22}]", plugin.getParams()
				.get(0));
		assertEquals(2, plugin.getRequiredJS().size());
		assertEquals("solutions/default/plugins/radar/radar.js", plugin.getRequiredJS().get(0));
		assertEquals("solutions/default/components/extJS/ext-all.js", plugin.getRequiredJS()
				.get(1));
		assertEquals(1, plugin.getRequiredCSS().size());
		assertEquals("solutions/default/components/extJS/resources/css/ext-all.css", plugin
				.getRequiredCSS().get(0));
	}

	@Test
	public void pluginCommandCanBeExecutedWithoutPostProcess() {
		PluginInfo elInfo = new PluginInfo("id", "radar", "pluginRadarInfo");

		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		Plugin plugin = command.execute();

		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertTrue(plugin.getParams().get(0).startsWith("<root>"));
	}
}
