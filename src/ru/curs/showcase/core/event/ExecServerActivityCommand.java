package ru.curs.showcase.core.event;

import java.io.UnsupportedEncodingException;
import java.util.*;

import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.xml.XMLSessionContextGenerator;

/**
 * Команда, выполняющая серверные действия.
 * 
 * @author den
 * 
 */
public final class ExecServerActivityCommand extends ServiceLayerCommand<Void> {

	private static final String SERVER_ACTION_EXECUTED = "Выполнено действие на сервере: ";

	@InputParam
	public Action getAction() {
		return action;
	}

	private final Action action;

	public ExecServerActivityCommand(final Action aAction) {
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
			ServerActivitySelector selector = new ServerActivitySelector(act);
			gateway = selector.getGateway();
			gateway.exec(act);
			LOGGER.info(SERVER_ACTION_EXECUTED + getSerializer().serialize(act));
		}
	}

}
