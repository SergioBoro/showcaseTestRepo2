package ru.curs.showcase.core.external;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.*;
import ru.curs.showcase.core.command.*;

/**
 * Внешняя команда. Вызывается с помощью сервлета, веб-сервиса или другого RPC
 * вызова. Не связана ни с одним UI элементом Showcase.
 * 
 * @author den
 * 
 */
public class ExternalCommand extends ServiceLayerCommand<String> {

	private final String request;
	private final String procName;

	@InputParam
	protected String getRequest() {
		return request;
	}

	@InputParam
	protected String getProcName() {
		return procName;
	}

	public ExternalCommand(final String aRequest, final String aProcName) {
		super(getDefaultUserDataContext());
		request = aRequest;
		procName = aProcName;
	}

	private static CompositeContext getDefaultUserDataContext() {
		return new CompositeContext();
	}

	@Override
	protected void mainProc() throws Exception {
		SourceSelector<ExternalCommandGateway> selector =
			new SourceSelector<ExternalCommandGateway>(procName) {

				@Override
				public ExternalCommandGateway getGateway() {
					if (sourceType() == SourceType.JYTHON) {
						return new JythonExternalCommandGateway();
					}
					return new DBExternalCommandGateway();
				}
			};
		ExternalCommandGateway gateway = selector.getGateway();
		setResult(gateway.handle(request, procName));
	}

}