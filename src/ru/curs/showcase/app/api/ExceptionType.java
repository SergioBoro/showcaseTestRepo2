package ru.curs.showcase.app.api;

/**
 * Тип исключения серверной части GWT.
 * 
 * @author den
 * 
 */
public enum ExceptionType implements SerializableElement {
	/**
	 * Ошибка в приложении - это ситуация, которой не должно быть, если бы
	 * программа работала как запланировано. Может быть связана с ошибками Java,
	 * нехваткой памяти или ошибками разработчиков Showcase.
	 */
	APP,
	/**
	 * Исключение решения - это ошибка в данных или настройках решения, ошибка
	 * работы с внешними файлами или приложениями.
	 */
	SOLUTION,
	/**
	 * Пользовательское исключение - т.е. предсказуемое исключение, связанное с
	 * введенной пользователем информацией, а не с ошибками в решении или
	 * программе. Выдается во время проверки введенных данных. Особенностью
	 * данных исключений является то, что в них смысл имеет только сообщение об
	 * ошибке (и возможно ее тип и оригинальное сообщение об ошибке).
	 */
	USER,
	/**
	 * Исключение Java.
	 */
	JAVA,
	/**
	 * Исключение, сгенерированное для передачи контроля в программе. Не
	 * является ошибкой.
	 */
	CONTROL
}
