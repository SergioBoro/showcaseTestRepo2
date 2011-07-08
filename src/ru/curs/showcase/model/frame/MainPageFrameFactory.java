package ru.curs.showcase.model.frame;

import ru.curs.showcase.app.server.AppInfoSingleton;

/**
 * Фабрика для создания фреймов главной формы. Отвечает за дополнительную
 * обработку полученного из шлюза текста.
 * 
 * @author den
 * 
 */
public final class MainPageFrameFactory {
	/**
	 * Шаблон для пути к текущей userdata в WebContent относительно корня
	 * веб-приложения.
	 */
	public static final String SHOWCASE_CURRENT_USERDATA = "%SHOWCASE_CURRENT_USERDATA%";
	/**
	 * Исходный HTML текст.
	 */
	private String source;

	/**
	 * Результат работы фабрики.
	 */
	private String result;

	/**
	 * Признак того, что результат должен загружаться в IFrame.
	 */
	private final Boolean forIFrame;

	public MainPageFrameFactory(final Boolean aForIFrame) {
		super();
		forIFrame = aForIFrame;
	}

	/**
	 * Основной метод фабрики - обработка исходного HTML кода.
	 * 
	 * @param aSource
	 *            - исходный код.
	 */
	public String build(final String aSource) {
		source = aSource;
		result = source;
		replaceTemplates();
		if (forIFrame) {
			convertToHTMLPage();
		}
		return result;
	}

	private void convertToHTMLPage() {
		result = "<html><head/><body>" + result + "</body></html>";
	}

	private void replaceTemplates() {
		result =
			result.replace(SHOWCASE_CURRENT_USERDATA, String.format("solutions/%s",
					AppInfoSingleton.getAppInfo().getCurUserDataId()));
	}

	public String getSource() {
		return source;
	}

	public String getResult() {
		return result;
	}

	public Boolean getForIFrame() {
		return forIFrame;
	}
}
