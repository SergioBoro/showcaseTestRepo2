package ru.curs.showcase.app.api.grid;

import javax.xml.bind.annotation.*;

import ru.beta2.extra.gwt.ui.SerializableElement;

/**
 * Информация о LiveGrid - offset, limit и идентификатор первого элемента на
 * странице.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtGridLiveInfo implements SerializableElement {

	private static final long serialVersionUID = 7112172183806266451L;

	@XmlAttribute
	private int number;
	@XmlAttribute
	private int size;

	public ExtGridLiveInfo(final int aDefPageNumber, final int aDefPageSizeVal) {
		number = aDefPageNumber;
		size = aDefPageSizeVal;
	}

	public ExtGridLiveInfo() {
		super();
	}

	@Override
	public String toString() {
		return "PageInfo [pageNumber=" + number + ", pageSize=" + size + "]";
	}

	public int getFirstRecord() {
		return size * (number - 1) + 1;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int aPageNumber) {
		number = aPageNumber;
	}

	public int getSize() {
		return size;
	}

	public void setSize(final int aPageSize) {
		size = aPageSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		result = prime * result + size;
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
		if (!(obj instanceof ExtGridLiveInfo)) {
			return false;
		}
		ExtGridLiveInfo other = (ExtGridLiveInfo) obj;
		if (number != other.number) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}
}
