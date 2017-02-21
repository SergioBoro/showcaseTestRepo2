package ru.curs.showcase.security.esia;

import org.slf4j.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

import ru.curs.celesta.*;
import ru.curs.showcase.runtime.AppInfoSingleton;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * ESIA провайдер авторизации.
 * 
 */
public class ESIAAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ESIAAuthenticationProvider.class);

	@Override
	public Authentication authenticate(final Authentication auth) {

		UserAndSessionDetails userAndSessionDetails = (UserAndSessionDetails) auth.getDetails();

		if (userAndSessionDetails.getUserInfo() != null) {
			try {
				Celesta.getInstance().login(userAndSessionDetails.getSessionId(),
						userAndSessionDetails.getUserInfo().getSid());
			} catch (CelestaException e) {
				if (AppInfoSingleton.getAppInfo().isEnableLogLevelError()) {
					LOGGER.error("Ошибка привязки сессии приложения к пользователю в celesta", e);
				}
			}
		}

		return auth;

	}

	@Override
	public boolean supports(final Class<?> arg0) {
		return ESIAAuthenticationToken.class.isAssignableFrom(arg0);
	}

}
