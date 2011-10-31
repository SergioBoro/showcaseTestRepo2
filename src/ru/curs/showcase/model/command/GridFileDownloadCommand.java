package ru.curs.showcase.model.command;

import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.grid.*;
import ru.curs.showcase.util.OutputStreamDataFile;

/**
 * Команда для скачивания файла с сервера в гриде.
 * 
 */
public class GridFileDownloadCommand extends DataPanelElementCommand<OutputStreamDataFile> {

	private final String linkId;
	private final String recordId;

	@InputParam
	public String getLinkId() {
		return linkId;
	}

	@InputParam
	public String getRecordId() {
		return recordId;
	}

	public GridFileDownloadCommand(final CompositeContext aContext,
			final DataPanelElementInfo aElInfo, final String aLinkId, final String aRecordId) {
		super(aContext, aElInfo);
		linkId = aLinkId;
		recordId = aRecordId;
	}

	@Override
	protected void mainProc() throws Exception {
		GridGateway gateway = new GridDBGateway();
		OutputStreamDataFile file =
			gateway.downloadFile(getContext(), getElementInfo(), linkId, recordId);
		// UserXMLTransformer transformer =
		// new UserXMLTransformer(file, getElementInfo().getProcs().get(linkId),
		// new DataPanelElementContext(getContext(), getElementInfo()));
		// transformer.checkAndTransform();
		// setResult(transformer.getOutputStreamResult());
		setResult(file);
	}

	@Override
	protected void logOutput() {
		super.logOutput();

		LOGGER.info(String.format("Размер скачиваемого файла: %d байт", getResult().getData()
				.size()));
	}

}
