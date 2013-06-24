package ru.curs.showcase.security.logging;

import ru.curs.showcase.core.command.ServiceLayerCommand;
import ru.curs.showcase.runtime.UserDataUtils;

/**
 * Команда для обработки события с использованием хранимой процедуры.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingCommand extends ServiceLayerCommand<Void> {
	private final Event event;

	public SecurityLoggingCommand(final Event oEvent) {
		super(oEvent.getContext());
		this.event = oEvent;
	}

	@Override
	protected void mainProc() throws Exception {
		String procName = UserDataUtils.getOptionalProp("security.logging.proc");
		if (procName != null && !procName.isEmpty()) {
			SecurityLoggingSelector selector = new SecurityLoggingSelector(procName);
			SecurityLoggingGateway gateway = selector.getGateway();
			gateway.doLogging(event);
		}
	}

}
