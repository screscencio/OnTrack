package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class CommandMenu extends Composite {

	private static CommandMenuUiBinder uiBinder = GWT.create(CommandMenuUiBinder.class);

	interface CommandMenuUiBinder extends UiBinder<Widget, CommandMenu> {}

	@UiField
	protected MenuBar menu;

	@UiField
	protected FocusPanel focusPanel;

	private CloseHandler closeHandler;

	private ItemSelectionHandler selectionHandler;

	@UiFactory
	protected MenuBar createMenuBar() {
		return new MenuBar(true);
	}

	public CommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		menu.setAnimationEnabled(true);
		menu.setAutoOpen(true);

		menu.setItemSelectionHandler(new ItemSelectionHandler() {

			@Override
			public void onItemSelected() {
				notifyItemSelectionHandler();
			}
		});
	}

	public void setItems(final List<CommandMenuItem> items) {
		menu.clearItems();
		for (final CommandMenuItem item : items) {
			final MenuItem menuItem = new MenuItem(item.getText(), true, new Command() {

				@Override
				public void execute() {
					hide();
					item.getCommand().execute();
				}
			});
			menu.addItem(menuItem);
		}
	}

	public void show() {
		this.setVisible(true);
	}

	public void hide() {
		if (!this.isVisible()) return;
		this.setVisible(false);
		if (closeHandler != null) closeHandler.onClose();
	}

	@UiHandler("focusPanel")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() != KEY_ESCAPE) return;

		event.preventDefault();
		event.stopPropagation();
		hide();
	}

	public void addCloseHandler(final CloseHandler closeHandler) {
		this.closeHandler = closeHandler;
	}

	public void setItemSelectionHandler(final ItemSelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	public MenuItem getSelectedItem() {
		return menu.getSelectedItem();
	}

	private void notifyItemSelectionHandler() {
		if (selectionHandler == null) return;
		selectionHandler.onItemSelected();
	}

	public void setSelectedItem(final MenuItem selectedItem) {
		menu.selectItem(selectedItem);
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

	public void clearItems() {
		menu.clearItems();
	}

}
