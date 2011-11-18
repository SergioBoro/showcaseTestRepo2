package ru.curs.showcase.model.html.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.model.html.*;

/**
 * Выбор шлюза для элемента в зависимости от текущей конфигурации: например,
 * типа процедуры, определяемого по суффиксу.
 * 
 * @author den
 * 
 */
public class WebTextSelector extends SourceSelector<HTMLGateway> {

	public WebTextSelector(final DataPanelElementInfo aElInfo) {
		super(aElInfo.getProcName());
	}

	@Override
	public HTMLGateway getGateway() {
		HTMLGateway res;
		if (isFile()) {
			res = new HTMLJythonGateway();
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
