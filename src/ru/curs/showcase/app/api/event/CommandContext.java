package ru.curs.showcase.app.api.event;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.*;

/**
 * Контекст выполнения текущей команды.
 * 
 * @author den
 * 
 */
public class CommandContext implements SerializableElement, Assignable<CommandContext>,
		GWTClonable {

	private static final long serialVersionUID = 8694977102691236398L;

	private String command = null;

	/**
	 * Идентификатор текущего запроса к сервисному слою.
	 */
	private String requestId = "";

	public String getRequestId() {
		return requestId;
	}

	public CommandContext() {
		super();
	}

	public String getCommandName() {
		return command;
	}

	public CommandContext(final String aCommandName, final String id) {
		super();
		command = aCommandName;
		requestId = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CommandContext)) {
			return false;
		}
		CommandContext other = (CommandContext) obj;
		if (command == null) {
			if (other.command != null) {
				return false;
			}
		} else if (!command.equals(other.command)) {
			return false;
		}
		if (requestId == null) {
			if (other.requestId != null) {
				return false;
			}
		} else if (!requestId.equals(other.requestId)) {
			return false;
		}
		return true;
	}

	@Override
	public CommandContext gwtClone() {
		CommandContext context = new CommandContext(command, requestId);
		return context;
	}

	@Override
	public void assignNullValues(final CommandContext aSource) {
		if (aSource == null) {
			return;
		}
		if (command == null) {
			command = aSource.command;
		}
		if (requestId == null) {
			requestId = aSource.requestId;
		}
	}

}
