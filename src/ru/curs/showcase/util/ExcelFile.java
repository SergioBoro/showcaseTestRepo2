package ru.curs.showcase.util;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.*;

/**
 * Класс файла Excel. Включает содержимое файла и его имя.
 * 
 * @author den
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExcelFile extends OutputStreamDataFile {
	/**
	 * Имя результирующего файла по умолчанию.
	 */
	private static final String DEF_FILENAME = "table";

	/**
	 * Расширение генерируемого Excel файла. Используем xls вместо xml для
	 * корректной обработки браузерами и Windows на компьютерах обычных
	 * пользователей.
	 */
	private static final String FILEEXT = "xls";

	public ExcelFile(final ByteArrayOutputStream aData) {
		super(aData, String.format("%s.%s", DEF_FILENAME, FILEEXT));
	}

	public ExcelFile() {
		super();
	}

	@Override
	public boolean isTextFile() {
		return true;
	}
}
