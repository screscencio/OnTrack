package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

// TODO++++ refactor this class and SearchScopeFiltableCommandMenu to extract duplicated code
// TODO++++ refactor this class and ProjectMenuWidget to extract duplicated code
// TODO++++ merge this class with FilterEngine
public class FiltrableCommandMenu extends Composite implements HasCloseHandlers<FiltrableCommandMenu>, PopupAware {

	private static final List<Integer> KEY_DOWN_HANDLED_KEYS = Arrays.asList(new Integer[] { KEY_DOWN, KEY_UP, KEY_TAB });

	private final int maxHeight;

	interface FiltrableCommandMenuUiBinder extends UiBinder<Widget, FiltrableCommandMenu> {}

	private static FiltrableCommandMenuUiBinder defaultUiBinder = GWT.create(FiltrableCommandMenuUiBinder.class);

	@UiTemplate("FiltrableCommandMenuForProjectSwitchingMenu.ui.xml")
	interface FiltrableCommandMenuForProjectSwitchingMenuUiBinder extends UiBinder<Widget, FiltrableCommandMenu> {}

	private static FiltrableCommandMenuForProjectSwitchingMenuUiBinder projectSwitchingMenuUiBinder = GWT
			.create(FiltrableCommandMenuForProjectSwitchingMenuUiBinder.class);

	public static FiltrableCommandMenu forProjectSwitchingMenu(final CustomCommandMenuItemFactory customItemFactory, final int maxWidth, final int maxHeight) {
		return new FiltrableCommandMenu(projectSwitchingMenuUiBinder, customItemFactory, maxWidth, maxHeight);
	}

	@UiField
	protected ScrollPanel scrollPanel;

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected CommandMenu menu;

	@UiField
	protected TextBox filterArea;

	@UiField
	protected Label helpLabel;

	private List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();

	private final CustomCommandMenuItemFactory customItemFactory;

	private boolean closeOnEscape = true;

	private boolean alwaysShowMenu = true;

	private boolean isMenuVisible = alwaysShowMenu;

	private SimpleCommandMenuItem noItemsItem;

	private boolean firstTime;

	public FiltrableCommandMenu(final CustomCommandMenuItemFactory customItemFactory, final int width, final int maxHeight) {
		this(defaultUiBinder, customItemFactory, width, maxHeight);
	}

	private FiltrableCommandMenu(final UiBinder<Widget, FiltrableCommandMenu> binder, final CustomCommandMenuItemFactory customItemFactory,
			final int width,
			final int maxHeight) {

		initWidget(binder.createAndBindUi(this));
		this.customItemFactory = customItemFactory;
		this.maxHeight = maxHeight;
		focusPanel.setWidth(width + "px");

		configureMenu();
	}

	public FiltrableCommandMenu setAlwaysShowMenu(final boolean mustShown) {
		this.alwaysShowMenu = mustShown;
		setMenuVisibility(false);
		return this;
	}

	public void setItems(final List<CommandMenuItem> items) {
		Collections.sort(items);
		setOrderedItems(items);
	}

	public void setOrderedItems(final List<CommandMenuItem> items) {
		this.items = items;
		setMenuItems(items);

		adjustHeight();
	}

	@Override
	public void show() {
		firstTime = true;
		menu.show();
		selectFirstItem();
		focus();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		menu.hide();
	}

	public void focus() {
		filterArea.setFocus(true);
	}

	@UiHandler("filterArea")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (BrowserKeyCodes.isModifierKey(event.getNativeKeyCode())) return;
		if (firstTime) {
			firstTime = false;
			return;
		}
		if (event.getNativeKeyCode() == KEY_ESCAPE) {
			if (closeOnEscape) hide();
			else {
				filterArea.setText("");
				filterMenuItems();
			}
		}
		else if (event.getNativeKeyCode() == KEY_ENTER) {
			if (executeSelectedItemCommand()) hide();
		}
		else if (!KEY_DOWN_HANDLED_KEYS.contains(event.getNativeKeyCode())) {
			filterMenuItems();
		}

