package ru.curs.showcase.app.server;

import javax.jws.*;

import ru.curs.showcase.model.command.ShowcaseExportException;
import ru.curs.showcase.model.external.*;

/**
 * Оболочка поверх ExternalCommand для работы с JAX-WS.
 * 
 * @author den
 * 
 */
@WebService
public class ShowcaseExternals {
	@WebMethod(operationName = "handle")
	@WebResult(name = "response")
	public String handle(@WebParam(name = "request") final String request, @WebParam(
			name = "procName") final String procName) throws ShowcaseExportException {
		ExternalCommand command = new ExternalCommand(request, procName);
		return command.executeForExport();
	}
}
