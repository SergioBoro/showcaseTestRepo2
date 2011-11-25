package ru.curs.showcase.model.jython;

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
	protected Object execute() {
		return getProc().handle(request);
	}

	@Override
	protected String getJythonProcName() {
		return source;
	}

	public JythonExternalCommandGateway() {
		super(String.class);
	}

}
