package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class CommandMenu extends Composite implements HasCloseHandlers<CommandMenu>, PopupAware {

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	@UiField
	protected MenuBar menu;

	@UiField
	protected FocusPanel focusPanel;

	private ItemSelectionHandler selectionHandler;

	private final Map<MenuItem, CommandMenuItem> itemsMap;

	private List<CommandMenuItem> previousItems = new ArrayList<CommandMenuItem>();

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		itemsMap = new HashMap<MenuItem, CommandMenuItem>();
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);

		menu.setItemSelectionHandler(new ItemSelectionHandler() {

			@Override
			public void onItemSelected() {
				notifyItemSelectionHandler();
			}
		});
	}

	public void setLargePadding() {
		menu.addStyleDependentName("largePadding");
	}

	public void setItems(final List<CommandMenuItem> items) {
		menu.clearItems();
		itemsMap.clear();
		previousItems = items;

		final Iterator<CommandMenuItem> iterator = items.iterator();

		// TODO remove this by using a callback
		// IMPORTANT Should try to add the first two items so the filtrableCommandMenu can select it.
		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) return;
			addItem(iterator.next());
		}

		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (previousItems != items || !iterator.hasNext()) return false;

				addItem(iterator.next());
				return true;
			}
		});
	}

	public void setItem(final CommandMenuItem item) {
		menu.clearItems();
		addItem(item);
	}

	private void addItem(final CommandMenuItem item) {
		itemsMap.put(item.getMenuItem(), item);
		menu.addItem(item.getMenuItem());
	}

	@Override
	public void show() {}

	@Override
	public void hide() {
		if (!isVisible()) return;

		CloseEvent.fire(this, this);
	}

	@UiHandler("focusPanel")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ESCAPE) return;

		event.stopPropagation();
		hide();
	}

	@UiHandler("focusPanel")
	protected void handleClick(final ClickEvent event) {
		event.stopPropagation();
		hide();
	}

	@UiHandler("focusPanel")
	protected void handleDoubleClick(final DoubleClickEvent event) {
		event.stopPropagation();
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<CommandMenu> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	public void setItemSelectionHandler(final ItemSelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	public CommandMenuItem getSelectedItem() {
		return itemsMap.get(menu.getSelectedItem());
	}

	private void notifyItemSelectionHandler() {
		if (selectionHandler == null) return;
		selectionHandler.onItemSelected();
	}

	public void selectItemDown() {
		menu.moveSelectionDown();
	}

	public void selectItemUp() {
		menu.moveSelectionUp();
	}

	public void selectFirstItem() {
		menu.selectFirstItem();
	}

	public void setFocusWhenMouseOver(final boolean bool) {
		menu.setFocusOnHoverEnabled(bool);
	}

	public void setSelected(final CommandMenuItem item) {
		menu.selectItem(item.getMenuItem());

	}

	public void focus() {
		menu.focus();
	}
}
