package ru.curs.showcase.app.api.plugin;

import java.io.Serializable;

import ru.curs.showcase.app.api.datapanel.PluginInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Данные для выполнения запроса к серверу.
 * 
 * @author bogatov
 * 
 */
public class RequestData implements Serializable {
	private static final long serialVersionUID = 1L;
	private CompositeContext context;
	private PluginInfo elInfo;
	private String xmlParams;

	public CompositeContext getContext() {
		return context;
	}

	public void setContext(final CompositeContext oContext) {
		this.context = oContext;
	}

	public PluginInfo getElInfo() {
		return elInfo;
	}

	public void setElInfo(final PluginInfo oElInfo) {
		this.elInfo = oElInfo;
	}

	public String getXmlParams() {
		return this.xmlParams;
	}

	public void setXmlParams(final String sXmlParams) {
		this.xmlParams = sXmlParams;
	}
}
