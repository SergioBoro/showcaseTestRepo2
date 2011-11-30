package ru.curs.showcase.model.html.xform;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.command.InputParam;
import ru.curs.showcase.model.html.XSLTransformationSelector;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.UserXMLTransformer;

/**
 * Команда для скачивания файла с сервера в XForms.
 * 
 * @author den
 * 
 */
public final class XFormDownloadCommand extends XFormContextCommand<OutputStreamDataFile> {

	private final String linkId;

	@InputParam
	public String getLinkId() {
		return linkId;
	}

	public XFormDownloadCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo,
			final String aLinkId) {
		super(aContext, aElInfo);
		linkId = aLinkId;
	}

	@Override
	protected void mainProc() throws Exception {
		XFormGateway gateway = new XFormDBGateway();
		OutputStreamDataFile file = gateway.downloadFile(getContext(), getElementInfo(), linkId);

		DataPanelElementProc proc = getElementInfo().getProcs().get(linkId);
		XSLTransformationSelector selector =
			new XSLTransformationSelector(getContext(), getElementInfo(), proc);
		DataFile<InputStream> transform = selector.getData();

		UserXMLTransformer transformer =
			new UserXMLTransformer(file, proc, transform, new DataPanelElementContext(
					getContext(), getElementInfo()));
		transformer.checkAndTransform();
		setResult(transformer.getOutputStreamResult());
	}

	@Override
	protected void logOutput() {
		super.logOutput();

		LOGGER.info(String.format("Размер скачиваемого файла: %d байт", getResult().getData()
				.size()));

	}

}
