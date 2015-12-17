package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.sp.RecordSetElementRawData;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Команда получения данных для грида.
 * 
 */
public class GridDataGetCommand extends DataPanelElementCommand<GridData> {

	private final Boolean applyLocalFormatting;

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridDataGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final Boolean aApplyLocalFormatting) {
		super(aContext, aElInfo);
		applyLocalFormatting = aApplyLocalFormatting;
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

		GridUtils.includeDataPanelWidthAndHeightInSessionContext(getContext());

		SourceSelector<GridGateway> selector = new GridSelector(getElementInfo());
		GridGateway gateway = selector.getGateway();
		RecordSetElementRawData rawData = gateway.getRawData(getContext(), getElementInfo());

		GridServerState state =
			(GridServerState) AppInfoSingleton.getAppInfo().getElementState(getSessionId(),
					getElementInfo(), getContext());
		if (state == null) {
			GridMetadataGetCommand command =
				new GridMetadataGetCommand(getContext(), getElementInfo());
			command.execute();
			state = command.getGridServerState();
		} else {
			if (state.isForceLoadSettings()) {
				GridMetadataGetCommand command =
					new GridMetadataGetCommand(getContext(), getElementInfo());
				state.setTotalCount(command.getTotalCount());
			}
		}

		GridDataFactory factory = new GridDataFactory(rawData, state, applyLocalFormatting);
		GridData grid = factory.buildData();
		grid.setOkMessage(getContext().getOkMessage());

		setResult(grid);

	}
}
