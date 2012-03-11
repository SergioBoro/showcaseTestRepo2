package ru.curs.showcase.core.html.plugin;

import java.io.*;
import java.util.*;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.html.*;
import ru.curs.showcase.core.event.EventFactory;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.TextUtils;
import ru.curs.showcase.util.exception.*;
import ru.curs.showcase.util.xml.*;

/**
 * Фабрика для создания UI плагинов.
 * 
 * @author den
 * 
 */
public final class PluginFactory extends HTMLBasedElementFactory {

	public static final String COMPONENTS_DIR = "libraries";
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
		String adapterOnTomcat =
			checkImport(getPluginDir(), getElementInfo().getPlugin() + ".js",
					SettingsFileType.PLUGIN_ADAPTER);
		result.getRequiredJS().add(adapterOnTomcat);
		readComponents();
	}

	private String checkImport(final String dir, final String adapterFile,
			final SettingsFileType fileType) {
		String adapter = String.format("%s/%s", dir, adapterFile);
		String adapterOnTomcat =
			String.format("%s/%s/%s", AppProps.SOLUTIONS_DIR, AppProps.getUserDataId(), adapter);
		File file = new File(AppProps.getUserDataCatalog() + "/" + adapter);
		if (!file.exists()) {
			throw new SettingsFileOpenException(adapter, fileType);
		}
		return adapterOnTomcat;
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
			if (dep.trim().isEmpty()) {
				continue;
			}
			String adapterOnTomcat =
				checkImport(COMPONENTS_DIR + "/" + comp, dep, SettingsFileType.LIBRARY_ADAPTER);
			list.add(adapterOnTomcat);
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
			if (name.trim().isEmpty()) {
				continue;
			}
			res.add(name);
		}
		return res;
	}

	private String getPluginDir() {
		return String.format("%s/%s", PLUGINS_DIR, getElementInfo().getPlugin());
	}

	@Override
	protected void addHandlers(final EventFactory<HTMLEvent> factory) {
		super.addHandlers(factory);

		SAXTagHandler handler = new StartTagSAXHandler() {
			@Override
			public Object handleStartTag(final String aNamespaceURI, final String aLname,
					final String aQname, final Attributes attrs) {
				String value;
				Integer intValue;
				if (attrs.getIndex(WIDTH_TAG) > -1) {
					value = attrs.getValue(WIDTH_TAG);
					intValue = TextUtils.getIntSizeValue(value);
					result.getSize().setWidth(intValue);
				}
				if (attrs.getIndex(HEIGHT_TAG) > -1) {
					value = attrs.getValue(HEIGHT_TAG);
					intValue = TextUtils.getIntSizeValue(value);
					result.getSize().setHeight(intValue);
				}
				return null;
			}

			@Override
			protected String[] getStartTags() {
				String[] tags = { PROPS_TAG };
				return tags;
			}

		};
		factory.addHandler(handler);
	}
}
