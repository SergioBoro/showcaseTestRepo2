package ru.curs.showcase.app.api;

import ru.beta2.extra.gwt.ui.SerializableElement;

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

	/**
	 * Код заголовка главной страницы.
	 */
	private String header;

	/**
	 * Код нижнего колонтитула главной страницы.
	 */
	private String footer;

	/**
	 * Код экрана приветствия.
	 */
	private String welcome;

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

	public String getHeader() {
		return header;
	}

	public void setHeader(final String aHeader) {
		header = aHeader;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(final String aFooter) {
		footer = aFooter;
	}

	public String getWelcome() {
		return welcome;
	}

	public void setWelcome(final String aWelcome) {
		welcome = aWelcome;
	}
}