		helpLabel.setVisible(filterArea.getText().isEmpty());
		eatEvent(event);
	}

	@UiHandler("filterArea")
	protected void handleKeyDown(final KeyDownEvent event) {
		final int keyCode = event.getNativeKeyCode();
		event.stopPropagation();

		if (keyCode == KEY_UP) menu.selectItemUp();
		else if (keyCode == KEY_DOWN) {
			if (isMenuVisible) menu.selectItemDown();
			else if (filterArea.getText().isEmpty()) {
				setMenuVisibility(true);
				adjustHeight();
			}
		}

		if (keyCode == KEY_TAB) {
			filterArea.setText(menu.getSelectedItem().getValue());
			event.preventDefault();
		}
	}

	@UiHandler("focusPanel")
	protected void handleMouseUpfocusPanel(final MouseUpEvent event) {
		focus();
	}

	private boolean executeSelectedItemCommand() {
		if (!isMenuVisible) return false;

		final CommandMenuItem selectedItem = menu.getSelectedItem();
		if (selectedItem == null) return false;

		selectedItem.executeCommand();
		return true;
	}

	private void filterMenuItems() {
		final String filterText = filterArea.getText().trim();
		setMenuVisibility(!filterText.isEmpty());

		final List<CommandMenuItem> filteredItems = getFilteredItems(filterText);
		final boolean shouldAddCustomItems = customItemFactory != null && !filterText.isEmpty() && !hasTextMatchInItemList(filteredItems, filterText);
		if (shouldAddCustomItems) filteredItems.add(0, customItemFactory.createCustomItem(filterText));

		setMenuItems(filteredItems);
		menu.selectFirstItem();
		if (shouldAddCustomItems) menu.selectItemDown();

		adjustHeight();
	}

	private void setMenuVisibility(final boolean b) {
		if (alwaysShowMenu) return;
		if (isMenuVisible != b) scrollPanel.setVisible(isMenuVisible = b);
		adjustHeight();
	}

	private boolean hasTextMatchInItemList(final List<CommandMenuItem> items, final String text) {
		for (final CommandMenuItem item : items)
			if (item.getText().toLowerCase().equals(text.toLowerCase())) return true;
		return false;
	}

	private List<CommandMenuItem> getFilteredItems(final String filterText) {
		if (filterText.isEmpty()) return new ArrayList<CommandMenuItem>(items);

		final String lowerCaseFilterText = filterText.toLowerCase();

		int itemsStartingWithIndex = 0;
		final List<CommandMenuItem> filteredItems = new ArrayList<CommandMenuItem>();
		for (final CommandMenuItem item : items) {
			final String itemText = item.getText().toLowerCase();
			if (itemText.contains(lowerCaseFilterText)) {
				if (itemText.startsWith(lowerCaseFilterText)) filteredItems.add(itemsStartingWithIndex++, item);
				else filteredItems.add(item);
			}

		}

		return filteredItems;
	}

	private void ensureSelectedItemIsVisible() {
		final CommandMenuItem selectedItem = menu.getSelectedItem();
		if (selectedItem == null) return;

		final int menuTop = scrollPanel.getVerticalScrollPosition();
		final int menuHeight = scrollPanel.getElement().getClientHeight();
		final int menuBottom = menuTop + menuHeight;

		final int itemTop = selectedItem.getMenuItem().getElement().getOffsetTop();
		final int itemHeight = selectedItem.getMenuItem().getElement().getOffsetHeight();
		final int itemBottom = selectedItem.getMenuItem().getElement().getOffsetTop() + itemHeight;

		if (itemTop < menuTop) scrollPanel.setVerticalScrollPosition(itemTop - 1);
		else if (itemBottom > menuBottom) scrollPanel.setVerticalScrollPosition(itemTop - menuHeight + itemHeight + 3);
	}

	/*
	 * IMPORTANT Do not use max_height CSS property directly in the ui.xml file, because the first time this ScrollabeCommandMenu is
	 * created, the CSS class which this property is set is not being loaded, causing the visibility assurance to act incorrectly.
	 */
	// TODO++++ Think a new way of setting the max height
	private void adjustHeight() {
		scrollPanel.setHeight("");
		if (scrollPanel.getOffsetHeight() > maxHeight) scrollPanel.setHeight(maxHeight + "px");
	}

	private void eatEvent(final DomEvent<?> event) {
		event.preventDefault();
		event.stopPropagation();
	}

	private void configureMenu() {
		menu.setFocusWhenMouseOver(false);
		menu.setItemSelectionHandler(new ItemSelectionHandler() {
			@Override
			public void onItemSelected() {
				ensureSelectedItemIsVisible();
			}
		});
		menu.addCloseHandler(new CloseHandler<CommandMenu>() {
			@Override
			public void onClose(final CloseEvent<CommandMenu> event) {
				filterArea.setText("");
				setMenuVisibility(false);

				CloseEvent.fire(FiltrableCommandMenu.this, FiltrableCommandMenu.this);
			}
		});
	}

	public void selectFirstItem() {
		menu.selectFirstItem();
	}

	public FiltrableCommandMenu setCloseOnEscape(final boolean bool) {
		closeOnEscape = bool;
		return this;
	}

	public void setCloseOnEscape(final String bool) {
		closeOnEscape = Boolean.valueOf(bool);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<FiltrableCommandMenu> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

	@UiHandler("focusPanel")
	protected void onAttach(final AttachEvent event) {
		if (!event.isAttached()) return;
		adjustHeight();
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) adjustHeight();
	}

	public FiltrableCommandMenu setLargePadding() {
		menu.setLargePadding();
		return this;
	}

	public FiltrableCommandMenu setHelpText(final String text) {
		helpLabel.setText(text);
		return this;
	}

	private void setMenuItems(final List<CommandMenuItem> items) {
		if (items.isEmpty() && customItemFactory.getNoItemText() != null) menu.setItem(getNoItemsItem());
		else menu.setItems(items);
	}

	private SimpleCommandMenuItem getNoItemsItem() {
		return noItemsItem == null ? noItemsItem = new SimpleCommandMenuItem(customItemFactory.getNoItemText(), "", null) : noItemsItem;
	}

	public void setSelected(final CommandMenuItem item) {
		menu.setSelected(item);
	}
}
