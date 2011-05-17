package ru.curs.showcase.model;

/**
 * Базовый класс для обмена файлами между сервером и клиентами. Содержимое файла
 * может храниться как в виде OutputStream, так и InputStream - чтобы избежать
 * цепочки преобразований.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип для хранения содержимого - OutputStream или InputStream.
 */
public class DataFile<T> {
	/**
	 * Данные файла (в виде выходного потока).
	 */
	private T data;

	/**
	 * Имя файла. При использовании русского имени возможны проблемы!!!
	 */
	private String name;

	public DataFile(final T aData, final String fileName) {
		name = fileName;
		data = aData;
	}

	public DataFile() {
	}

	public final T getData() {
		return data;
	}

	public final void setData(final T aData) {
		data = aData;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String aName) {
		name = aName;
	}

}