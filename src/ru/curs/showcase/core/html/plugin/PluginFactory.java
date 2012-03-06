package ru.curs.showcase.core.html.plugin;

import java.io.*;
import java.util.*;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.Plugin;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;

/**
 * Фабрика для создания UI плагинов.
 * 
 * @author den
 * 
 */
public final class PluginFactory extends HTMLBasedElementFactory {

	public static final String COMPONENTS_DIR = "components";
	private static final String IMPORT_TXT = "import.txt";
	public static final String PLUGINS_DIR = "plugins";
	private Plugin result;

	public PluginFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public Plugin getResult() {
		return result;
	}

	@Override
	public PluginInfo getElementInfo() {
		return (PluginInfo) super.getElementInfo();
	}

	@Override
	protected void initResult() {
		result = new Plugin(getElementInfo());
	}

	@Override
	public Plugin build() throws Exception {
		return (Plugin) super.build();
	}

	@Override
	protected void transformData() {
		StandartXMLTransformer transformer = new StandartXMLTransformer(getSource());
		String data = transformer.transform();
		data = replaceVariables(data);
		if (getElementInfo().getPostProcessProcName() != null) {
			PluginPostProcessJythonGateway gateway =
				new PluginPostProcessJythonGateway(getCallContext(), getElementInfo(), data);
			String[] params = gateway.postProcess();
			result.getParams().addAll(Arrays.asList(params));
		} else {
			result.getParams().add(data);
		}
	}

	@Override
	protected void correctSettingsAndData() {
		result.setCreateProc("create" + TextUtils.capitalizeWord(getElementInfo().getPlugin()));
		result.getRequiredJS().add(
				String.format("%s/%s/%s/%s.js", AppProps.SOLUTIONS_DIR, AppProps.getUserDataId(),
						getPluginDir(), getElementInfo().getPlugin()));
		readComponents();
	}

	private void readComponents() {
		List<String> comps = readImportFile(getPluginDir() + "/" + IMPORT_TXT);
		for (String comp : comps) {
			addImport(comp, "scriptList.txt", result.getRequiredJS());
			addImport(comp, "styleList.txt", result.getRequiredCSS());
		}
	}

	private void addImport(final String comp, final String fileName, final List<String> list) {
		List<String> deps = readImportFile(COMPONENTS_DIR + "/" + comp + "/" + fileName);
		for (String dep : deps) {
			list.add(String.format("%s/%s/%s/%s/%s", AppProps.SOLUTIONS_DIR,
					AppProps.getUserDataId(), COMPONENTS_DIR, comp, dep));
		}
	}

	private List<String> readImportFile(final String fileName) {
		List<String> res = new ArrayList<>();
		File compList = new File(AppProps.getUserDataCatalog() + "/" + fileName);
		if (!compList.exists()) {
			return res;
		}

		String list;
		try {
			InputStream is = AppProps.loadUserDataToStream(fileName);
			list = TextUtils.streamToString(is);
		} catch (IOException e) {
			throw new SettingsFileOpenException(fileName, SettingsFileType.IMPORT_LIST);
		}
		String[] compNames = list.split("\\r?\\n");
		for (String name : compNames) {
			res.add(name);
		}
		return res;
	}

	private String getPluginDir() {
		return String.format("%s/%s", PLUGINS_DIR, getElementInfo().getPlugin());
	}
}
