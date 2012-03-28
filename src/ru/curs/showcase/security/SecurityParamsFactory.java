package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;

import ru.curs.showcase.runtime.UserdataUtils;

/**
 * @author anlug
 * 
 *         Класс, позволяющий получить url для сервера аутентификации.
 * 
 */
public final class SecurityParamsFactory {

	/**
	 * Имя параметра в файле настроек, содержащего путь к AuthServer.
	 */
	public static final String AUTH_SERVER_URL_PARAM = "security.authserverurl";

	public static final String APP_PROP_READ_ERROR = "Не удалось считать " + AUTH_SERVER_URL_PARAM
			+ " из app.properties";

	/**
	 * Имя параметра в файле настроек, содержащего локальный путь к AuthServer.
	 */
	public static final String LOCAL_AUTH_SERVER_URL_PARAM = "local.security.authserverurl";

	private SecurityParamsFactory() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * Возвращает URL сервера аутентификации в строке.
	 * 
	 * @return - url.
	 */
	public static String getAuthServerUrl() {

		return UserdataUtils.getGeneralRequiredProp(AUTH_SERVER_URL_PARAM);

	}

	/**
	 * 
	 * Возвращает локальный URL сервера аутентификации в строке. Локальный URL
	 * нужен для случая, когда Showcase и AuthServer установлены на одном
	 * компьютере, на котором нет доступа к DNS серверам, но при этом auth куки
	 * должны быть привязаны к полному DNS имени AuthServer.
	 * 
	 * @return - url.
	 * @throws ProtocolException
	 */
	// public static String getLocalAuthServerUrl1() {
	// String result =
	// AppProps.getOptionalValueByName(LOCAL_AUTH_SERVER_URL_PARAM);
	// if (result == null) {
	// result = getAuthServerUrl();
	// }
	// // try
	//
	// URL server;
	//
	// try {
	// server = new URL(result);
	//
	// HttpURLConnection c = (HttpURLConnection) server.openConnection();
	// c.setRequestMethod("GET");
	// c.setDoInput(true);
	// c.connect();
	//
	// if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
	// return "true1";
	// }
	//
	// } catch (IOException e) {
	// LOGGER.info(String.format(LOGIN_INFO));
	// }
	//
	// return "false1";
	//
	// }

	public static String getLocalAuthServerUrl() {
		String result = UserdataUtils.getGeneralOptionalProp(LOCAL_AUTH_SERVER_URL_PARAM);
		if (result == null) {
			result = getAuthServerUrl();
		}
		return result;
	}

	/**
	 * 
	 * Функция, проверяющая возможность подключения к AuthServer с запросом в
	 * строке authGifSrc, и в случае доступности AuthServer возвращает url =
	 * authGifSrc иначе возвращает url = "".
	 * 
	 * @param authGifSrc
	 *            - url для проверки
	 * @return
	 */
	public static String correctAuthGifSrcRequestInCaseOfInaccessibility(final String authGifSrc) {
		String result = "";
		URL server;
		try {
			server = new URL(authGifSrc);

			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod("GET");
			c.setDoInput(true);
			c.connect();

			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				result = authGifSrc;
			}

		} catch (IOException e) {

			result = "";
		}
		return result;

	}

}
