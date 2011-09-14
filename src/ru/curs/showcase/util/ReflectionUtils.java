package ru.curs.showcase.util;

import java.lang.reflect.*;

/**
 * Статический класс, содержащий общие функции для работы с Java Reflection.
 * 
 * @author den
 * 
 */
public final class ReflectionUtils {

	/**
	 * Возвращает значение свойства по имени поля, используя для доступа get
	 * метод.
	 */
	public static Object getPropValueForField(final Object obj, final Field field) {
		try {
			String metName = getAccessMethodNameForField(field);
			Method met = obj.getClass().getMethod(metName);
			return met.invoke(obj);
		} catch (Exception e) {
			throw new ServerInternalError(e);
		}

	}

	/**
	 * Возвращает имя метода доступа к поля по имени поля согласно принятым в
	 * Java нормам наименования.
	 * 
	 */
	private static String getAccessMethodNameForField(final Field field) {
		String fldName = field.getName();
		if (fldName.startsWith("is")) {
			return fldName;
		} else {
			return String.format("get%s%s", fldName.substring(0, 1).toUpperCase(),
					fldName.substring(1));
		}
	}

	private ReflectionUtils() {
		throw new UnsupportedOperationException();
	}

}
