package ru.curs.showcase.app.api.grid;

import java.io.Serializable;

import javax.xml.bind.annotation.*;

import com.sencha.gxt.core.shared.FastMap;

/**
 * Класс грида из GXT с данными.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LiveGridModel implements Serializable {

	private static final long serialVersionUID = -3660786754868736757L;

	private FastMap<Object> map;

	public FastMap<Object> getMap() {
		return map;
	}

	public void setMap(final FastMap<Object> aMap) {
		map = aMap;
	}

	public LiveGridModel() {
		map = new FastMap<Object>();
	}

	public Object get(final String key) {
		return map.get(key);
	}

	public void set(final String key, final Object value) {
		map.put(key, value);
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
		if ((obj != null) && (obj instanceof LiveGridModel)) {
			LiveGridModel egd = (LiveGridModel) obj;
			return getId().equals(egd.getId());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
