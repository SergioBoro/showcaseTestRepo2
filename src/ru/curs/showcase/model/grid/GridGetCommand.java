package ru.curs.showcase.model.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.model.command.*;
import ru.curs.showcase.model.sp.*;
import ru.curs.showcase.runtime.AppInfoSingleton;

/**
 * Базовый класс для команды грида.
 * 
 * @author den
 * 
 */
public class GridGetCommand extends DataPanelElementCommand<Grid> {

	private final Boolean applyLocalFormatting;

	@Override
	protected DataPanelElementType getRequestedElementType() {
		return DataPanelElementType.GRID;
	}

	public GridGetCommand(final GridContext aContext, final DataPanelElementInfo aElInfo,
			final Boolean aApplyLocalFormatting) {
		super(aContext, aElInfo);
		applyLocalFormatting = aApplyLocalFormatting;
	}

	@InputParam
	@Override
	public GridContext getContext() {
		return (GridContext) super.getContext();
	}

	@Override
	protected void mainProc() throws Exception {
		GridDBGateway gateway = new GridDBGateway();
		GridDBFactory factory = null;
		RecordSetElementRawData raw = null;
		ElementSettingsDBGateway sgateway = null;
		GridServerState state = null;

		state = getGridState(getContext(), getElementInfo());

		if (getElementInfo().loadByOneProc()) {
			raw = gateway.getRawDataAndSettings(getContext(), getElementInfo());
			factory = new GridDBFactory(raw, state);
			factory.setApplyLocalFormatting(applyLocalFormatting);
			setResult(factory.build());
		} else {
			if (getContext().isFirstLoad()) {
				sgateway = new ElementSettingsDBGateway();
				raw = sgateway.getRawData(getContext(), getElementInfo());
				factory = new GridDBFactory(raw, state);
				factory.buildStepOne();
				gateway.setConn(sgateway.getConn());
			} else {
				factory = new GridDBFactory(getContext(), getElementInfo(), state);
				factory.buildStepOneFast();
			}
			raw = gateway.getRawData(getContext(), getElementInfo());
			factory.setSource(raw);
			factory.setApplyLocalFormatting(applyLocalFormatting);
			setResult(factory.buildStepTwo());
		}
	}

	private GridServerState getGridState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state;
		if (context.isFirstLoad()) {
			state = saveGridServerState(context, elementInfo);
		} else {
			state =
				(GridServerState) AppInfoSingleton.getAppInfo().getElementState(getSessionId(),
						elementInfo, context);
			if (state == null) {
				// состояние по каким-либо причинам не сохранено
				context.setIsFirstLoad(false);
				state = saveGridServerState(context, elementInfo);
			}
		}
		return state;
	}

	private GridServerState saveGridServerState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(getSessionId(), elementInfo, context,
				state);
		return state;
	}

}
