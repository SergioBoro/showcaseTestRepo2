package ru.curs.showcase.core.grid;

import ru.curs.showcase.app.api.datapanel.*;
import ru.curs.showcase.app.api.grid.*;
import ru.curs.showcase.core.SourceSelector;
import ru.curs.showcase.core.command.*;
import ru.curs.showcase.core.sp.*;
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

	/**
	 * Явное сохранение состояния в конце нужно на случай удаления состояния по
	 * таймауту к этому моменту. Например, в случае долгого выполнения функции
	 * или не правильных настроек кэша.
	 * 
	 * @see ru.curs.showcase.core.command.ServiceLayerCommand#mainProc()
	 **/
	@Override
	protected void mainProc() throws Exception {
		SourceSelector<GridGateway> selector = new GridSelector(getElementInfo());
		GridGateway gateway = selector.getGateway();
		GridFactory factory = null;
		RecordSetElementRawData raw = null;
		GridServerState state = getGridState(getContext(), getElementInfo());

		if (getElementInfo().loadByOneProc()) {
			raw = gateway.getRawDataAndSettings(getContext(), getElementInfo());
			factory = new GridFactory(raw, state);
			factory.setApplyLocalFormatting(applyLocalFormatting);
			setResult(factory.build());
		} else {
			if (getContext().isFirstLoad()) {
				SourceSelector<ElementSettingsGateway> sselector =
					new GridSettingsSelector(getElementInfo());
				ElementSettingsGateway sgateway = sselector.getGateway();
				raw = sgateway.getRawData(getContext(), getElementInfo());
				factory = new GridFactory(raw, state);
				factory.buildStepOne();
				gateway.continueSession(sgateway);
			} else {
				factory = new GridFactory(getContext(), getElementInfo(), state);
				factory.buildStepOneFast();
			}
			raw = gateway.getRawData(getContext(), getElementInfo());
			factory.setSource(raw);
			factory.setApplyLocalFormatting(applyLocalFormatting);
			setResult(factory.buildStepTwo());
		}
		AppInfoSingleton.getAppInfo().storeElementState(getSessionId(), getElementInfo(),
				getContext(), state);
	}

	private GridServerState getGridState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state;
		if (context.isFirstLoad()) {
			state = prepareInitGridServerState(context, elementInfo);
		} else {
			state =
				(GridServerState) AppInfoSingleton.getAppInfo().getElementState(getSessionId(),
						elementInfo, context);
			if (state == null) {
				// состояние устарело или память была очищена
				state = prepareInitGridServerState(context, elementInfo);
				context.setIsFirstLoad(true);
			}
		}
		return state;
	}

	private GridServerState prepareInitGridServerState(final GridContext context,
			final DataPanelElementInfo elementInfo) {
		GridServerState state = new GridServerState();
		AppInfoSingleton.getAppInfo().storeElementState(getSessionId(), elementInfo, context,
				state);
		return state;
	}

}
