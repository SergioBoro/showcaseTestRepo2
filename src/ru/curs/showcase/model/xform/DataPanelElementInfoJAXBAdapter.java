package ru.curs.showcase.model.xform;

import javax.xml.bind.annotation.XmlRootElement;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;

/**
 * Наследник класса DataPanelElementInfo, готовый к сериализации. Введен из-за
 * того, что DataPanelElementInfo - это GWT DTO и поэтому не может содержать
 * аннотации JAXB.
 * 
 * @author den
 * 
 */
@XmlRootElement(name = "element")
public final class DataPanelElementInfoJAXBAdapter extends DataPanelElementInfo {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 8381357835819848268L;

	public DataPanelElementInfoJAXBAdapter(final DataPanelElementInfo original) {
		super();
		assignNullValues(original);
	}

	public DataPanelElementInfoJAXBAdapter() {
		super();
	}
}
