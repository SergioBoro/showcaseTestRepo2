package ru.curs.showcase.security.logging;

import javax.servlet.http.HttpServletRequest;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.core.command.ServiceLayerCommand;
import ru.curs.showcase.runtime.UserDataUtils;
import ru.curs.showcase.security.logging.Event.TypeEvent;

/**
 * Команда для обработки события с использованием хранимой процедуры.
 * 
 * @author bogatov
 * 
 */
public class SecurityLoggingCommand extends ServiceLayerCommand<Void> {
	private final HttpServletRequest request;
	private final TypeEvent typeEvent;

	public SecurityLoggingCommand(final CompositeContext context,
			final HttpServletRequest oRequest, final TypeEvent eTypeEvent) {
		super(context);
		this.request = oRequest;
		this.typeEvent = eTypeEvent;
	}

	@Override
	protected void mainProc() throws Exception {
		String procName = UserDataUtils.getOptionalProp("security.logging.proc");
		if (procName != null && !procName.isEmpty()) {
			String sesionid = request != null ? request.getSession().getId() : null;
			String ip = request != null ? request.getRemoteAddr() : null;
			Event event = new Event(typeEvent, getContext(), sesionid, ip);
			SecurityLoggingSelector selector = new SecurityLoggingSelector(procName);
			SecurityLoggingGateway gateway = selector.getGateway();
			gateway.doLogging(event);
		}
	}

}
