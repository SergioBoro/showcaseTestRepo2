/**
 * 
 */
package ru.curs.showcase.app.client;

import ru.curs.showcase.app.client.api.*;

/**
 * 
 * Класс определяющий Java функции gwt клиентского кода (JSNI-технология),
 * которые будут выполняться при их вызове в javaScript из dom-модели Showcase.
 * 
 * @author anlug
 * 
 */
public final class FeedbackJSNI {

	private FeedbackJSNI() {

	}

	/**
	 * 
	 * Функция возвращает последний main-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String getElementMainContext(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			if (((BasicElementPanelBasis) bep).getContext() != null) {
				return ((BasicElementPanelBasis) bep).getContext().getMain();
			}
		}
		return null;
	}

	/**
	 * 
	 * Функция возвращает последний addition-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String getElementAdditionContext(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			if (((BasicElementPanelBasis) bep).getContext() != null) {
				return ((BasicElementPanelBasis) bep).getContext().getAdditional();
			}
		}
		return null;

	}

	/**
	 * 
	 * Функция возвращает последний addition-context с которым был отрисован
	 * элемент.
	 * 
	 * @param elementId
	 *            - Id элемента
	 * @return - String
	 */
	public static String refreshElementFromBase(final String elementId) {
		BasicElementPanel bep = ActionExecuter.getElementPanelById(elementId);
		if (bep != null) {
			bep.refreshPanel();
		}
		return null;

	}

	/**
	 * 
	 * Процедура инициализирующая Java функции gwt клиентского кода
	 * (JSNI-технология), которые будут выполняться при их вызове в javaScript
	 * из dom-модели Showcase. Данная функция должна вызываться при
	 * инициализации-загрузке приложения.
	 * 
	 */
	public static native void initFeedbackJSNIFunctions() /*-{
		$wnd.getMainContextFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getElementMainContext(Ljava/lang/String;);
		$wnd.getAdditionalContextFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::getElementAdditionContext(Ljava/lang/String;);
		$wnd.refreshElementFromBaseFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.FeedbackJSNI::refreshElementFromBase(Ljava/lang/String;);
        $wnd.showAboutFeedbackJSNIFunction = 
		@ru.curs.showcase.app.client.About::showAbout();		
	}-*/;

}
