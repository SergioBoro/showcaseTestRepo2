package ru.curs.showcase.app.api.plugin;

import java.io.Serializable;

/**
 * Данные возвращаемые сервером.
 * 
 * @author bogatov
 * 
 */
public class ResponceData implements Serializable {
	private static final long serialVersionUID = 1L;
	private String jsonData;

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(final String sJsonData) {
		this.jsonData = sJsonData;
	}

}
