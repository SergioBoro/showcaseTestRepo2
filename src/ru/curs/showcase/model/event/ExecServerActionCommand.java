package ru.curs.showcase.model.event;

import java.io.UnsupportedEncodingException;
import java.util.*;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.exception.ServerLogicError;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Команда, выполняющая серверные действия.
 * 
 * @author den
 * 
 */
public final class ExecServerActionCommand extends ServiceLayerCommand<Void> {

	private static final String NO_SERVER_ACTIVIVTY_IMPL_ERROR = "%s серверное действие еще не реализовано";
	private static final String SERVER_ACTION_EXECUTED = "Выполнено действие на сервере: ";

	@InputParam
	public Action getAction() {
		return action;
	}

	private final Action action;

	public ExecServerActionCommand(final Action aAction) {
		super(aAction.getContext());
		action = aAction;
	}

	@Override
	protected void initSessionContext() throws UnsupportedEncodingException {
		if (action.getContext() == null) {
			return;
		}
		CompositeContext context = action.getContext();

		if (context.getSession() != null) {
			return;
		}
		String sessionContext = XMLSessionContextGenerator.generate(context);

		action.setSessionContext(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		action.setSessionContext((Map<String, List<String>>) null);
	}

	@Override
	protected void mainProc() throws Exception {
		ActivityGateway gateway = null;
		for (Activity act : action.getServerActivities()) {
			switch (act.getType()) {
			case SP:
				gateway = new ActivityDBGateway();
				break;
			case JYTHON:
				gateway = new ActivityJythonGateway();
				break;
			default:
				throw new ServerLogicError(String.format(
						NO_SERVER_ACTIVIVTY_IMPL_ERROR, act.getType().toString()));
			}
			gateway.exec(act);
			LOGGER.info(SERVER_ACTION_EXECUTED + getSerializer().serialize(act));
		}
	}

}