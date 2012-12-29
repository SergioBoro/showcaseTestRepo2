package ru.curs.showcase.core.selector;

import ru.beta2.extra.gwt.ui.selector.api.DataRequest;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.DataPanelElementCommand;

/**
 * Команда получения данных селектора компоненты XFORMS.
 * 
 * @author bogatov
 * 
 */
public class SelectorGetCommand extends DataPanelElementCommand<ResultSelectorData> {
	private final DataRequest dataRequest;

	public SelectorGetCommand(final DataRequest aDataRequest) {
		super((CompositeContext) aDataRequest.getAddData().getContext(),
				(DataPanelElementInfo) aDataRequest.getAddData().getElementInfo());
		this.dataRequest = aDataRequest;
	}

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.XFORMS;
	}

	@Override
	protected void mainProc() throws Exception {
		SelectorGatewayFactory gf = new SelectorGatewayFactory(this.dataRequest.getProcName());
		SelectorGateway gateway = gf.getGateway();
		ResultSelectorData result = gateway.getData(dataRequest);
		setResult(result);
	}

}
