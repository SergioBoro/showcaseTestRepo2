package ru.curs.showcase.app.api;

/**
 * Информация о главной странице.
 * 
 * @author den
 * 
 */
public class MainPage implements SerializableElement {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -3089363754774183721L;

	/**
	 * Высота верхнего колонтитула. Задается в пикселях или в процентах.
	 */
	private String headerHeight;

	/**
	 * Высота нижнего колонтитула. Задается в пикселях или в процентах.
	 */
	private String footerHeight;

	public String getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(final String aHeaderHeight) {
		headerHeight = aHeaderHeight;
	}

	public String getFooterHeight() {
		return footerHeight;
	}

	public void setFooterHeight(final String aFooterHeight) {
		footerHeight = aFooterHeight;
	}
}
