package ru.curs.showcase.app.server;

import javax.jws.*;

import ru.curs.showcase.model.command.ExternalCommand;

/**
 * Оболочка поверх ExternalCommand для работы с JAX-WS.
 * 
 * @author den
 * 
 */
@WebService
public class ShowcaseExternals {
	@WebMethod(operationName = "handle")
	public String handle(@WebParam(name = "request") final String request, @WebParam(
			name = "procName") final String procName) {
		ExternalCommand command = new ExternalCommand(request, procName);
		return command.execute();
	}
}
