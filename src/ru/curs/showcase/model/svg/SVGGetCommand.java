package ru.curs.showcase.model.svg;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.app.api.geomap.*;

/**
 * Команда, возвращающая исходный SVG после предварительной обработки.
 * 
 * @author den
 * 
 */
public class SVGGetCommand extends AbstractSVGCommand<String> {

	public SVGGetCommand(final CompositeContext aContext, final GeoMapExportSettings aSettings,
			final ImageFormat aImageFormat, final String aInput) {
		super(aContext, aSettings, aImageFormat, aInput);
	}

	@Override
	protected void mainProc() throws Exception {
		super.mainProc();
		setResult(getInput());
	}

}
