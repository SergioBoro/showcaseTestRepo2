package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.command.InputParam;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Команда загрузки файла на сервер для XForm.
 * 
 * @author den
 * 
 */
public final class XFormUploadCommand extends XFormContextCommand<Void> {

	private final ID linkId;
	private final OutputStreamDataFile file;

	@InputParam
	public ID getLinkId() {
		return linkId;
	}

	@InputParam
	public OutputStreamDataFile getFile() {
		return file;
	}

	public XFormUploadCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo,
			final ID aLinkId, final OutputStreamDataFile aFile) {
		super(aContext, aElInfo);
		linkId = aLinkId;
		file = aFile;
	}

	@Override
	protected void logInputParams() {
		super.logInputParams();
		LOGGER.info("Получен файл '" + file.getName() + "' размером " + file.getData().size()
				+ " байт");
	}

	@Override
	protected void mainProc() throws Exception {
		DataPanelElementProc proc = getElementInfo().getProcs().get(linkId);

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(file, proc, getContext(), getElementInfo());
		transformer.transform();

		HTMLAdvGateway gateway = new HtmlDBGateway();
		gateway.uploadFile(getContext(), getElementInfo(), linkId,
				transformer.getInputStreamResult());
	}

}
