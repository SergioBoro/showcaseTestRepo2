package ru.curs.showcase.app.server;

import java.io.IOException;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.app.server.ws.*;
import ru.curs.showcase.model.command.ShowcaseExportException;
import ru.curs.showcase.model.external.ExternalCommand;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Оболочка поверх ExternalCommand для работы с JAX-WS. Примечания - имена
 * параметров задавать обязательно, методов - нет.
 * 
 * @author den
 * 
 */
@WebService
public class ShowcaseExternals {
	@WebMethod
	@WebResult(name = "response")
	public String handle(@WebParam(name = "request") final String request, @WebParam(
			name = "procName") final String procName) throws ShowcaseExportException {
		ExternalCommand command = new ExternalCommand(request, procName);
		return command.executeForExport();
	}

	@WebMethod
	@WebResult(name = "responseXML")
	@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
	public ResponseXML handleXML(@WebParam(name = "requestXML") final RequestXML requestXML)
			throws ShowcaseExportException {
		String requestStr = XMLUtils.documentToString(XMLUtils.objectToXML(requestXML));
		ExternalCommand command = new ExternalCommand(requestStr, requestXML.getProcName());
		String responseStr = command.executeForExport();
		try {
			Document doc = XMLUtils.stringToDocument(responseStr);
			return (ResponseXML) XMLUtils.xmlToObject(doc, ResponseXML.class);
		} catch (SAXException | IOException e) {
			throw new ShowcaseExportException(
					"Ошибка решения: рабочая процедура вернула данные не в формате XML");
		}

	}
}
