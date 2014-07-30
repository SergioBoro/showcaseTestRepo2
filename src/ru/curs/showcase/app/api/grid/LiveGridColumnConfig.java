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
	@XmlAttribute
	private String parentId = null;
	@XmlTransient
	private String caption = null;
	@XmlAttribute
	private boolean readonly = false;
	@XmlAttribute
	private String editor = null;
	@XmlAttribute
	private Integer width = null;

	@XmlTransient
	private HorizontalAlignment horizontalAlignment;
	@XmlAttribute
	private Sorting sorting;
	@XmlAttribute
	private GridValueType valueType;
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(final String aParentId) {
		parentId = aParentId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String caption1) {
		this.caption = caption1;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(final boolean aReadonly) {
		readonly = aReadonly;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(final String aEditor) {
		editor = aEditor;
	}

	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment1) {
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

	public Integer getWidth() {
		return width;
	}

	public void setWidth(final Integer width1) {
		this.width = width1;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(final String linkId1) {
		this.linkId = linkId1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result =
			prime * result + ((horizontalAlignment == null) ? 0 : horizontalAlignment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((sorting == null) ? 0 : sorting.hashCode());
		result = prime * result + ((valueType == null) ? 0 : valueType.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
		result = prime * result + ((linkId == null) ? 0 : linkId.hashCode());
		return result;
	}

	// CHECKSTYLE:OFF

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
		if (sorting != other.sorting) {
			return false;
		}
		if (valueType != other.valueType) {
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

}
