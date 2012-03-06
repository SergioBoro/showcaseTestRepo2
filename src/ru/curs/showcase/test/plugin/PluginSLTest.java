package ru.curs.showcase.test.plugin;

import org.junit.Test;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
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
	public void pluginCommandShouldExecWithoutExceptions() {
		PluginInfo elInfo = new PluginInfo("id", "radar", "pluginRadarInfo");
		String jythonProcName = "plugin/handleRadar.py";
		elInfo.addPostProcessProc(jythonProcName, jythonProcName);

		PluginCommand command = new PluginCommand(getSimpleTestContext(), elInfo);
		command.execute();
	}

}
