package ru.curs.showcase.core.grid;

import java.util.List;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.command.*;

/**
 * Команда получения данных для TreeGrid.
 * 
 */
public class TreeGridDataGetCommand extends DataPanelElementCommand<List<TreeGridModel>> {

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public TreeGridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo) {
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
		setResult(GridTransformer.gridToTreeGridData(grid));
	}

}
