package ru.curs.showcase.model.frame;

import ru.curs.showcase.app.api.event.CompositeContext;

/**
 * Команда получения одного "специального" фрейма главной формы.
 * 
 * @author den
 * 
 */
public final class MainPageFrameGetCommand extends AbstractMainPageFrameCommand<String> {

	private final MainPageFrameType type;

	public MainPageFrameGetCommand(final String aSessionId, final CompositeContext aContext,
			final MainPageFrameType aType) {
		super(aSessionId, aContext);
		type = aType;
	}

	@Override
	protected void mainProc() throws Exception {
		String result = getRawMainPageFrame(getContext(), type);
		MainPageFrameFactory factory = new MainPageFrameFactory(true);
		setResult(factory.build(result));
	}

}
