package ru.curs.showcase.model.frame;

import ru.curs.showcase.model.GeneralXMLHelper;
import ru.curs.showcase.util.AppProps;

/**
 * "Выбиральщик" для фрейма главной страницы. Отвечает за считывание названия
 * источника и создание требуемого шлюза.
 * 
 * @author den
 * 
 */
public class MainPageFrameSelector {
	/**
	 * Зафиксированное расширение для файлов с фреймами.
	 */
	private static final String FILE_EXT = "html";
	/**
	 * Тип фрейма.
	 */
	private final MainPageFrameType type;

	/**
	 * Имя источника.
	 */
	private String sourceName;

	public MainPageFrameSelector(final MainPageFrameType aType) {
		super();
		type = aType;
		read();
	}

	private void read() {
		String frameParam =
			String.format("%s.%s", type.toString().toLowerCase(), GeneralXMLHelper.SOURCE_TAG);
		sourceName = AppProps.getOptionalValueByName(frameParam);
		if (sourceName == null) {
			sourceName = getDefaultValue();
		}
	}

	private String getDefaultValue() {
		return type.toString().toLowerCase() + "." + FILE_EXT;
	}

	private boolean isFile(final String aFrameSource) {
		return aFrameSource.endsWith("." + FILE_EXT);
	}

	public MainPageFrameType getType() {
		return type;
	}

	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Создает и возвращает шлюз для текущего источника.
	 */
	public MainPageFrameGateway getGateway() {
		if (isFile(sourceName)) {
			return new FileMainPageFrameGateway();
		} else {
			return new DBMainPageFrameGateway();
		}
	}
}
