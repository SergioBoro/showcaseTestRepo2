package ru.curs.showcase.model.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.model.SourceSelector;

/**
 * Выбор шлюза для элемента в зависимости от текущей конфигурации: например,
 * типа процедуры, определяемого по суффиксу.
 * 
 * @author den
 * 
 */
public class WebTextSelector extends SourceSelector<WebTextGateway> {

	public WebTextSelector(final DataPanelElementInfo aElInfo) {
		super(aElInfo.getProcName());
	}

	@Override
	public WebTextGateway getGateway() {
		WebTextGateway res;
		if (isFile()) {
			res = new WebTextJythonGateway();
		} else {
			res = new WebTextDBGateway();
		}
		return res;
	}

	@Override
	protected String getFileExt() {
		return "py";
	}

}
