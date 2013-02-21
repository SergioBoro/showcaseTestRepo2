package ru.curs.showcase.core.plugin;

import ru.beta2.extra.gwt.ui.plugin.RequestData;
import ru.curs.showcase.core.jython.*;

/**
 * Шлюз для получения данных, источником которых являются Jython скрипты.
 * 
 * @author bogatov
 * 
 */
public class GetDataPluginJythonGateway extends JythonQuery<JythonDTO> implements
		GetDataPluginGateway {
	private RequestData request;

	protected GetDataPluginJythonGateway() {
		super(JythonDTO.class);
	}

	@Override
	public ResultPluginData getData(final RequestData oRequest) throws Exception {
		this.request = oRequest;
		runTemplateMethod();
		JythonDTO jytResult = getResult();
		ResultPluginData result = new ResultPluginData();
		result.setData(jytResult.getData());
		return result;
	}

	@Override
	protected Object execute() {
		PluginAttributes pluginAttributes = new PluginAttributes();
		pluginAttributes.setParamMap(this.request.getParamMap());
		return getProc().getPluginData(request.getContext(), pluginAttributes);
	}

	@Override
	protected String getJythonProcName() {
		return this.request.getProcName();
	}

}