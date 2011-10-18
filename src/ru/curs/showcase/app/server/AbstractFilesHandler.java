package ru.curs.showcase.app.server;

import java.io.*;

import javax.servlet.http.*;

import org.apache.commons.fileupload.FileUploadException;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.services.GeneralException;
import ru.curs.showcase.model.command.GeneralExceptionFactory;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

/**
 * Базовый обработчик для работы с файлами.
 * 
 * @author den
 * 
 */
public abstract class AbstractFilesHandler {
	/**
	 * Контекст.
	 */
	private CompositeContext context;
	/**
	 * Описание элемента.
	 */
	private DataPanelElementInfo elementInfo;

	/**
	 * Обрабатываемые HTTP запрос.
	 */
	private HttpServletRequest request;
	/**
	 * Формируемый HTTP ответ.
	 */
	private HttpServletResponse response;

	/**
	 * Основной метод обработчика.
	 * 
	 * @param aRequest
	 *            - запрос.
	 * @param aResponse
	 *            - ответ.
	 * @throws GeneralException
	 */
	public void handle(final HttpServletRequest aRequest, final HttpServletResponse aResponse)
			throws GeneralException {
		request = aRequest;
		response = aResponse;

		try {
			handleTemplateMethod();
		} catch (SerializationException e) {
			GeneralExceptionFactory.build(e);
		} catch (IOException e) {
			GeneralExceptionFactory.build(e);
		} catch (FileUploadException e) {
			GeneralExceptionFactory.build(e);
		}
	}

	private void handleTemplateMethod() throws SerializationException, IOException,
			GeneralException, FileUploadException {
		getParams();
		processFiles();
		fillResponse();
	}

	/**
	 * Функция для заполнения response.
	 * 
	 * @throws IOException
	 */
	protected abstract void fillResponse() throws IOException;

	/**
	 * Функция считывания параметров запроса на скачивание.
	 * 
	 * @throws SerializationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	protected abstract void getParams() throws SerializationException, FileUploadException,
			IOException;

	/**
	 * Функция получения файла.
	 * 
	 * @throws GeneralException
	 */
	protected abstract void processFiles() throws GeneralException;

	/**
	 * Функция десериализации объекта, переданного в теле запроса.
	 * 
	 * @param data
	 *            - строка с urlencoded объектом.
	 * @return - объект.
	 * @throws SerializationException
	 */
	protected Object deserializeObject(final String data) throws SerializationException {
		ServerSerializationStreamReader streamReader =
			new ServerSerializationStreamReader(Thread.currentThread().getContextClassLoader(),
					null);
		streamReader.prepareToRead(data);
		return streamReader.readObject();
	}

	public CompositeContext getContext() {
		return context;
	}

	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(final HttpServletRequest aRequest) {
		request = aRequest;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(final HttpServletResponse aResponse) {
		response = aResponse;
	}

	public void setContext(final CompositeContext aContext) {
		this.context = aContext;
	}

	public void setElementInfo(final DataPanelElementInfo aElementInfo) {
		this.elementInfo = aElementInfo;
	}

}