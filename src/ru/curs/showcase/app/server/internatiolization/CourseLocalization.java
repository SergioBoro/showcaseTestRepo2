package ru.curs.showcase.app.server.internatiolization;

import gnu.gettext.GettextResource;

import java.util.ResourceBundle;

import ru.curs.showcase.runtime.UserDataUtils;

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

		String bundleFile = UserDataUtils.getBundleClass(UserDataUtils.getUserDataId());

		ResourceBundle rb = null;
		try {
			if (!"".equals(bundleFile) && bundleFile.contains("."))
				rb =
					ResourceBundle.getBundle(bundleFile.substring(0, bundleFile.lastIndexOf(".")));
		} catch (Exception e) {
			rb = null;
		}
		return rb;
	}

}
