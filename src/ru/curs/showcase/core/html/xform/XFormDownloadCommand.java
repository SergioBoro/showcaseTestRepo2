package ru.curs.showcase.core.html.xform;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.core.command.InputParam;
import ru.curs.showcase.core.html.*;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Команда для скачивания файла с сервера в XForms.
 * 
 * @author den
 * 
 */
public final class XFormDownloadCommand extends XFormContextCommand<OutputStreamDataFile> {

	private final ID linkId;

	@InputParam
	public ID getLinkId() {
		return linkId;
	}

	public XFormDownloadCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo,
			final ID aLinkId) {
		super(aContext, aElInfo);
		linkId = aLinkId;
	}

	@Override
	protected void mainProc() throws Exception {
		HTMLAdvGateway gateway = new HtmlDBGateway();
		OutputStreamDataFile file = gateway.downloadFile(getContext(), getElementInfo(), linkId);
		DataPanelElementProc proc = getElementInfo().getProcs().get(linkId);

		SelectableXMLTransformer transformer =
			new SelectableXMLTransformer(file, proc, getContext(), getElementInfo());
		transformer.transform();

		setResult(transformer.getOutputStreamResult());
	}

	@Override
	protected void logOutput() {
		super.logOutput();

		LOGGER.info(String.format("Размер скачиваемого файла: %d байт", getResult().getData()
				.size()));

	}

}
