package ru.curs.showcase.util;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Вспомогательные функции для работы с сервлетами.
 * 
 * @author den
 * 
 */
public final class ServletUtils {

	/**
	 * Идентификатор сессии для модульных тестов.
	 */
	public static final String TEST_SESSION = "testSession";

	private ServletUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Подготавливает карту с параметрами URL. При подготовке учитывается то,
	 * что русские параметры URL считываются сервером в кодировке ISO-8859-1 при
	 * том, что в реальности они приходят либо в UTF-8 либо в СP1251, а также
	 * тот факт, что установка req.setCharacterEncoding("ISO-8859-1") не
	 * помогает для параметров из URL (хотя помогает для параметров HTML формы).
	 * 
	 * @param req
	 *            - http запрос.
	 */
	public static SortedMap<String, List<String>>
			prepareURLParamsMap(final HttpServletRequest req) throws UnsupportedEncodingException {
		SortedMap<String, List<String>> result = new TreeMap<String, List<String>>();
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = req.getParameterMap().keySet().iterator();
		while (iterator.hasNext()) {
			String oldKey = iterator.next();
			String key = checkAndRecodeURLParam(oldKey);
			String[] oldValues = (String[]) req.getParameterMap().get(oldKey);
			ArrayList<String> values = new ArrayList<String>();
			for (int i = 0; i < oldValues.length; i++) {
				values.add(checkAndRecodeURLParam(oldValues[i]));
			}
			result.put(key, values);
		}
		return result;
	}

	/**
	 * Стандартный обработчик ошибки в сервлете.
	 * 
	 * @param response
	 *            - ответ.
	 * @param message
	 *            - сообщение об ошибке.
	 * @throws IOException
	 */
	public static void fillErrorResponce(final HttpServletResponse response, final String message)
			throws IOException {
		response.reset();
		doNoCasheResponse(response);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		makeResponseFromString(response, message);
	}

	/**
	 * Стандартная функция для записи ответа сервера из переданной строки. Код
	 * статуса должен быть установлен до вызова этой функции, т.к. она закрывает
	 * поток записи ответа.
	 * 
	 * @param response
	 *            - объект ответа сервера.
	 * @param message
	 *            - текст для записи в тело ответа.
	 * @throws IOException
	 */
	public static void makeResponseFromString(final HttpServletResponse response,
			final String message) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding(TextUtils.DEF_ENCODING);
		response.getWriter().append(message);
		response.getWriter().close();
	}

	/**
	 * Возвращает имя пользователя из текущей сессии приложения.
	 * 
	 * @return - имя пользователя.
	 */
	public static String getCurrentSessionUserName() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		} else {
			return "";
		}
	}

	public static String getCurrentSessionId() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return ((WebAuthenticationDetails) SecurityContextHolder.getContext()
					.getAuthentication().getDetails()).getSessionId();
		} else {
			return TEST_SESSION;
		}
	}

	/**
	 * Определяет, является ли браузер пользователя "старой версией IE".
	 * "Старыми" считаются 6 и 7 IE.
	 * 
	 * @param request
	 *            - HttpServletRequest.
	 * @return - результат проверки.
	 */
	public static boolean isOldIE(final HttpServletRequest request) {
		String userAgent = getUserAgent(request);
		boolean isOldIE =
			((userAgent.indexOf("msie 6.0") != -1) || (userAgent.indexOf("msie 7.0") != -1)) ? true
					: false;
		return isOldIE;
	}

	public static String getUserAgent(final HttpServletRequest request) {
		return request.getHeader("User-Agent").toLowerCase();
	}

	/**
	 * Возвращает содержимое реквеста в виде строки. Применяется для обработки
	 * submission с X-форм.
	 * 
	 * @param request
	 *            реквест
	 * @return - строку с содержимым.
	 */
	public static String getRequestAsString(final HttpServletRequest request)
			throws java.io.IOException {
		InputStream is = request.getInputStream();
		BufferedReader requestData =
			new BufferedReader(new InputStreamReader(is, TextUtils.DEF_ENCODING));
		String line;

		try {
			StringBuffer stringBuffer = new StringBuffer();
			while ((line = requestData.readLine()) != null) {
				stringBuffer.append(line);
			}
			line = stringBuffer.toString();

		} finally {
			requestData.close();
		}
		return line;
	}

	/**
	 * Функция устанавливает у response сервера атрибуты, предотвращающие
	 * кэширование результатов запроса в различных браузерах и прокси-серверах.
	 * 
	 * @param resp
	 *            - response (ответ сервера).
	 */
	public static void doNoCasheResponse(final HttpServletResponse resp) {
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "must-revalidate,no-store,no-cache");
		resp.setDateHeader("Expires", 0);
	}

	/**
	 * Проверяет параметр на неверную кодировку ANSI\ISO вместо UTF8 и
	 * исправляет ее при необходимости.
	 * 
	 * @param param
	 *            - параметр.
	 * @return - правильно кодированный параметр.
	 * @throws UnsupportedEncodingException
	 */
	public static String checkAndRecodeURLParam(final String param)
			throws UnsupportedEncodingException {
		String enc = UTF8Checker.getRealEncoding(param);
		if (!TextUtils.DEF_ENCODING.equals(enc)) {
			return TextUtils.recode(param, enc, TextUtils.DEF_ENCODING);
		}
		return param;
	}
}
