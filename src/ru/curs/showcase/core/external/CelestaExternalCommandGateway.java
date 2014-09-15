package ru.curs.showcase.core.external;

import java.util.Random;

import org.python.core.PyObject;

import ru.curs.celesta.*;

/**
 * Celesta шлюз для трансформации данных или выполнения команд, полученных из
 * WebServices или сервлета.
 * 
 * @author anlug
 * 
 */
public class CelestaExternalCommandGateway implements ExternalCommandGateway {

	private String request;
	private String source;

	@Override
	public String handle(final String aRequest, final String aSource) {
		request = aRequest;
		source = aSource;
		String result = null;

		String tempSesId = String.format("WebService%08X", (new Random()).nextInt());
		try {
			Celesta.getInstance().login(tempSesId, "userCelestaSid");
			PyObject pObj = Celesta.getInstance().runPython(tempSesId, source, request);

			Object obj = pObj.__tojava__(Object.class);
			if (obj == null) {
				return null;
			}
			if (obj.getClass().isAssignableFrom(String.class)) {
				return (String) obj;
			}

		} catch (CelestaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				Celesta.getInstance().logout(tempSesId, false);
			} catch (CelestaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;

	}
}
