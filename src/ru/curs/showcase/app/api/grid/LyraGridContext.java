package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

/**
 * Класс, содержащий детальный контекст лирыгрида.
 * 
 */
@XmlRootElement(name = "lyraGridContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class LyraGridContext extends GridContext {
	private static final long serialVersionUID = 8898993042175526645L;

	private int dgridOldPosition = 0;

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static LyraGridContext createFirstLoadDefault() {
		LyraGridContext result = new LyraGridContext();
		result.setIsFirstLoad(true);
		return result;
	}

	public int getDgridOldPosition() {
		return dgridOldPosition;
	}

	public void setDgridOldPosition(final int aDgridOldPosition) {
		dgridOldPosition = aDgridOldPosition;
	}

}
