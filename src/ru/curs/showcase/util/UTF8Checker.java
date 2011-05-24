package ru.curs.showcase.util;

/**
 * Класс для проверки того, что не английский текст в UTF8 был неправильно
 * закодирован.
 * 
 * @author den
 * 
 */
public class UTF8Checker {
	/**
	 * Длина исследуемой последовательности по умолчанию.
	 */
	private static final int DEF_SEQ_LEN = 5;

	/**
	 * Длина проверяемой цепочки символов, после которой будет сделан вывод о
	 * реальной кодировке строки.
	 */
	private int sequenceLen = DEF_SEQ_LEN;

	/**
	 * Номер символа, с которого нужно начинать проверку. Используется для того,
	 * чтобы пропустить BOM.
	 */
	private int startFrom = 2;

	/**
	 * Возможные символы, содержащийся в первом байте букв национального
	 * алфавита в UTF8.
	 */
	private char[] signs;

	public UTF8Checker(final char[] aSigns) {
		super();
		signs = aSigns;
	}

	public char[] getSigns() {
		return signs;
	}

	public void setSigns(final char[] aSigns) {
		signs = aSigns;
	}

	public int getSequenceLen() {
		return sequenceLen;
	}

	public int getStartFrom() {
		return startFrom;
	}

	public void setSequenceLen(final int aSequenceLen) {
		sequenceLen = aSequenceLen;
	}

	public void setStartFrom(final int aStartFrom) {
		startFrom = aStartFrom;
	}

	/**
	 * Функция проверки.
	 * 
	 * @param str
	 *            - проверяемая строка.
	 * @return - результат проверки.
	 */
	public boolean check(final String str) {
		for (int i = startFrom; i < startFrom + sequenceLen * 2; i = i + 2) {
			char testCh = str.charAt(i);
			boolean foundUFT8Symbol = false;
			for (char j : signs) {
				if ((testCh == j)) {
					foundUFT8Symbol = true;
					break;
				}
			}
			if (!foundUFT8Symbol) {
				return false;
			}

		}
		return true;
	}

	/**
	 * Функция проверки для случая, когда набор символов для первого байта
	 * динамический.
	 * 
	 * @param str
	 *            - проверяемая строка.
	 * @return - результат проверки.
	 * 
	 * @param aSigns
	 *            - символы для первого байта.
	 */
	public static boolean check(final String str, final char[] aSigns) {
		UTF8Checker checker = new UTF8Checker(aSigns);
		return checker.check(str);
	}

	public UTF8Checker() {
		super();
	}
}
