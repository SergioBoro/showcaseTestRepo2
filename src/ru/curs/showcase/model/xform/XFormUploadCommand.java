package ru.curs.showcase.model.xform;

import java.io.ByteArrayOutputStream;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.html.XFormContext;
import ru.curs.showcase.model.command.InputParam;
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

	@InputParam
	public String getLinkId() {
		return linkId;
	}

	@InputParam
	public DataFile<ByteArrayOutputStream> getFile() {
		return file;
	}

	public XFormUploadCommand(final XFormContext aContext, final DataPanelElementInfo aElInfo,
			final String aLinkId, final DataFile<ByteArrayOutputStream> aFile) {
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
		UserXMLTransformer transformer =
			new UserXMLTransformer(file, getElementInfo().getProcs().get(linkId),
					new DataPanelElementContext(getContext(), getElementInfo()));
		transformer.checkAndTransform();
		XFormGateway gateway = new XFormDBGateway();
		gateway.uploadFile(getContext(), getElementInfo(), linkId,
				transformer.getInputStreamResult());
	}

}
