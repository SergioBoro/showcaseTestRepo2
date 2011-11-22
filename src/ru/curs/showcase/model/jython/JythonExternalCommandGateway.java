package ru.curs.showcase.model.jython;

import ru.curs.showcase.app.api.UserMessage;

/**
 * Jython шлюз для трансформации данных или выполнения команд, полученных из
 * WebServices или сервлета.
 * 
 * @author den
 * 
 */
public class JythonExternalCommandGateway extends JythonQuery<String> {

	private String request;
	private String source;

	public String handle(final String aRequest, final String aSource) {
		request = aRequest;
		source = aSource;
		runTemplateMethod();
		return getResult();
	}

	@Override
	protected void execute() {
		setResult(getProc().handle(request));
	}

	@Override
	protected String getJythonProcName() {
		return source;
	}

	/*
	 * Не используется в данном шлюзе. (non-Javadoc)
	 * 
	 * @see ru.curs.showcase.model.jython.JythonQuery#getUserMessage()
	 */
	@Override
	protected UserMessage getUserMessage() {
		return null;
	}

}
