/**
 * 
 */
package ru.curs.showcase.core.html.webtext;

import java.io.InputStream;

import ru.curs.showcase.app.api.element.DataPanelElement;
import ru.curs.showcase.app.api.html.WebText;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.DataFile;
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
		XSLTransformSelector selector =
			new XSLTransformSelector(getCallContext(), getElementInfo());
		DataFile<InputStream> transform = selector.getData();
		String out = XMLUtils.xsltTransform(getSource().getData(), transform);
		result.setData(out);
	}

	@Override
	public DataPanelElement getResult() {
		return result;
	}

	@Override
	protected void initResult() {
		result = new WebText(getElementInfo());
	}

	@Override
	protected void correctSettingsAndData() {
		String out = result.getData();
		out = replaceVariables(out);
		result.setData(out);
	}
}