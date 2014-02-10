package ru.curs.showcase.app.api.grid.toolbar;

import java.util.Map;

import ru.curs.showcase.app.api.ID;
import ru.curs.showcase.app.api.datapanel.DataPanelElementInfo;
import ru.curs.showcase.app.api.event.*;
import ru.curs.showcase.app.api.services.DataServiceAsync;
import ru.curs.showcase.app.client.GWTServiceCallback;
import ru.curs.showcase.app.client.api.*;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.event.*;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.*;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.*;

/**
 * Помощник загрузки и формирования панели инструментов.
 * 
 * @author bogatov
 * 
 */
public abstract class ToolBarHelper {
	private static final int ICON_SIZE = 16;
	private Timer toolBarRefreshTimer = null;
	private final DataServiceAsync dataService;
	private final Panel panel;
	private final BasicElementPanelBasis basicElementPanelBasis;
	private boolean isStaticToolBar = false;

	// private final Panel panel = new SimplePanel();

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
			panel.clear();
			if (toolBarRefreshTimer != null) {
				toolBarRefreshTimer.cancel();
			}
			toolBarRefreshTimer = new Timer() {
				@Override
				public void run() {
					CompositeContext elContext = basicElementPanelBasis.getContext();
					CompositeContext context = new CompositeContext();
					context.setMain(elContext.getMain());
					context.setAdditional(elContext.getAdditional());
					context.setFilter(elContext.getFilter());
					context.setSession(elContext.getSession());
					context.setSessionParamsMap(elContext.getSessionParamsMap());
					Map<ID, CompositeContext> aRelated = context.getRelated();
					aRelated.put(elInfo.getId(), basicElementPanelBasis.getDetailedContext());

					dataService.getGridToolBar(context, elInfo,
							new GWTServiceCallback<GridToolBar>(
									"при получении данных панели инструментов грида с сервера") {

								@Override
								public void onSuccess(final GridToolBar result) {
									ToolBar toolBar = new ToolBar();
									addStaticItemToToolBar(toolBar);
									createDynamicToolBar(result, toolBar);
									panel.add(toolBar);
								}
							});
				}
			};
			toolBarRefreshTimer.schedule(Constants.GRID_SELECTION_DELAY);
		} else {
			isStaticToolBar = true;
			ToolBar toolBar = new ToolBar();
			addStaticItemToToolBar(toolBar);
			panel.add(toolBar);
		}
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
		TextButton textButton = createTextButton(item);
		if (textButton != null && item.getAction() != null) {
			if (item.getAction() != null) {
				textButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(final SelectEvent event) {
						runAction(item.getAction());
					}
				});
			}
			toolBar.add(textButton);
		}
	}

	private void createMenuItemToolBar(final AbstractToolBarItem obj, final Menu menu) {
		if (obj instanceof ToolBarItem) {
			final ToolBarItem item = (ToolBarItem) obj;
			MenuItem menuItem = createMenuItem(item);
			if (menuItem != null) {
				if (item.getAction() != null) {
					menuItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(final SelectionEvent<Item> event) {
							runAction(item.getAction());
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

	private void createDynamicToolBar(final AbstractToolBarItem obj, final ToolBar toolBar) {
		if (obj instanceof ToolBarItem) {
			final ToolBarItem item = (ToolBarItem) obj;
			addToolBarItem(item, toolBar);
		} else if (obj instanceof ToolBarGroup) {
			ToolBarGroup group = (ToolBarGroup) obj;
			TextButton textButton = createTextButton(group);
			if (textButton != null) {
				Menu menu = new Menu();
				for (AbstractToolBarItem item : group.getItems()) {
					createMenuItemToolBar(item, menu);
				}
				textButton.setMenu(menu);
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
		}
		for (AbstractToolBarItem obj : oGridToolBar.getItems()) {
			createDynamicToolBar(obj, toolBar);
		}
		return toolBar;
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
