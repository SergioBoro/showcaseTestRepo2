package ru.curs.showcase.app.server;

import ru.curs.showcase.util.XMLUtils;

/**
 * Инициализатор приложения. Содержит главную функцию initialize, которая должна
 * быть вызвана и при старте TomCat, и при запуске модульных тестов.
 * 
 * @author den
 * 
 */
public final class AppInitializer {

	private AppInitializer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Основной метод инициализатора.
	 */
	public static void initialize() {
		XMLUtils.setupSchemaFactory();
		XMLUtils.setupTransformer();
	}
}
