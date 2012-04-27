package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения данных для LiveGrid.
 * 
 */
public class ExtGridDataGetCommand extends
		DataPanelElementCommand<ExtGridPagingLoadResult<ExtGridData>> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public ExtGridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo) {
		super(aContext, aElInfo);

	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	/**
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {
		GridGetCommand command = new GridGetCommand(getContext(), getElementInfo(), true);
		Grid grid = command.execute();
		setResult(GridTransformer.gridToExtGridData(grid));
	}

}
