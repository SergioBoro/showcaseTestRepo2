/**
 * 
 */
package ru.curs.showcase.model.html.webtext;

import ru.curs.showcase.app.api.datapanel.DataPanelElementContext;
import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.model.event.HTMLBasedElementFactory;
import ru.curs.showcase.model.html.HTMLBasedElementRawData;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Фабрика для создания объектов WebText.
 * 
 * @author den
 * 
 */
public final class WebTextFactory extends HTMLBasedElementFactory {
	/**
	 * Результат работы фабрики.
	 */
	private WebText result;

	public WebTextFactory(final HTMLBasedElementRawData aSource) {
		super(aSource);
	}

	@Override
	public WebText build() throws Exception {
		return (WebText) super.build();
	}

	@Override
	protected void transformData() {
		String out =
			XMLUtils.xsltTransform(getSource().getData(), new DataPanelElementContext(
					getCallContext(), getElementInfo()));
		result.setData(out);
	}

	@Override
	public DataPanelElement getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new WebText();
	}

	@Override
	protected void correctSettingsAndData() {
		String out = result.getData();
		out = replaceVariables(out);
		result.setData(out);
	}
}
