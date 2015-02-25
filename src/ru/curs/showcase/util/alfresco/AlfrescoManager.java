package ru.curs.showcase.util.alfresco;

import java.io.*;
import java.nio.charset.Charset;

import org.apache.http.*;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.json.*;
import org.python.core.*;

/**
 * Класс интеграции Showcase и Alfresco.
 * 
 */
public final class AlfrescoManager {

	private static final String DEF_ENCODING = "UTF-8";
	private static final int HTTP_OK = 200;
	private static final int RESULT_OK = 0;
	private static final int RESULT_ERROR = 1;

	private AlfrescoManager() {
	}

	public static AlfrescoLoginResult login(final String alfURL, final String alfUser,
			final String alfPass) {

		AlfrescoLoginResult ar = new AlfrescoLoginResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost = new HttpPost(alfURL + "/service/api/login");
			httppost.setHeader("Content-type", "application/json");
			httppost.setEntity(new StringEntity("{\"username\" : \"" + alfUser
					+ "\",\"password\" : \"" + alfPass + "\"}"));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					ar.setResult(RESULT_OK);
					ar.setTicket(jo.getJSONObject("data").getString("ticket"));
				} else {
					ar.setErrorMessage(resContent);
				}
			} else {
				ar.setErrorMessage("HTTP-запрос логирования вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);

		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;
	}

	public static AlfrescoUploadFileResult uploadFile(final String fileName,
			final InputStream file, final String alfURL, final String alfTicket,
			final PyDictionary alfUploadParams) {

		AlfrescoUploadFileResult ar = new AlfrescoUploadFileResult();
		ar.setResult(RESULT_ERROR);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost =
				new HttpPost(alfURL + "/service/api/upload?alf_ticket=" + alfTicket);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.setCharset(Charset.forName(DEF_ENCODING));

			builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, fileName);

			for (int i = 0; i < alfUploadParams.items().__len__(); i++) {
				PyTuple tup = (PyTuple) alfUploadParams.items().__getitem__(i);
				builder.addPart(tup.__getitem__(0).toString(), new StringBody(tup.__getitem__(1)
						.toString(), ContentType.DEFAULT_TEXT));
			}

			httppost.setEntity(builder.build());

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resContent = EntityUtils.toString(resEntity);
				if (response.getStatusLine().getStatusCode() == HTTP_OK) {
					JSONTokener jt = new JSONTokener(resContent);
					JSONObject jo = new JSONObject(jt);

					ar.setResult(RESULT_OK);
					ar.setNodeRef(jo.getString("nodeRef"));
				} else {
					ar.setErrorMessage(EntityUtils.toString(resEntity));
				}
			} else {
				ar.setErrorMessage("HTTP-запрос загрузки файла в Alfresco вернул пустые данные.");
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			ar.setResult(RESULT_ERROR);
			ar.setErrorMessage(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ar;

	}
}
