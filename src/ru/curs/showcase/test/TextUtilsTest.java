package ru.curs.showcase.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.curs.showcase.util.*;

/**
 * Тесты для функций по работе с текстом из TextUtils.
 * 
 * @author den
 * 
 */
public class TextUtilsTest extends AbstractTestWithDefaultUserData {

	private static final String RUS_PATH2 = "C:/Виндоус 7/System32/русское имя.ext";
	private static final String RUS_PATH1 = "C:\\Виндоус 7\\System32\\русское имя.ext";
	private static final String RUS_NAME = "русское имя";
	private static final String RUS_NAME_WITH_EXT = "русское имя.ext";

	/**
	 * Проверка функции {@link ru.curs.showcase.util.UTF8Checker#getRealEncoding
	 * TextUtils.getRealEncoding}.
	 * 
	 */
	@Test
	public void testGetRealEncoding() {
		assertEquals(
				"CP1251",
				UTF8Checker
						.getRealEncoding("РђР±Р±СЂРµРІРёР°С‚СѓСЂС‹ Р РѕСЃС‚СЂР°РЅСЃРЅР°РґР·РѕСЂР°"));
		assertEquals("CP1251", UTF8Checker.getRealEncoding("РђР±"));
		assertEquals("UTF8", UTF8Checker.getRealEncoding("Аббревиатуры Ространснадзора"));
		assertEquals("ISO-8859-1", UTF8Checker.getRealEncoding("ÐÐ±Ð±ÑÐµÐ²Ð¸Ð°ÑÑÑÑ"));
	}

	/**
	 * Проверка функции
	 * {@link ru.curs.showcase.util.TextUtils#extractFileNameWithExt
	 * TextUtils.extractFileNameWithExt}.
	 * 
	 */
	@Test
	public void testExtractFileNameWithExt() {
		assertEquals(RUS_NAME_WITH_EXT, TextUtils.extractFileNameWithExt(RUS_PATH1));
		assertEquals(RUS_NAME_WITH_EXT, TextUtils.extractFileNameWithExt(RUS_PATH2));
	}

	/**
	 * Проверка функции {@link ru.curs.showcase.util.TextUtils#extractFileName
	 * TextUtils.extractFileName}.
	 * 
	 */
	@Test
	public void testExtractFileName() {
		assertEquals(RUS_NAME, TextUtils.extractFileName(RUS_PATH1));
		assertEquals(RUS_NAME, TextUtils.extractFileName(RUS_PATH2));
		final String procName = "dp0903";
		assertEquals(procName, TextUtils.extractFileName(procName));
		assertEquals("calc", TextUtils.extractFileName("calc.exe"));
		assertEquals("calc", TextUtils.extractFileName("C:\\windows\\calc"));
	}

}
