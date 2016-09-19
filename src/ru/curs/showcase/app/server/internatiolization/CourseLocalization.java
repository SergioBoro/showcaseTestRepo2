package ru.curs.showcase.app.server.internatiolization;

import gnu.gettext.GettextResource;

import java.io.File;
import java.net.*;
import java.util.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ru.curs.showcase.runtime.*;
import ru.curs.showcase.util.UserAndSessionDetails;

/**
 * 
 * @author s.borodanev
 *
 *         Класс, предназначенный для вызова функциональных возможностей пакета
 *         GNU GetText. Каждый из методов данного класса перевызывает
 *         соответствующий метод указанного пакета. Указанный пакет выполняет
 *         функции локализации и интернационализации.
 */

public class CourseLocalization {

	/**
	 * Возвращает перевод переменной <VAR>msgid</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @return перевод переменной <VAR>msgid</VAR>, или сама переменная
	 *         <VAR>msgid</VAR>, если перевод не найден
	 */
	public static String gettext(ResourceBundle catalog, String msgid) {
		return GettextResource.gettext(catalog, msgid);
	}

	/**
	 * Возвращает форму множественного числа для номера <VAR>n</VAR> перевода
	 * переменной <VAR>msgid</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @param msgid_plural
	 *            - её форма множественного числа на английском языке
	 * @return перевод переменной <VAR>msgid</VAR>, в зависимости от переменной
	 *         <VAR>n</VAR>, или сама переменная <VAR>msgid</VAR>, или
	 *         переменная <VAR>msgid_plural</VAR>, если перевод не найден
	 */
	public static String
			ngettext(ResourceBundle catalog, String msgid, String msgid_plural, long n) {
		return GettextResource.ngettext(catalog, msgid, msgid_plural, n);
	}

	/**
	 * Возвращает перевод переменной <VAR>msgid</VAR>, в зависимости от
	 * контекстной переменной <VAR>msgctxt</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgctxt
	 *            - контекст для строки-ключа, используется ASCII-кодировка
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @return перевод переменной <VAR>msgid</VAR>, или сама переменная
	 *         <VAR>msgid</VAR>, если перевод не найден
	 */
	public static String pgettext(ResourceBundle catalog, String msgctxt, String msgid) {
		return GettextResource.pgettext(catalog, msgctxt, msgid);
	}

	/**
	 * * Возвращает форму множественного числа для номера <VAR>n</VAR> перевода
	 * переменной <VAR>msgid</VAR>, в зависимости от контекстной переменной
	 * <VAR>msgctxt</VAR>.
	 * 
	 * @param catalog
	 *            - экземпляр класса ResourceBundle
	 * @param msgctxt
	 *            - контекст для строки-ключа, используется ASCII-кодировка
	 * @param msgid
	 *            - строка-ключ, которая должна быть переведена, используется
	 *            ASCII-кодировка
	 * @param msgid_plural
	 *            - её форма множественного числа на английском языке
	 * @return перевод переменной <VAR>msgid</VAR>, в зависимости от переменной
	 *         <VAR>n</VAR>, или сама переменная <VAR>msgid</VAR>, или
	 *         переменная <VAR>msgid_plural</VAR>, если перевод не найден
	 */
	public static String npgettext(ResourceBundle catalog, String msgctxt, String msgid,
			String msgid_plural, long n) {
		return GettextResource.npgettext(catalog, msgctxt, msgid, msgid_plural, n);
	}

	/**
	 * Метод установки ResourceBundle для дальнейшего использования в переводе
	 * серверной части Showcase с помощью Gettext.
	 * 
	 * @return ResourceBundle
	 */
	public static ResourceBundle getLocalizedResourseBundle() {
		// String lang = UserDataUtils.getLocaleForCurrentUserdata();

		// ProcessBuilder pb = new ProcessBuilder();
		// String classpath = pb.environment().get("CLASSPATH");
		// String localizePath = classpath.substring(classpath.lastIndexOf(";")
		// + 1);
		// File localizeDir = new File(localizePath);

		// String bundleFile = "";
		// for (String file : localizeDir.list()) {
		// if (file.equals(lang + ".class")) {
		// bundleFile = file;
		// break;
		// }
		// }

		File dir = UserDataUtils.getResourceDir(UserDataUtils.getUserDataId());

		String lang = "";
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sesid = ((UserAndSessionDetails) auth.getDetails()).getSessionId();

			lang = AppInfoSingleton.getAppInfo().getLocalizationCache().get(sesid);
		}

		if (lang == null || "".equals(lang))
			lang = UserDataUtils.getLocaleForCurrentUserdata();

		ResourceBundle rb = null;
		try {
			URL[] urls = { dir.toURI().toURL() };
			MyLoader loader = new MyLoader(urls, Thread.currentThread().getContextClassLoader());
			rb = ResourceBundle.getBundle("loc", new Locale(lang), loader);
			loader.finalize();
		} catch (Throwable e) {
			rb = null;
		}
		return rb;
	}

	private static class MyLoader extends URLClassLoader {

		MyLoader(URL[] urls, ClassLoader parentLoader) {
			super(urls, parentLoader);
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
		}
	}
}
