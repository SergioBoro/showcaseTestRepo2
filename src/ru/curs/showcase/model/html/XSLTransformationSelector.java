package ru.curs.showcase.model.html;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.SourceSelector;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.exception.SettingsFileType;

/**
 * Класс для выбора источника XSL трансформации.
 * 
 * @author den
 * 
 */
public class XSLTransformationSelector extends SourceSelector<ElementPartsGateway> {

	private final CompositeContext context;
	private final DataPanelElementInfo elInfo;

	public XSLTransformationSelector(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aElInfo.getTransformName());
		context = aContext;
		elInfo = aElInfo;
	}

	@Override
	public ElementPartsGateway getGateway() {
		ElementPartsGateway gateway;
		switch (sourceType()) {
		case JYTHON:
			gateway = new ElementPartsJythonGateway();
			break;
		case FILE:
			gateway = new ElementPartsFileGateway();
			break;
		default:
			gateway = new ElementPartsDBGateway();
		}
		gateway.setSource(elInfo.getTransformName());
		gateway.setType(SettingsFileType.XSLT);
		return gateway;
	}

	@Override
	protected String getFileExt() {
		return "xsl";
	}

	public DataFile<InputStream> getData() {
		return getGateway().getRawData(context, elInfo);
	}

}
