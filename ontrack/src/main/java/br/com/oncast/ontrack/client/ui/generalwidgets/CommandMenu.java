package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
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

public class CommandMenu extends Composite implements HasCloseHandlers<CommandMenu> {

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	@UiField
	protected MenuBar menu;

	@UiField
	protected FocusPanel focusPanel;

	private ItemSelectionHandler selectionHandler;

	private final Map<MenuItem, CommandMenuItem> itensMap;

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		itensMap = new HashMap<MenuItem, CommandMenuItem>();
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

	public void setItens(final List<CommandMenuItem> items) {
		menu.clearItems();
		itensMap.clear();
		for (final CommandMenuItem item : items) {
			itensMap.put(item.getMenuItem(), item);
			menu.addItem(item.getMenuItem());
		}
	}

	public void show() {
		this.setVisible(true);
	}

	public void hide() {
		if (!this.isVisible()) return;
		this.setVisible(false);
		CloseEvent.fire(this, this);
	}

	@UiHandler("focusPanel")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ESCAPE) return;

		event.preventDefault();
		event.stopPropagation();
		hide();
	}

	@UiHandler("focusPanel")
	protected void handleClick(final ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		hide();
	}

	@UiHandler("focusPanel")
	protected void handleDoubleClick(final DoubleClickEvent event) {
		event.preventDefault();
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
		return itensMap.get(menu.getSelectedItem());
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
