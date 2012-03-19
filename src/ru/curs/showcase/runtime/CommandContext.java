package ru.curs.showcase.runtime;

import java.util.Map;

import org.slf4j.MDC;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.showcase.app.api.*;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * Контекст выполнения текущей команды. Включает в себя контекст текущей сессии.
 * 
 * @author den
 * 
 */
public class CommandContext implements SerializableElement, Assignable<CommandContext>,
		GWTClonable, AbstractCommandContext {

	private static final long serialVersionUID = 8694977102691236398L;

	private String commandName;

	/**
	 * Идентификатор текущего запроса к сервисному слою.
	 */
	private String requestId;

	private String userName;

	private String userdata;

	public CommandContext() {
		super();
	}

	public CommandContext(final String aCommandName, final String id) {
		super();
		commandName = aCommandName;
		requestId = id;
		userdata = AppInfoSingleton.getAppInfo().getCurUserDataId();
		userName = SessionUtils.getCurrentSessionUserName();
	}

	// CHECKSTYLE:OFF
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commandName == null) ? 0 : commandName.hashCode());
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((userdata == null) ? 0 : userdata.hashCode());
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
		if (userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!userName.equals(other.userName)) {
			return false;
		}
		if (userdata == null) {
			if (other.userdata != null) {
				return false;
			}
		} else if (!userdata.equals(other.userdata)) {
			return false;
		}
		return true;
	}

	// CHECKSTYLE:ON

	@Override
	public AbstractCommandContext gwtClone() {
		CommandContext context = new CommandContext(commandName, requestId);
		context.userdata = userdata;
		context.userName = userName;
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
		if (userdata == null) {
			userdata = aSource.userdata;
		}
		if (userName == null) {
			userName = aSource.userName;
		}
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	public String getRequestIdSafe() {
		return requestId != null ? requestId : "";
	}

	@Override
	public void setRequestId(final String aRequestId) {
		requestId = aRequestId;
	}

	@Override
	public String getCommandName() {
		return commandName;
	}

	public String getCommandNameSafe() {
		return commandName != null ? commandName : "";
	}

	@Override
	public void setCommandName(final String aCommandName) {
		commandName = aCommandName;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	public String getUserNameSafe() {
		return userName != null ? userName : "";
	}

	@Override
	public void setUserName(final String aUserName) {
		userName = aUserName;
	}

	@Override
	public String getUserdata() {
		return userdata;
	}

	public String getUserdataSafe() {
		return userdata != null ? userdata : "";
	}

	@Override
	public void setUserdata(final String aUserdata) {
		userdata = aUserdata;
	}

	public void toMDC() {
		MDC.put(GeneralXMLHelper.USERNAME_TAG, getUserNameSafe());
		MDC.put(ExchangeConstants.URL_PARAM_USERDATA, getUserdataSafe());
		MDC.put(GeneralXMLHelper.REQUEST_ID_TAG, getRequestIdSafe());
		MDC.put(GeneralXMLHelper.COMMAND_NAME_TAG, getCommandNameSafe());
	}

	public void fromMDC() {
		@SuppressWarnings("unchecked")
		Map<String, String> params = MDC.getCopyOfContextMap();
		if (params != null) {
			setUserName(params.get(GeneralXMLHelper.USERNAME_TAG));
			setUserdata(params.get(ExchangeConstants.URL_PARAM_USERDATA));
			setRequestId(params.get(GeneralXMLHelper.REQUEST_ID_TAG));
			setCommandName(params.get(GeneralXMLHelper.COMMAND_NAME_TAG));
		}
	}

}
