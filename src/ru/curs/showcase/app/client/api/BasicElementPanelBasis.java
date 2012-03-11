/**
 * 
 */
package ru.curs.showcase.app.client.api;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.CompositeContext;

import com.google.gwt.user.client.Timer;

/**
 * @author anlug
 * 
 */
public abstract class BasicElementPanelBasis implements BasicElementPanel {

	/**
	 * Таймер, для обновление данных панели элемента через заданные интервалы
	 * времени.
	 */
	private Timer timer;

	/**
	 * @return the timer
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * @param atimer
	 *            the timer to set
	 */
	public void setTimer(final Timer atimer) {
		this.timer = atimer;
	}

	/**
	 * CompositeContext элемента, с которым он был отрисован последний раз..
	 */
	private CompositeContext context;

	public void setContext(final CompositeContext acontext) {
		this.context = acontext;
	}

	/**
	 * Расширенная функция получения контекста. Создает related элементы
	 * контекста. При этом учитывается ситуация, когда related панель еще не
	 * отрисована. Кроме того, не передаются данные о себе - они добавляются на
	 * сервере чтобы избежать дублирования данных при передаче.
	 * 
	 * @see ru.curs.showcase.app.client.api.BasicElementPanel#getContext()
	 **/
	@Override
	public CompositeContext getContext() {
		for (ID id : elementInfo.getRelated()) {
			final BasicElementPanel elementPanel = ActionExecuter.getElementPanelById(id);
			if (elementPanel == this) {
				continue;
			}
			if ((elementPanel != null) && (elementPanel.getContext() != null)) {
				context.addRelated(id, elementPanel.getDetailedContext());
			} else {
				context.addRelated(id, new CompositeContext());
			}
		}
		return context;
	}

	private DataPanelElementInfo elementInfo;

	@Override
	public DataPanelElementInfo getElementInfo() {
		return elementInfo;
	}

	public void setElementInfo(final DataPanelElementInfo aelement) {
		this.elementInfo = aelement;
	}

	@Override
	public CompositeContext getDetailedContext() {
		return getContext();
	}

	@Override
	public void prepareSettings(final boolean aKeepElementSettings) {
		// ничего не делаем для элементов, не имеющих настроек
	}

}
