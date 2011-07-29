package ru.curs.showcase.app.api.element;

/**
 * Данные о размере элемента. Являются базывым классом для данных графика и
 * карты.
 * 
 * @author den
 * 
 */
public abstract class Size {

	/**
	 * Ширина элемента.
	 */
	private Integer width;
	/**
	 * Высота элемента.
	 */
	private Integer height;

	public final Integer getWidth() {
		return width;
	}

	public final void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public final Integer getHeight() {
		return height;
	}

	public final void setHeight(final Integer aHeight) {
		height = aHeight;
	}

}