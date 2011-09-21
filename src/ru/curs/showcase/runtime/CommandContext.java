package ru.curs.showcase.runtime;

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

	private String commandName = null;

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
		return commandName;
	}

	public CommandContext(final String aCommandName, final String id) {
		super();
		commandName = aCommandName;
		requestId = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandName == null) ? 0 : commandName.hashCode());
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
		if (commandName == null) {
			if (other.commandName != null) {
				return false;
			}
		} else if (!commandName.equals(other.commandName)) {
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
		CommandContext context = new CommandContext(commandName, requestId);
		return context;
	}

	@Override
	public void assignNullValues(final CommandContext aSource) {
		if (aSource == null) {
			return;
		}
		if (commandName == null) {
			commandName = aSource.commandName;
		}
		if (requestId == null) {
			requestId = aSource.requestId;
		}
	}

	public void setRequestId(final String aRequestId) {
		requestId = aRequestId;
	}

	public void setCommandName(final String aCommandName) {
		commandName = aCommandName;
	}

}
