package ru.curs.showcase.model.command;

import java.util.UUID;

/**
 * Контекст выполнения текущей команды.
 * 
 * @author den
 * 
 */
@SuppressWarnings("rawtypes")
public class CommandContext {

	private final ServiceLayerCommand command;

	/**
	 * Идентификатор текущего запроса к сервисному слою.
	 */
	private final UUID requestId = UUID.randomUUID();

	public UUID getRequestId() {
		return requestId;
	}

	public String getCommandName() {
		return command.getClass().getSimpleName();
	}

	public CommandContext(final ServiceLayerCommand aCommand) {
		super();
		command = aCommand;
	}

}
