package ru.curs.showcase.util;

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

	// private static String alfURL = null;

	private AlfrescoManager() {
	}

	private static String login(final String alfURL, final String alfUser, final String alfPass) {
		// if (alfURL == null) {
		// alfURL =
		// UserDataUtils.getGeneralOptionalProp("alfresco.alfrescourl");
		// }

		String ticket = null;

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost = new HttpPost(alfURL + "/service/api/login");
			httppost.setHeader("Content-type", "application/json");
			httppost.setEntity(new StringEntity("{\"username\" : \"" + alfUser
					+ "\",\"password\" : \"" + alfPass + "\"}"));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();
			try {
				String resContent = null;
				if (resEntity != null) {
					resContent = EntityUtils.toString(resEntity);
					if (response.getStatusLine().getStatusCode() == HTTP_OK) {
						JSONTokener jt = new JSONTokener(resContent);
						JSONObject jo = new JSONObject(jt);

						ticket = jo.getJSONObject("data").getString("ticket");
					} else {
						throw new RuntimeException(resContent);
					}
				} else {
					throw new RuntimeException("HTTP-запрос логирования вернул пустые данные.");
				}
			} finally {
				EntityUtils.consume(resEntity);
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ticket;
	}

	public static String uploadFile(final String fileName, final InputStream file,
			final PyDictionary alfConnectParams, final PyDictionary alfUploadParams) {
		String error = null;

		String alfURL = "";
		String alfUser = "";
		String alfPass = "";
		for (int i = 0; i < alfConnectParams.items().__len__(); i++) {
			PyTuple tup = (PyTuple) alfConnectParams.items().__getitem__(i);
			if ("url".equalsIgnoreCase(tup.__getitem__(0).toString())) {
				alfURL = tup.__getitem__(1).toString();
			}
			if ("user".equalsIgnoreCase(tup.__getitem__(0).toString())) {
				alfUser = tup.__getitem__(1).toString();
			}
			if ("password".equalsIgnoreCase(tup.__getitem__(0).toString())) {
				alfPass = tup.__getitem__(1).toString();
			}
		}

		String ticket = null;
		try {
			ticket = login(alfURL, alfUser, alfPass);
		} catch (RuntimeException e) {
			e.printStackTrace();
			error = e.getMessage();
			return error;
		}

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		try {
			HttpPost httppost = new HttpPost(alfURL + "/service/api/upload?alf_ticket=" + ticket);

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
				if (response.getStatusLine().getStatusCode() != HTTP_OK) {
					error = EntityUtils.toString(resEntity);
				}
			} else {
				error = "HTTP-запрос загрузки файла в Alfresco вернул пустые данные.";
			}

			EntityUtils.consume(resEntity);
		} catch (Exception e) {
			e.printStackTrace();
			error = e.getMessage();
			return error;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return error;

	}
}
