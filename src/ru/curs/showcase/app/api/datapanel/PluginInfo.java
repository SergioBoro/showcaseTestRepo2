package ru.curs.showcase.app.api.datapanel;

import javax.xml.bind.annotation.*;

/**
 * Информация о элементе типа UI плагин.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "element")
@XmlAccessorType(XmlAccessType.FIELD)
public final class PluginInfo extends DataPanelElementInfo {

	private static final long serialVersionUID = -1192137836340386361L;

	private String plugin;

	public PluginInfo(final String id, final String aPlugin, final String aProcName) {
		super(id, DataPanelElementType.PLUGIN);
		plugin = aPlugin;
		setProcName(aProcName);
	}

	public String getPlugin() {
		return plugin;
	}

	public void setPluginName(final String aPlugin) {
		plugin = aPlugin;
	}

	public void addPostProcessProc(final String id, final String name) {
		DataPanelElementProc proc = new DataPanelElementProc();
		proc.setId(id);
		proc.setName(name);
		proc.setType(DataPanelElementProcType.POSTPROCESS);
		getProcs().put(proc.getId(), proc);

	}

	public PluginInfo() {
		super();
	}
}
