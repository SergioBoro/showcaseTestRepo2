package ru.curs.showcase.model.event;

import ru.curs.showcase.app.api.UserMessage;
import ru.curs.showcase.app.api.event.Activity;
import ru.curs.showcase.model.jython.JythonQuery;

/**
 * Шлюз для работы с Jython Server Activity.
 * 
 * @author den
 * 
 */
public class ActivityJythonGateway extends JythonQuery<UserMessage> implements
		ActivityGateway {
	private Activity activity;

	@Override
	public void exec(final Activity act) {
		activity = act;
		runTemplateMethod();
	}

	@Override
	public String getJythonProcName() {
		return activity.getName();
	}

	@Override
	public void execute() {
		setResult(getProc().execute(activity.getContext()));
	}

	@Override
	public UserMessage getUserMessage() {
		return getResult();
	}
}
