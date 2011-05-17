package ru.curs.showcase.util;

import java.io.*;

/**
 * Дубликатор для InputStream. TODO: не всякий поток позволяет повторное
 * копирование!
 * 
 * @author den
 * 
 */
public class InputStreamDuplicator {

	static final int BUF_LEN = 512;
	/**
	 * Внутреннее хранилище данных.
	 */
	private final ByteArrayOutputStream internal = new ByteArrayOutputStream();
	/**
	 * Входной InputStream.
	 */
	private final InputStream input;

	public InputStreamDuplicator(final InputStream is) throws IOException {
		input = is;
		copy();
	}

	private void copy() throws IOException {
		int chunk = 0;
		byte[] data = new byte[BUF_LEN];

		while (-1 != (chunk = input.read(data))) {
			internal.write(data, 0, chunk);
		}
	}

	public InputStream getCopy() {
		return outputToInputStream(internal);
	}

	public ByteArrayOutputStream getOutputStream() {
		return internal;
	}

	/**
	 * Преобразует входной поток в выходной.
	 * 
	 * @param stream
	 *            - входной поток.
	 * @return - выходной поток.
	 * @throws IOException
	 */
	public static ByteArrayOutputStream inputToOutputStream(final InputStream stream)
			throws IOException {
		InputStreamDuplicator dup = new InputStreamDuplicator(stream);
		ByteArrayOutputStream out = dup.getOutputStream();
		return out;
	}

	/**
	 * Преобразует выходной поток в входной.
	 * 
	 * @param stream
	 *            - выходной поток.
	 * @return - выходной поток.
	 */
	public static InputStream outputToInputStream(final ByteArrayOutputStream stream) {
		return new ByteArrayInputStream(stream.toByteArray());
	}

}