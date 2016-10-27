package ru.curs.showcase.app.api.grid.toolbar;

import java.util.Map.Entry;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.fx.client.animation.*;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.*;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.*;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.DataServiceAsync;
import ru.curs.showcase.app.client.GWTServiceCallback;
import ru.curs.showcase.app.client.api.*;

/**
 * Помощник загрузки и формирования панели инструментов.
 * 
 * @author bogatov
 * 
 */
public abstract class ToolBarHelper {

	private static final int BLINKING_DURATION = 2000;
	private static final int BLINKING_INTERVAL = 50;

	private static final String TOOLBAR_HEIGHT = "28px";
	private static final int ICON_SIZE = 16;

	private Timer toolBarRefreshTimer = null;
	private final DataServiceAsync dataService;
	private final Panel panel;
	private final BasicElementPanelBasis basicElementPanelBasis;
	private boolean isStaticToolBar = false;

	private int blinkingCount = 0;
	private boolean blinkingStartTimer = false;

	/**
	 * 
	 * @param oDataService
	 *            - имплементация DataService.
	 * @param oPanel
	 *            - панель на которую помещается ToolBar.
	 */
	public ToolBarHelper(final DataServiceAsync oDataService,
			final BasicElementPanelBasis oBasicElementPanelBasis) {
		this.dataService = oDataService;
		this.basicElementPanelBasis = oBasicElementPanelBasis;

		this.panel = new SimplePanel();
		this.panel.setHeight(TOOLBAR_HEIGHT);
	}

	/**
	 * Обновлении панели инструментов. Очистка текущей панели и формирование
	 * панели инструментов на основе полученных метаданных.
	 */
	public void fillToolBar() {
		if (isStaticToolBar) {
			return;
		}
		final DataPanelElementInfo elInfo = basicElementPanelBasis.getElementInfo();
		if (elInfo.isToolBarProc()) {

			blinkingStartTimer = true;

			if (toolBarRefreshTimer != null) {
				toolBarRefreshTimer.cancel();
			}

			toolBarRefreshTimer = new Timer() {
				@Override
				public void run() {

					blinkingStartTimer = false;
					blinkingCount++;

					CompositeContext context = getContext(basicElementPanelBasis);
					dataService.getGridToolBar(context, elInfo,
							new GWTServiceCallback<GridToolBar>(
									"при получении данных панели инструментов грида с сервера") {

								@Override
								public void onSuccess(final GridToolBar result) {
									panel.clear();
									ToolBar toolBar = new ToolBar();
									toolBar.setHeight(TOOLBAR_HEIGHT);
									toolBar.setBorders(false);
									addStaticItemToToolBar(toolBar);
									createDynamicToolBar(result, toolBar);
									panel.add(toolBar);

									blinkingCount--;
								}
							});
				}
			};
			toolBarRefreshTimer.schedule(Constants.GRID_SELECTION_DELAY);
		} else {
			isStaticToolBar = true;
			ToolBar toolBar = new ToolBar();
			toolBar.setHeight(TOOLBAR_HEIGHT);
			toolBar.setBorders(false);
			addStaticItemToToolBar(toolBar);
			panel.add(toolBar);
		}
	}

	private boolean needBlinking() {
		return blinkingStartTimer || (blinkingCount > 0);
	}

	private void blinkItem(final XElement xElement) {
		Fx fx = new Fx();
		fx.run(BLINKING_DURATION, new Blink(xElement, BLINKING_INTERVAL));
	}

	public Panel getToolBarPanel() {
		return this.panel;
	}

	private TextButton createTextButton(final BaseToolBarItem item) {
		TextButton textButton = null;
		if (item.isVisible()) {
			textButton = new TextButton();
			if (item.isDisable()) {
				textButton.setEnabled(false);
			}
			if (item.getText() != null && !item.getText().isEmpty()) {
				textButton.setText(item.getText());
			}
			if (item.getImg() != null && !item.getImg().isEmpty()) {
				textButton.setIcon(IconHelper.getImageResource(
						UriUtils.fromSafeConstant(item.getImg()), ICON_SIZE, ICON_SIZE));
			}
			if (item.getHint() != null) {
				textButton.setTitle(item.getHint());
			}
		}
		return textButton;
	}

	private MenuItem createMenuItem(final BaseToolBarItem item) {
		MenuItem menuItem = null;
		if (item.isVisible()) {
			menuItem = new MenuItem();
			if (item.isDisable()) {
				menuItem.setEnabled(false);
			}
			if (item.getText() != null && !item.getText().isEmpty()) {
				menuItem.setText(item.getText());
			}
			if (item.getImg() != null && !item.getImg().isEmpty()) {
				menuItem.setIcon(IconHelper.getImageResource(
						UriUtils.fromSafeConstant(item.getImg()), ICON_SIZE, ICON_SIZE));
			}
			if (item.getHint() != null) {
				menuItem.setTitle(item.getHint());
			}
		}
		return menuItem;
	}

