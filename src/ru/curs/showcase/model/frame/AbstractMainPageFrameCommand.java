package ru.curs.showcase.model.frame;

import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.model.command.ServiceLayerCommand;

/**
 * Базовый класс для команд, возвращающих фрейм(ы) главной страницы.
 * 
 * @author den
 * 
 * @param <T>
 *            - тип результата.
 */
public abstract class AbstractMainPageFrameCommand<T> extends ServiceLayerCommand<T> {

	public AbstractMainPageFrameCommand(final CompositeContext aContext) {
		super(aContext);
	}

	protected String getRawMainPageFrame(final CompositeContext context,
			final MainPageFrameType type) {
		MainPageFrameSelector selector = new MainPageFrameSelector(type);
		String result = selector.getGateway().getRawData(context);
		return result;
	}
}
