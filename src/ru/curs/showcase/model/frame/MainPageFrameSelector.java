package ru.curs.showcase.model.frame;

import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.runtime.AppProps;
import ru.curs.showcase.util.xml.GeneralXMLHelper;

/**
 * "Выбиральщик" для фрейма главной страницы. Отвечает за считывание названия
 * источника и создание требуемого шлюза.
 * 
 * @author den
 * 
 */
public class MainPageFrameSelector extends SourceSelector<MainPageFrameGateway> {
	/**
	 * Тип фрейма.
	 */
	private final MainPageFrameType type;

	public MainPageFrameSelector(final MainPageFrameType aType) {
		super();
		type = aType;
		read();
	}

	private void read() {
		String frameParam =
			String.format("%s.%s", type.toString().toLowerCase(), GeneralXMLHelper.SOURCE_TAG);
		setSourceName(AppProps.getOptionalValueByName(frameParam));
		if (getSourceName() == null) {
			setSourceName(getDefaultValue());
		}
	}

	private String getDefaultValue() {
		return type.toString().toLowerCase() + "." + getFileExt();
	}

	public MainPageFrameType getType() {
		return type;
	}

	/**
	 * Создает и возвращает шлюз для текущего источника.
	 */
	@Override
	public MainPageFrameGateway getGateway() {
		MainPageFrameGateway gateway;
		if (isFile()) {
			gateway = new MainPageFrameFileGateway();
		} else {
			gateway = new MainPageFrameDBGateway();
		}
		gateway.setSourceName(getSourceName());
		return gateway;
	}

	@Override
	protected String getFileExt() {
		return "html";
	}
}
