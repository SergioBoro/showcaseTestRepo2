package ru.curs.showcase.model.svg;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.util.xml.XMLUtils;

/**
 * Базовый класс команды экспорта из SVG.
 * 
 * @author den
 * 
 * @param <T>
 */
public abstract class AbstractSVGCommand<T> extends ServiceLayerCommand<T> {

	private String input;

	private final GeoMapExportSettings settings;

	private final ImageFormat imageFormat;

	public AbstractSVGCommand(final CompositeContext aContext,
			final GeoMapExportSettings aSettings, final ImageFormat aImageFormat,
			final String aInput) {
		super(aContext);
		input = aInput;
		settings = aSettings;
		imageFormat = aImageFormat;
	}

	@InputParam
	public String getInput() {
		return input;
	}

	@InputParam
	public GeoMapExportSettings getSettings() {
		return settings;
	}

	@InputParam
	public ImageFormat getImageFormat() {
		return imageFormat;
	}

	private String checkSVGEncoding(final String aSvg) {
		if (aSvg.startsWith(XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8)) {
			return aSvg;
		} else {
			return XMLUtils.XML_VERSION_1_0_ENCODING_UTF_8 + "\n" + aSvg;
		}
	}

	@Override
	protected void mainProc() throws Exception {
		input = checkSVGEncoding(input);
	}
}
