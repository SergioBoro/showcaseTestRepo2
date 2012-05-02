package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;
import ru.curs.gwt.datagrid.model.*;

/**
 * Столбец.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LiveGridColumnConfig implements SerializableElement {

	private static final long serialVersionUID = -5349384847976436436L;

	@XmlAttribute
	private String id = null;
	@XmlTransient
	private String caption = null;
	@XmlAttribute
	private Integer width = null;

	@XmlAttribute
	private String dateTimeFormat = null;

	@XmlTransient
	private boolean visible;
	@XmlTransient
	private int index;
	@XmlTransient
	private com.extjs.gxt.ui.client.Style.HorizontalAlignment horizontalAlignment;
	@XmlAttribute
	private Sorting sorting;
	@XmlTransient
	private ColumnValueDisplayMode displayMode;
	/**
	 * Позволяет ограничить минимальную ширину столбца (в пикселях).
	 */
	@XmlTransient
	private Integer minWidthPx;
	@XmlAttribute
	private GridValueType valueType;
	/**
	 * Содержит способ форматирования значений. Используется на сервере при
	 * построении грида.
	 */
	@XmlTransient
	private String format;
	/**
	 * Ссылка на процедуру загрузки файла.
	 */
	@XmlTransient
	private String linkId;

	public LiveGridColumnConfig() {
	}

	public LiveGridColumnConfig(final String aId, final String aCaption, final Integer aWidth) {
		id = aId;
		caption = aCaption;
		width = aWidth;
	}

	public final GridValueType getValueType() {
		return valueType;
	}

	public final void setValueType(final GridValueType aValueType) {
		valueType = aValueType;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id1) {
		this.id = id1;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String caption1) {
		this.caption = caption1;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(final boolean visible1) {
		this.visible = visible1;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index1) {
		this.index = index1;
	}

	public com.extjs.gxt.ui.client.Style.HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(
			final com.extjs.gxt.ui.client.Style.HorizontalAlignment horizontalAlignment1) {
		this.horizontalAlignment = horizontalAlignment1;
	}

	public Sorting getSorting() {
		return sorting;
	}

	public void setSorting(final Sorting sorting1) {
		this.sorting = sorting1;
	}

	public boolean hasSorting() {
		return sorting != null;
	}

	public void clearSorting() {
		sorting = null;
	}

	public ColumnValueDisplayMode getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(final ColumnValueDisplayMode displayMode1) {
		this.displayMode = displayMode1;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer width1) {
		this.width = width1;
	}

	public final String getFormat() {
		return format;
	}

	public final void setFormat(final String aFormat) {
		format = aFormat;
	}

	public Integer getMinWidthPx() {
		return minWidthPx;
	}

	public void setMinWidthPx(final Integer minWidthPx1) {
		this.minWidthPx = minWidthPx1;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(final String linkId1) {
		this.linkId = linkId1;
	}

	// CHECKSTYLE:OFF
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result + ((displayMode == null) ? 0 : displayMode.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result =
			prime * result + ((horizontalAlignment == null) ? 0 : horizontalAlignment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		result = prime * result + ((minWidthPx == null) ? 0 : minWidthPx.hashCode());
		result = prime * result + ((sorting == null) ? 0 : sorting.hashCode());
		result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		result = prime * result + ((linkId == null) ? 0 : linkId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LiveGridColumnConfig)) {
			return false;
		}
		LiveGridColumnConfig other = (LiveGridColumnConfig) obj;
		if (caption == null) {
			if (other.caption != null) {
				return false;
			}
		} else if (!caption.equals(other.caption)) {
			return false;
		}
		if (displayMode != other.displayMode) {
			return false;
		}
		if (format == null) {
			if (other.format != null) {
				return false;
			}
		} else if (!format.equals(other.format)) {
			return false;
		}
		if (horizontalAlignment != other.horizontalAlignment) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (index != other.index) {
			return false;
		}
		if (minWidthPx == null) {
			if (other.minWidthPx != null) {
				return false;
			}
		} else if (!minWidthPx.equals(other.minWidthPx)) {
			return false;
		}
		if (sorting != other.sorting) {
			return false;
		}
		if (valueType != other.valueType) {
			return false;
		}
		if (visible != other.visible) {
			return false;
		}
		if (width == null) {
			if (other.width != null) {
				return false;
			}
		} else if (!width.equals(other.width)) {
			return false;
		}
		if (linkId == null) {
			if (other.linkId != null) {
				return false;
			}
		} else if (!linkId.equals(other.linkId)) {
			return false;
		}
		return true;
	}

	// CHECKSTYLE:ON

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(final String aDateTimeFormat) {
		this.dateTimeFormat = aDateTimeFormat;
	}
}
