package ru.curs.showcase.app.api.grid.toolbar;


/**
 * Абстрактный базовый класс элемента (элемент, группа) панели инструментов грида.
 * 
 * @author bogatov
 * 
 */
public abstract class BaseToolBarItem extends AbstractToolBarItem {
	private static final long serialVersionUID = 1L;
	private String text;
	private String img;
	private Boolean visible = Boolean.TRUE;
	private Boolean disable = Boolean.FALSE;
	private String hint;

	public String getText() {
		return text;
	}

	public void setText(final String sText) {
		this.text = sText;
	}

	public String getImg() {
		return img;
	}

	public void setImg(final String sImg) {
		this.img = sImg;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final Boolean bVisible) {
		this.visible = bVisible != null ? bVisible : Boolean.TRUE;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(final Boolean bDisable) {
		this.disable = bDisable != null ? bDisable : Boolean.FALSE;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(final String sHint) {
		this.hint = sHint;
	}
}
