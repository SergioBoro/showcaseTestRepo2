package ru.curs.showcase.security;

import java.io.*;
import java.net.*;
import java.util.IllegalFormatException;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;
import ru.curs.showcase.util.exception.SettingsFileOpenException;

/**
 * @author anlug
 * 
 *         Класс, реализующий провайдер аутентификации с помощью AuthServer.
 * 
 */
public class AuthServerAuthenticationProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(final Authentication arg1) {

		// TODO Auto-generated method stub
		// arg0.getAuthorities().iterator().next().
		// if (SecurityContextHolder.getContext().getAuthentication() != null) {
		// if
		// (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
		// {
		// return arg0;
		// }
		// }
		// authentication.
		// UsernamePasswordAuthenticationToken arg0 = arg1;

		String ipAddresOfRemouteHost =
			((UserAndSessionDetails) arg1.getDetails()).getRemoteAddress();

		if (ipAddresOfRemouteHost == null) {
			ipAddresOfRemouteHost = "";
		}

		// System.out.println(qqq);
		String url = "";
		String login = arg1.getPrincipal().toString();
		String pwd = arg1.getCredentials().toString();
		String sesid = ((UserAndSessionDetails) arg1.getDetails()).getSessionId();
		String groupProviders =
			((UserAndSessionDetails) arg1.getDetails()).getUserInfo().getGroupProviders();

		try {
			url = SecurityParamsFactory.getLocalAuthServerUrl();
		} catch (SettingsFileOpenException e1) {
			throw new AuthenticationServiceException(SecurityParamsFactory.APP_PROP_READ_ERROR, e1);
		}

		// AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid,
		// false);
		// AppCurrContext.getInstance().setAuthViaAuthServ(false);
		if ("9152046062107176349L_default_value".equals(pwd)) {
			// AppCurrContext.getInstance().setAuthViaAuthServ(true);
			AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
			((UserAndSessionDetails) arg1.getDetails()).setAuthViaAuthServer(true);

			if (AuthServerUtils.getTheAuthServerAlias() == null) {
				AuthServerUtils.init(url);
			}

			((UserAndSessionDetails) arg1.getDetails()).setUserInfo(AuthServerUtils
					.getTheAuthServerAlias().isAuthenticated(sesid));

		} else {
			AuthServerUtils.init(url);

			// UserData ud =
			// AuthServerUtils.getTheAuthServerAlias().isAuthenticated(sesid);
			// if (ud == null) {
			try {
				URL server;
				if (groupProviders == null) {
					server =
						new URL(url
								+ String.format("/login?sesid=%s&login=%s&pwd=%s&ip=%s", sesid,
										encodeParam(login), encodeParam(pwd),
										ipAddresOfRemouteHost));
				} else {
					server =
						new URL(url
								+ String.format("/login?sesid=%s&login=%s&pwd=%s&gp=%s&ip=%s",
										sesid, encodeParam(login), encodeParam(pwd),
										encodeParam(groupProviders), ipAddresOfRemouteHost));
				}

				HttpURLConnection c = (HttpURLConnection) server.openConnection();
				c.setRequestMethod("GET");
				c.connect();
				// Thread.sleep(1000);
				if (c.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// AppCurrContext.getInstance();
					AppInfoSingleton.getAppInfo().setAuthViaAuthServerForSession(sesid, true);
					((UserAndSessionDetails) arg1.getDetails()).setAuthViaAuthServer(true);
					((UserAndSessionDetails) arg1.getDetails()).setUserInfo(AuthServerUtils
							.getTheAuthServerAlias().isAuthenticated(sesid));

					// AppCurrContext.getInstance().setAuthViaAuthServ(true);
				} else {
					throw new BadCredentialsException("Bad credentials");
				}

			} catch (BadCredentialsException | IllegalStateException | SecurityException
					| IllegalFormatException | NullPointerException | IOException
					| IndexOutOfBoundsException e) {
				if ("Bad credentials".equals(e.getMessage())) {
					throw new BadCredentialsException(e.getMessage(), e);
				} else {
					throw new BadCredentialsException("Authentication server is not available: "
							+ e.getMessage(), e);
				}
			}
			// }
		}

		// Authentication g = new Authentication.;

		// try {
		// arg0.setAuthenticated(true);
		// } catch (Exception e) {
		// }
		// SecurityContextHolder.getContext().setAuthentication(arg0);
		return arg1;

	}

	@Override
	public boolean supports(final Class<? extends Object> arg0) {
		return SignedUsernamePasswordAuthenticationToken.class.isAssignableFrom(arg0);
	}

	private String encodeParam(final String param) throws UnsupportedEncodingException {
		String s = param;
		s = s.replace("%", "AB4AFD63A4C");
		s = s.replace("+", "D195B4C989F");
		s = URLEncoder.encode(s, "ISO8859_1");
		return s;
	}

}