package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.core.html.plugin.PluginCommand;
import ru.curs.showcase.core.primelements.*;
import ru.curs.showcase.core.primelements.datapanel.DataPanelFactory;
import ru.curs.showcase.test.AbstractTestWithDefaultUserData;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Тесты для команды создания UI плагина.
 * 
 * @author den
 * 
 */
public class PluginSLTest extends AbstractTestWithDefaultUserData {

	private static final String PLUGIN_HANDLE_RADAR_PY = "plugin/handleRadar.py";
	private static final String PLUGIN_RADAR_PROC = "pluginRadarInfo";
	private static final String RADAR_COMP = "radar";

	@Test
	public void pluginCommandShouldReadDataFromDBAndCanRunPostProcessJython() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		String jythonProcName = PLUGIN_HANDLE_RADAR_PY;
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		Plugin plugin = command.execute();

		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		final int width = 800;
		final int height = 600;
		assertEquals(width, plugin.getSize().getWidth().intValue());
		assertEquals(height, plugin.getSize().getHeight().intValue());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertEquals("[{name: 'Russia', data1: 63.82, data2: 17.18, data3: 7.77},"
				+ "{name: 'Moscow', data1: 47.22, data2: 19.12, data3: 20.21},"
				+ "{name: 'Piter', data1: 58.77, data2: 13.06, data3: 15.22},]", plugin
				.getParams().get(0));
	}

	@Test
	public void pluginCommandShouldAddComponentsJSAndCSS() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		String jythonProcName = PLUGIN_HANDLE_RADAR_PY;
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		Plugin plugin = command.execute();

		assertEquals(2, plugin.getRequiredJS().size());
		assertEquals("solutions/default/plugins/radar/radar.js", plugin.getRequiredJS().get(0));
		assertEquals("solutions/default/libraries/extJS/ext-all.js", plugin.getRequiredJS().get(1));
		assertEquals(0, plugin.getRequiredCSS().size());
	}

	@Test
	public void pluginCommandCanBeExecutedWithoutPostProcess() {
		PluginInfo elInfo = new PluginInfo("id", RADAR_COMP, PLUGIN_RADAR_PROC);
		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		Plugin plugin = command.execute();

		assertNull(plugin.getDefaultAction());
		assertEquals("createRadar", plugin.getCreateProc());
		assertEquals("one param expected", 1, plugin.getParams().size());
		assertTrue(plugin.getParams().get(0).startsWith("<root>"));
	}

	@Test
	public void datapanelFactoryMustReadPluginInfo() {
		PrimElementsGateway gateway = new PrimElementsFileGateway(SettingsFileType.DATAPANEL);
		DataFile<InputStream> file = gateway.getRawData(new CompositeContext(), TEST2_XML);
		DataPanelFactory factory = new DataPanelFactory();
		DataPanel panel = factory.fromStream(file);
		final PluginInfo elementInfo =
			(PluginInfo) panel.getTabById("4").getElementInfoById("0401");

		assertNotNull(elementInfo);
		assertEquals(DataPanelElementType.PLUGIN, elementInfo.getType());
		assertEquals(RADAR_COMP, elementInfo.getPlugin());
		assertEquals("fake.xsl", elementInfo.getTransformName());
		assertEquals(PLUGIN_HANDLE_RADAR_PY, elementInfo.getPostProcessProcName());
	}
}
