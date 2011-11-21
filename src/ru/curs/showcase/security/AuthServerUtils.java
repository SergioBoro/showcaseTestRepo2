package ru.curs.showcase.security;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.xml.transform.TransformerException;

import org.slf4j.*;

/**
 * Набор утилит (функций) для работы с сервлетами сервера аутентификации.
 */
public final class AuthServerUtils {
	private static final String REQUEST_METHOD = "GET";

	public static final String APP_PROP_READ_ERROR =
		"Не удалось считать security.authserverurl из app.properties";

	private static final String LOGOUT_WARN = "Не удалось разлогиниться с AuthServer";
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthServerUtils.class);

	public static final String AUTH_SERVER_DATA_ERROR =
		"Ошибка при разборе данных, возвращенных AuthServer: ";
	/**
	 * Экземпляр-псевдоним псевдоним для работы с сервлетами сервера
	 * аутентификации.
	 */
	private static AuthServerUtils theAuthServerAlias;

	/**
	 * Адрес сервера аутентификации.
	 */
	private final String authServerURL;

	private AuthServerUtils(final String url) {
		this.authServerURL = url;
	}

	/**
	 * Возвращает экземпляр-псевдоним для работы с сервером аутентификации (если
	 * таковой имеется).
	 * 
	 * @return theAuthServerAlias
	 * 
	 */
	public static AuthServerUtils getTheAuthServerAlias() {
		return theAuthServerAlias;
	}

	/**
	 * Производит аутентификацию по заданному логину и паролю.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Логин
	 * @param pwd
	 *            Переданный пароль
	 * @return true, если аутентификация удалась
	 */
	public boolean login(final String sesid, final String login, final String pwd) {
		if (authServerURL == null) {
			return false;
		}
		try {
			URL server =
				new URL(authServerURL
						+ String.format("/login?sesid=%s&login=%s&pwd=%s", sesid, login, pwd));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod(REQUEST_METHOD);
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Производит выход из сессии аутентификации.
	 * 
	 * @param sesid
	 *            Идентификатор сессии.
	 */
	public void logout(final String sesid) {
		if (authServerURL != null) {
			try {
				URL server = new URL(authServerURL + String.format("/logout?sesid=%s", sesid));
				HttpURLConnection c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod(REQUEST_METHOD);
				c.connect();
				c.getResponseCode();
			} catch (IOException e) {
				// Do nothing, not our problems.
				LOGGER.warn(LOGOUT_WARN);
			}
		}
	}

	/**
	 * Проверяет валидность соединения и работоспособность сервера
	 * аутентификации.
	 * 
	 * @return true если сервер аутентификации работоспособен и доступен.
	 */
	public boolean checkServer() {
		if (authServerURL == null) {
			return false;
		}
		try {
			URL server = new URL(authServerURL + "/authentication.gif");
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.connect();
			return c.getResponseCode() == HttpURLConnection.HTTP_OK
					&& c.getContentType().startsWith("image/gif");
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Фабричный метод генерации клиента менеджера аутентификации.
	 * 
	 * @param url
	 *            URL соединения с менеджером аутентификации, настроенным на
	 *            взаимодействие с LDAP.
	 */
	public static void init(final String url) {

		// На этапе создания проверяется только правильность URL.
		if (url != null) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				LOGGER.error("Проверка URL", e);
			}
		}
		theAuthServerAlias = new AuthServerUtils(url);
	}

	/**
	 * Проверяет введённое имя пользователя и возвращает SID.
	 * 
	 * @param sesid
	 *            Идентификатор сессии
	 * @param login
	 *            Проверяемый логин
	 * @return UserData
	 */
	public UserData checkUser(final String sesid, final String login) {
		if (authServerURL == null) {
			return null;
		}
		try {
			URL server =
				new URL(authServerURL + String.format("/checkname?sesid=%s&name=%s", sesid, login));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod(REQUEST_METHOD);
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<UserData> l = UserInfo.parseStream(c.getInputStream());
				if (l.isEmpty()) {
					return null;
				} else {
					return l.get(0);
				}
			} else {
				return null;
			}
		} catch (IllegalStateException | TransformerException | SecurityException
				| IllegalFormatException | NullPointerException | IOException
				| IndexOutOfBoundsException e) {
			LOGGER.error("Проверка пользователя", e);
			return null;
		}
	}

	/**
	 * Возвращает информацию о текущем пользователе, если сессия
	 * аутентифицирована, и null, если сессия не аутентифицирована.
	 * 
	 * @param sesid
	 *            текущая сессия
	 * @return UserData
	 */
	public UserData isAuthenticated(final String sesid) {
		if (authServerURL == null) {
			return null;
		}
		try {
			URL server =
				new URL(authServerURL + String.format("/isauthenticated?sesid=%s", sesid));
			HttpURLConnection c = (HttpURLConnection) server.openConnection();
			c.setRequestMethod(REQUEST_METHOD);
			c.setDoInput(true);
			c.connect();
			if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
				List<UserData> l = UserInfo.parseStream(c.getInputStream());
				if (l.isEmpty()) {
					return null;
				} else {
					return l.get(0);
				}
			} else {
				return null;
			}
		} catch (IllegalStateException | TransformerException | SecurityException
				| IllegalFormatException | NullPointerException | IOException
				| IndexOutOfBoundsException e) {
			LOGGER.error("Проверка входа в систему", e);
			return null;
		}
	}

	/**
	 * Возвращает URL сервера аутентификации.
	 * 
	 * @return authServerURL
	 */
	public String getUrl() {
		return authServerURL;
	}

}
