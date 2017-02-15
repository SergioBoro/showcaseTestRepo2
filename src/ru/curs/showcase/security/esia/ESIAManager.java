package ru.curs.showcase.security.esia;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.json.*;

/**
 * Класс интеграции Showcase и ESIA.
 * 
 */
public final class ESIAManager {

	private ESIAManager() {
	}

	private static String getTimeStamp() {
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		return utc.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss Z"));
	}

	public static String getAuthorizationURL() {

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("client_id", "PNMO08771");
		// params.put("redirect_uri", "https://www.yandex.ru/");
		params.put("redirect_uri", "http://localhost:8081/Showcase/esia");
		params.put("scope", "openid http://esia.gosuslugi.ru/usr_inf");
		params.put("response_type", "code");
		params.put("state", "b5fbf220-5a2e-4771-a9f4-5b3fe2ce2e28");
		params.put("timestamp", getTimeStamp());
		params.put("access_type", "offline");

		putClientSecret(params);

		String url = "https://esia-portal1.test.gosuslugi.ru/" + "aas/oauth2/ac" + "?";

		String query = "";

		for (String key : params.keySet()) {

			try {
				if (!query.isEmpty()) {
					query = query + "&";
				}

				query = query + key + "=" + URLEncoder.encode(params.get(key), "UTF-8");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		url = url + query;

		return url;

	}

	private static void putClientSecret(final HashMap<String, String> params) {

		Process proc;
		try {

			String opensslFileName = "D:\\workspace8\\esia\\OpenSSL\\bin\\openssl.exe";
			String certFileName = "D:\\workspace8\\esia\\src\\res\\test.pem";
			String keyFileName = "D:\\workspace8\\esia\\src\\res\\key.pem";
			String inFileName = "D:\\workspace8\\esia\\src\\res\\in.txt";
			String outFileName = "D:\\workspace8\\esia\\src\\res\\out.txt";

			FileWriter in = new FileWriter(inFileName, false);
			in.write(params.get("scope") + params.get("timestamp") + params.get("client_id")
					+ params.get("state"));
			in.close();

			proc = Runtime.getRuntime()
					.exec(String.format(
							"%s smime -sign -md sha256 -in %s -signer %s -inkey %s -passin pass:changeit -out %s -outform DER",
							opensslFileName, inFileName, certFileName, keyFileName, outFileName));

			proc.waitFor();
			proc.destroy();

			FileInputStream out = new FileInputStream(outFileName);
			byte[] buffer = new byte[out.available()];
			out.read(buffer, 0, out.available());
			out.close();

			params.put("client_secret",
					new String(Base64.getUrlEncoder().encode(buffer), "UTF-8"));

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ESIAUserInfo getUserInfo(final String code) {

		ESIAUserInfo ui = new ESIAUserInfo();

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("client_id", "PNMO08771");
		params.put("code", code);
		params.put("grant_type", "authorization_code");
		params.put("redirect_uri", "https://www.yandex.ru/");
		params.put("timestamp", getTimeStamp());
		params.put("token_type", "Bearer");
		params.put("scope", "openid http://esia.gosuslugi.ru/usr_inf");
		params.put("state", "b5fbf220-5a2e-4771-a9f4-5b3fe2ce2e28");

		putClientSecret(params);

		// --------------------------------------------------

		long oid = 0;
		String accessToken = null;

		HttpsURLConnection conn = null;
		try {
			try {

				StringBuilder postData = new StringBuilder();
				for (Entry<String, String> param : params.entrySet()) {
					if (postData.length() != 0) {
						postData.append('&');
					}
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
				}
				byte[] postDataBytes = postData.toString().getBytes("UTF-8");

				URL url = new URL("https://esia-portal1.test.gosuslugi.ru/" + "aas/oauth2/te");

				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.getOutputStream().write(postDataBytes);

				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					Reader in =
						new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;) {
						sb.append((char) c);
					}
					String resContent = sb.toString();

					// System.out.println(resContent);

					JSONObject jo = new JSONObject(resContent);

					String idToken = jo.getString("id_token");
					accessToken = jo.getString("access_token");

					// System.out.println(idToken);

					String[] idTokenParts = idToken.split("\\.");

					String payload =
						new String(Base64.getUrlDecoder().decode(idTokenParts[1]), "UTF-8");

					// System.out.println(payload);

					jo = new JSONObject(payload);

					oid = jo.getJSONObject("urn:esia:sbj").getLong("urn:esia:sbj:oid");

					// System.out.println(oid);

				} else {

				}

			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		// --------------------------------------------------

		conn = null;
		try {
			try {

				URL url = new URL(
						String.format("https://esia-portal1.test.gosuslugi.ru/rs/prns/%s", oid));

				conn = (HttpsURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoInput(true);
				conn.setDoOutput(true);

				conn.connect();

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

					Reader in =
						new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;) {
						sb.append((char) c);
					}
					String resContent = sb.toString();

					// System.out.println(resContent);

					JSONObject jo = new JSONObject(resContent);

					ui.setOid(oid);
					ui.setSnils(jo.getString("snils"));
					ui.setTrusted(jo.getBoolean("trusted"));
					ui.setFirstName(jo.getString("firstName"));
					ui.setLastName(jo.getString("lastName"));
					ui.setMiddleName(jo.getString("middleName"));
					ui.setGender(jo.getString("gender"));
					ui.setBirthDate(jo.getString("birthDate"));
					ui.setBirthPlace(jo.getString("birthPlace"));

				} else {

				}

			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return ui;
	}

}
