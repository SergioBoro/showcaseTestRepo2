package ru.curs.showcase.model.xform;

import java.io.ByteArrayOutputStream;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.util.DataFile;
import ru.curs.showcase.util.xml.UserXMLTransformer;

/**
 * Команда загрузки файла на сервер для XForm.
 * 
 * @author den
 * 
 */
public final class XFormUploadCommand extends XFormContextCommand<Void> {

	private final String linkId;
	private final DataFile<ByteArrayOutputStream> file;

	public XFormUploadCommand(final String aSessionId, final XFormContext aContext,
			final DataPanelElementInfo aElInfo, final String aLinkId,
			final DataFile<ByteArrayOutputStream> aFile) {
		super(aSessionId, aContext, aElInfo);
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
		UserXMLTransformer transformer =
			new UserXMLTransformer(file, getElementInfo().getProcs().get(linkId));
		transformer.checkAndTransform();
		XFormGateway gateway = new XFormDBGateway();
		gateway.uploadFile(getContext(), getElementInfo(), linkId, transformer.getInputStreamResult());
	}

}
