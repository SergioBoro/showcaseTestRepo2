package ru.curs.showcase.core.grid;

/**
 * Класс для хранения дополнительной информации, необходимой для подключения
 * лирагрид.
 * 
 */
public class LyraGridAddInfo {

	private int lyraOldPosition = 0;
	private int dgridOldTotalCount = 0;
	private Object[] lastKeyValues = null;

	public int getLyraOldPosition() {
		return lyraOldPosition;
	}

	public void setLyraOldPosition(final int aLyraOldPosition) {
		lyraOldPosition = aLyraOldPosition;
	}

	public int getDgridOldTotalCount() {
		return dgridOldTotalCount;
	}

	public void setDgridOldTotalCount(final int aDgridOldTotalCount) {
		dgridOldTotalCount = aDgridOldTotalCount;
	}

	public Object[] getLastKeyValues() {
		return lastKeyValues;
	}

	public void setLastKeyValues(final Object[] aLastKeyValues) {
		lastKeyValues = aLastKeyValues;
	}

}
