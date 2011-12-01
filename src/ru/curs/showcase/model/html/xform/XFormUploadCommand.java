package ru.curs.showcase.model.html.xform;

import java.io.InputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.command.InputParam;
import ru.curs.showcase.model.html.XSLTransformationSelector;
import ru.curs.showcase.util.*;
import ru.curs.showcase.util.xml.UserXMLTransformer;

/**
 * Команда загрузки файла на сервер для XForm.
 * 
 * @author den
 * 
 */
public final class XFormUploadCommand extends XFormContextCommand<Void> {

	private final String linkId;
	private final OutputStreamDataFile file;

	@InputParam
	public String getLinkId() {
		return linkId;
	}

	@InputParam
	public OutputStreamDataFile getFile() {
		return file;
	}

	public XFormUploadCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo,
			final String aLinkId, final OutputStreamDataFile aFile) {
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
		XSLTransformationSelector selector =
			new XSLTransformationSelector(getContext(), getElementInfo(), proc);
		DataFile<InputStream> transform = selector.getData();

		UserXMLTransformer transformer = new UserXMLTransformer(file, proc, transform);
		transformer.checkAndTransform();
		XFormGateway gateway = new XFormDBGateway();
		gateway.uploadFile(getContext(), getElementInfo(), linkId,
				transformer.getInputStreamResult());
	}

}
