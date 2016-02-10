package ru.curs.showcase.core.grid;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения метаданных для LyraGrid.
 * 
 */
public class LyraGridMetadataGetCommand extends DataPanelElementCommand<GridMetadata> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraGridMetadataGetCommand(final GridContext aContext,
			final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);
	}

	@InputParam
	@Override
	public LyraGridContext getContext() {
		return (LyraGridContext) super.getContext();
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {

		GridUtils.includeDataPanelWidthAndHeightInSessionContext(getContext());

		LyraGridGateway lgateway = new LyraGridGateway();
		BasicGridForm basicGridForm = lgateway.getLyraFormInstance(getContext(), getElementInfo());

		LyraGridMetaFactory factory =
			new LyraGridMetaFactory(getContext(), getElementInfo(), basicGridForm);
		GridMetadata gm = factory.buildMetadata();
		gm.setOkMessage(getContext().getOkMessage());

		setResult(gm);

	}

}
