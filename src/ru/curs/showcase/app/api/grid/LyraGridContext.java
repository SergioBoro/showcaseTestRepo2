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

	@XmlAttribute
	private int oldPosition = -1;

	/**
	 * Создает дефолтные настройки для грида - нужны для первоначальной
	 * отрисовки грида и для тестов.
	 */
	public static LyraGridContext createFirstLoadDefault() {
		LyraGridContext result = new LyraGridContext();
		result.setIsFirstLoad(true);
		return result;
	}

	public int getOldPosition() {
		return oldPosition;
	}

	public void setOldPosition(final int aOldPosition) {
		oldPosition = aOldPosition;
	}

}
