package ru.curs.showcase.model.command;

import ru.curs.showcase.app.api.ServerState;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.ServerStateFactory;

/**
 * Команда получения состояния сервера.
 * 
 * @author den
 * 
 */
public final class ServerStateGetCommand extends ServiceLayerCommand<ServerState> {

	public ServerStateGetCommand(final CompositeContext aContext) {
		super(aContext);
	}

	@Override
	protected void mainProc() throws Exception {
		setResult(ServerStateFactory.build(getSessionId()));
	}

}
