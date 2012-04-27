package ru.curs.showcase.app.api.grid;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Класс грида из ExtGWT с данными.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtGridData extends BaseModel implements Serializable {

	private static final long serialVersionUID = -3660786754868736757L;

	public ExtGridData() {

	}

	public String getId() {
		return (String) get("id");
	}

	public void setId(final String id) {
		set("id", id);
	}

	public String getRowStyle() {
		return (String) get("rowstyle");
	}

	public void setRowStyle(final String rowstyle) {
		set("rowstyle", rowstyle);
	}

	@Override
	public boolean equals(final Object obj) {
		if ((obj != null) && (obj instanceof ExtGridData)) {
			ExtGridData egd = (ExtGridData) obj;
			return getId().equals(egd.getId());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
