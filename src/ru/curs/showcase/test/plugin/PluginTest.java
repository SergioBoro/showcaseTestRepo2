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
	public void pluginInfoIsDataPanelElementInfoWithPluginData() {
		final String pluginName = "flash";
		final String procName = "getPluginInfo";
		final String jythonProcName = "handleFlash";
		PluginInfo pi = new PluginInfo("id", pluginName, procName);
		pi.addPostProcessProc(jythonProcName, jythonProcName);

		assertTrue(pi instanceof DataPanelElementInfo);
		assertEquals(procName, pi.getProcName());
		assertEquals(pluginName, pi.getPlugin());
		assertEquals(1, pi.getProcs().size());
		assertEquals(jythonProcName, pi.getProcByType(DataPanelElementProcType.POSTPROCESS)
				.getName());
	}

}
