package ru.curs.showcase.core.grid;

import ru.curs.lyra.BasicGridForm;
import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения данных для LyraGrid.
 * 
 */
public class LyraGridDataGetCommand extends DataPanelElementCommand<GridData> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public LyraGridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo) {
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

		LyraGridGateway lgateway = new LyraGridGateway();
		BasicGridForm basicGridForm = lgateway.getLyraFormInstance(getContext(), getElementInfo());

		LyraGridDataFactory factory =
			new LyraGridDataFactory(getContext(), getElementInfo(), basicGridForm);
		GridData gd = factory.buildData();
		gd.setOkMessage(getContext().getOkMessage());

		setResult(gd);

	}

}
