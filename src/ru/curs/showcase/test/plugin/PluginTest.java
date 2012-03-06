package ru.curs.showcase.test.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.test.AbstractTest;

/**
 * Модульные тесты для класса UI плагина.
 * 
 * @author den
 * 
 */
public class PluginTest extends AbstractTest {

	private static final String PLUGIN_NAME = "flash";
	private static final String PROC_NAME = "getPluginInfo";
	private static final String JYTHON_PROC_NAME = "handleFlash";

	@Test
	public void pluginShouldHaveEmptyPropValuesAfterCreation() {
		Plugin plugin = new Plugin();

		assertNull(plugin.getCreateProc());
		assertNull(plugin.getDefaultAction());
		assertEquals(0, plugin.getParams().size());
		assertEquals(0, plugin.getRequiredJS().size());
		assertEquals(0, plugin.getRequiredCSS().size());
		assertEquals(0, plugin.getEventManager().getEvents().size());
	}

	@Test
	public void correctPluginInfoShouldHavePluginProp() {
		PluginInfo pi = new PluginInfo("id", null, PROC_NAME);
		assertFalse(pi.isCorrect());
		pi.setPlugin(PLUGIN_NAME);
		assertTrue(pi.isCorrect());
	}

	@Test
	public void pluginInfoIsDataPanelElementInfoWithPluginData() {
		PluginInfo pi = new PluginInfo("id", PLUGIN_NAME, PROC_NAME);
		pi.addPostProcessProc(JYTHON_PROC_NAME, JYTHON_PROC_NAME);

		assertTrue(pi instanceof DataPanelElementInfo);
		assertEquals(PROC_NAME, pi.getProcName());
		assertEquals(PLUGIN_NAME, pi.getPlugin());
		assertEquals(1, pi.getProcs().size());
		assertEquals(JYTHON_PROC_NAME, pi.getProcByType(DataPanelElementProcType.POSTPROCESS)
				.getName());
		assertEquals(pi.getPostProcessProcName(),
				pi.getProcByType(DataPanelElementProcType.POSTPROCESS).getName());
	}

}
