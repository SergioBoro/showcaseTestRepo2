package ru.curs.showcase.core.jython;

import java.io.InputStream;

/**
 * Результат выполнение Jython скрипта, загрузка файла из компонента grid.
 * 
 * @author bogatov
 * 
 */
public class JythonDownloadResultForGrid {
	private final InputStream inputStream;
	private final String fileName;

	public JythonDownloadResultForGrid(final InputStream aInputStream, final String aFileName) {
		super();
		this.inputStream = aInputStream;
		this.fileName = aFileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getFileName() {
		return fileName;
	}

}
