package ru.curs.showcase.util;

import java.lang.reflect.*;

import ru.curs.showcase.util.exception.ServerLogicError;

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
			throw new ServerLogicError(e);
		}
	}

	/**
	 * Метод не должен вызывать исключения, наследованные от BaseException по
	 * причине того, что используется в механизме вывода "веб-консоли". А запись
	 * нового события в лог во время процесса вывода, происходящая в том же
	 * потоке, приводит к ConcurrentModificationException.
	 */
	public static Object getPropValueByFieldName(final Object obj, final String fieldName)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String metName = getAccessMethodNameForField(fieldName);
		Method met = obj.getClass().getMethod(metName);
		return met.invoke(obj);
	}

	/**
	 * Возвращает имя метода доступа к поля по имени поля согласно принятым в
	 * Java нормам наименования.
	 * 
	 */
	private static String getAccessMethodNameForField(final Field field) {
		String fldName = field.getName();
		return getAccessMethodNameForField(fldName);
	}

	private static String getAccessMethodNameForField(final String fldName) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getProcessDescForClass(final Class classLink) {
		if (classLink.getAnnotation(Description.class) != null) {
			return ((Description) classLink.getAnnotation(Description.class)).process();
		}
		return "не задан";

	}
}
