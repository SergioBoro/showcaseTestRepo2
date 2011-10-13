package ru.curs.showcase.app.api.element;

/**
 * Данные о размере элемента. Являются базовым классом для данных графика и
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

	public void setWidth(final Integer aWidth) {
		width = aWidth;
	}

	public final Integer getHeight() {
		return height;
	}

	public void setHeight(final Integer aHeight) {
		height = aHeight;
	}

}