	private void addToolBarItem(final ToolBarItem item, final Container toolBar) {
		final TextButton textButton = createTextButton(item);
		if (textButton != null) {
			if (item.getAction() != null) {
				textButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(final SelectEvent event) {
						if (needBlinking()) {
							blinkItem(textButton.getElement());
						} else {
							CompositeContext context = getContext(basicElementPanelBasis);
							Action action = item.getAction();
							action.setContext(context);
							action.setActionCaller(textButton);
							runAction(action);
						}
					}
				});
			}
			toolBar.add(textButton);
		}
	}

	// CHECKSTYLE:OFF
	private void createMenuItemToolBar(final AbstractToolBarItem obj, final Menu menu) {
		if (obj instanceof ToolBarItem) {
			final ToolBarItem item = (ToolBarItem) obj;
			final MenuItem menuItem = createMenuItem(item);
			if (menuItem != null) {
				if (item.getAction() != null) {
					menuItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(final SelectionEvent<Item> event) {
							if (needBlinking()) {
								blinkItem(menuItem.getElement());
							} else {
								CompositeContext context = getContext(basicElementPanelBasis);
								Action action = item.getAction();
								action.setContext(context);
								action.setActionCaller(menuItem);
								runAction(action);
							}
						}
					});
				}
				menu.add(menuItem);
			}
		} else if (obj instanceof ToolBarGroup) {
			ToolBarGroup group = (ToolBarGroup) obj;
			MenuItem menuItem = createMenuItem(group);
			if (menuItem != null) {
				Menu groupMenu = new Menu();
				for (AbstractToolBarItem item : group.getItems()) {
					createMenuItemToolBar(item, groupMenu);
				}
				menuItem.setSubMenu(groupMenu);
				menu.add(menuItem);
			}
		} else if (obj instanceof ToolBarSeparator) {
			SeparatorMenuItem separator = new SeparatorMenuItem();
			menu.add(separator);
		}
	}

	// CHECKSTYLE:ON

	private void createDynamicToolBar(final AbstractToolBarItem obj, final ToolBar toolBar) {
		if (obj instanceof ToolBarItem) {
			final ToolBarItem item = (ToolBarItem) obj;
			addToolBarItem(item, toolBar);
		} else if (obj instanceof ToolBarGroup) {
			ToolBarGroup group = (ToolBarGroup) obj;
			final TextButton textButton = createTextButton(group);
			if (textButton != null) {
				Menu menu = new Menu();
				for (AbstractToolBarItem item : group.getItems()) {
					createMenuItemToolBar(item, menu);
				}
				textButton.setMenu(menu);

				textButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(final SelectEvent event) {
						if (needBlinking()) {
							blinkItem(textButton.getElement());
						}
					}
				});

				toolBar.add(textButton);
			}
		} else if (obj instanceof ToolBarSeparator) {
			SeparatorToolItem separator = new SeparatorToolItem();
			toolBar.add(separator);
		}
	}

	private ToolBar createDynamicToolBar(final GridToolBar oGridToolBar, final ToolBar oToolBar) {
		ToolBar toolBar = oToolBar;
		if (toolBar == null) {
			toolBar = new ToolBar();
			toolBar.setHeight(TOOLBAR_HEIGHT);
		}
		for (AbstractToolBarItem obj : oGridToolBar.getItems()) {
			createDynamicToolBar(obj, toolBar);
		}
		return toolBar;
	}

	private CompositeContext getContext(final BasicElementPanelBasis element) {
		CompositeContext elContext = element.getContext();
		CompositeContext context = new CompositeContext();
		context.setMain(elContext.getMain());
		context.setAdditional(elContext.getAdditional());
		context.setFilter(elContext.getFilter());
		context.setSession(elContext.getSession());
		context.setSessionParamsMap(elContext.getSessionParamsMap());
		context.setCurrentDatapanelWidth(elContext.getCurrentDatapanelWidth());
		context.setCurrentDatapanelHeight(elContext.getCurrentDatapanelHeight());
		for (Entry<ID, CompositeContext> entry : elContext.getRelated().entrySet()) {
			context.addRelated(entry.getKey(), entry.getValue());
		}
		context.addRelated(element.getElementInfo().getId(), element.getDetailedContext());
		return context;
	}

	/**
	 * Статичное добавление элементов на панель инструментов.
	 * 
	 * @param oToolBar
	 *            - ToolBar панель.
	 */
	public abstract void addStaticItemToToolBar(final ToolBar oToolBar);

	/**
	 * Выполнение события.
	 * 
	 * @param ac
	 *            - событие.
	 */
	public abstract void runAction(final Action ac);
}
