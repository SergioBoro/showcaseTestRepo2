package ru.curs.showcase.model.command;

import java.io.UnsupportedEncodingException;
import java.util.*;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.model.event.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.SessionContextGenerator;

/**
 * Команда, выполняющая серверные действия.
 * 
 * @author den
 * 
 */
public final class ExecServerActionCommand extends ServiceLayerCommand<Void> {

	private static final String SERVER_ACTION_EXECUTED = "Выполнено действие на сервере: ";

	private final Action action;

	public ExecServerActionCommand(final String aSessionId, final Action aAction) {
		super(aSessionId, aAction.getContext());
		action = aAction;
	}

	@Override
	protected void initContext() throws UnsupportedEncodingException {
		if (action.getContext() == null) {
			return;
		}
		CompositeContext context = action.getContext();

		if (context.getSession() != null) {
			return;
		}
		String sessionContext = SessionContextGenerator.generate(context);

		action.setSessionContext(sessionContext);
		AppInfoSingleton.getAppInfo().setCurUserDataIdFromMap(context.getSessionParamsMap());
		action.setSessionContext((Map<String, List<String>>) null);
	}

	@Override
	protected void mainProc() throws Exception {
		ActivityGateway gateway = new ActivityDBGateway();
		for (Activity act : action.getServerActivities()) {
			gateway.exec(act);
			LOGGER.info(SERVER_ACTION_EXECUTED + act.toString());
		}
	}

}
