package ru.curs.showcase.model.frame;

import ru.curs.showcase.app.api.MainPage;
import ru.curs.showcase.app.api.event.CompositeContext;
import ru.curs.showcase.runtime.AppProps;

/**
 * Команда для получения всей информации о главной странице приложения.
 * 
 * @author den
 * 
 */
public final class MainPageGetCommand extends AbstractMainPageFrameCommand<MainPage> {

	public MainPageGetCommand(final String aSessionId, final CompositeContext aContext) {
		super(aSessionId, aContext);
	}

	@Override
	protected void mainProc() throws Exception {
		MainPage mp = new MainPage();
		String value = AppProps.getOptionalValueByName(AppProps.HEADER_HEIGHT_PROP);
		if (value != null) {
			mp.setHeaderHeight(value);
		} else {
			mp.setHeaderHeight(AppProps.DEF_HEADER_HEIGTH);
		}
		value = AppProps.getOptionalValueByName(AppProps.FOOTER_HEIGHT_PROP);
		if (value != null) {
			mp.setFooterHeight(value);
		} else {
			mp.setFooterHeight(AppProps.DEF_FOOTER_HEIGTH);
		}

		MainPageFrameFactory factory = new MainPageFrameFactory(false);
		String html = getRawMainPageFrame(getContext(), MainPageFrameType.HEADER);
		html = factory.build(html);
		mp.setHeader(html);

		html = getRawMainPageFrame(getContext(), MainPageFrameType.FOOTER);
		html = factory.build(html);
		mp.setFooter(html);

		html = getRawMainPageFrame(getContext(), MainPageFrameType.WELCOME);
		html = factory.build(html);
		mp.setWelcome(html);

		setResult(mp);
	}

}
