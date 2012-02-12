package ru.curs.showcase.app.server.ws;

import java.io.IOException;

import javax.jws.*;
import javax.jws.soap.SOAPBinding;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.curs.showcase.core.command.ShowcaseExportException;
import ru.curs.showcase.core.external.ExternalCommand;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Оболочка поверх ExternalCommand для работы с JAX-WS. Примечания - имена
 * параметров задавать обязательно, методов - нет.
 * 
 * @author den
 * 
 */
@WebService(targetNamespace = "http://showcase.curs.ru")
public class ShowcaseExternals {
	public static final String NOT_XML_OUTPUT_ERROR = "Ошибка решения: рабочая процедура вернула данные не в формате XML";

	@WebMethod
	@WebResult(name = "response")
	public String handle(@WebParam(name = "request") final String request, @WebParam(
			name = "procName") final String procName) throws ShowcaseExportException {
		ExternalCommand command = new ExternalCommand(request, procName);
		return command.executeForExport();
	}

	@WebMethod
	@WebResult(name = "responseAnyXML")
	@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.LITERAL)
	public ResponseAnyXML handleXML(
			@WebParam(name = "requestAnyXML") final RequestAnyXML requestXML, @WebParam(
					name = "procName", header = true) final String procName)
			throws ShowcaseExportException {
		String requestStr = XMLUtils.documentToString(XMLUtils.objectToXML(requestXML));
		ExternalCommand command = new ExternalCommand(requestStr, procName);
		String responseStr = command.executeForExport();
		try {
			Document doc = XMLUtils.stringToDocument(responseStr);
			return (ResponseAnyXML) XMLUtils.xmlToObject(doc, ResponseAnyXML.class);
		} catch (SAXException | IOException e) {
			throw new ShowcaseExportException(
					NOT_XML_OUTPUT_ERROR);
		}
	}

}
