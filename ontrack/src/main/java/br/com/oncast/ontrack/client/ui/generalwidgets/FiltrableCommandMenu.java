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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

// TODO refactor this class and SearchScopeFiltableCommandMenu to extract duplicated code
public class FiltrableCommandMenu extends Composite implements HasCloseHandlers<FiltrableCommandMenu>, PopupAware {

	private static final List<Integer> KEY_DOWN_HANDLED_KEYS = Arrays.asList(new Integer[] { KEY_DOWN, KEY_UP, KEY_TAB });

	private final int maxHeight;

	private final int maxWidth;

	private static FiltrableCommandMenuUiBinder uiBinder = GWT.create(FiltrableCommandMenuUiBinder.class);

	interface FiltrableCommandMenuUiBinder extends UiBinder<Widget, FiltrableCommandMenu> {}

	@UiField
	protected ScrollPanel scrollPanel;

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected CommandMenu menu;

	@UiField
	protected HTMLPanel rootPanel;

	@UiField
	protected TextBox filterArea;

	private List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();

	private final CustomCommandMenuItemFactory customItemFactory;

	public FiltrableCommandMenu(final CustomCommandMenuItemFactory customItemFactory, final int maxWidth, final int maxHeight) {
		initWidget(uiBinder.createAndBindUi(this));
		this.customItemFactory = customItemFactory;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;

		configureMenu();
	}

	public void setItens(final List<CommandMenuItem> itens) {
		Collections.sort(itens);
		setOrderedItens(itens);
	}

	public void setOrderedItens(final List<CommandMenuItem> itens) {
		this.itens = itens;
		menu.setItens(itens);

		ajustDimentions();
	}

	@Override
	public void show() {
		menu.show();
		this.setVisible(true);
		menu.selectFirstItem();
		focus();
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		this.setVisible(false);
		filterArea.setText("");

		CloseEvent.fire(this, this);
	}

	public void focus() {
		filterArea.setFocus(true);
	}

	@UiHandler("filterArea")
	protected void handleKeyUp(final KeyUpEvent event) {
		if (event.getNativeKeyCode() == KEY_ESCAPE) {
			hide();
		}
		else if (event.getNativeKeyCode() == KEY_ENTER) {
			if (executeSelectedItemCommand()) hide();
		}
		else if (!KEY_DOWN_HANDLED_KEYS.contains(event.getNativeKeyCode())) {
			filterMenuItens();
		}

		eatEvent(event);
	}

	@UiHandler("filterArea")
	protected void handleKeyDown(final KeyDownEvent event) {
		final int keyCode = event.getNativeKeyCode();
		if (!KEY_DOWN_HANDLED_KEYS.contains(keyCode)) return;

		if (keyCode == KEY_UP) menu.selectItemUp();
		else if (keyCode == KEY_DOWN) menu.selectItemDown();

		if (keyCode == KEY_TAB) filterArea.setText(menu.getSelectedItem().getValue());

		eatEvent(event);
	}

	@UiHandler("focusPanel")
	protected void handleMouseUpfocusPanel(final MouseUpEvent event) {
		filterArea.setFocus(true);
	}

	private boolean executeSelectedItemCommand() {
		final CommandMenuItem selectedItem = menu.getSelectedItem();
		if (selectedItem == null) return false;

		selectedItem.executeCommand();
		return true;
	}

	private void filterMenuItens() {
		final String filterText = filterArea.getText().trim();

		final List<CommandMenuItem> filteredItens = getFilteredItens(filterText);
		final boolean shouldAddCustomItens = !filterText.isEmpty() && !hasTextMatchInItemList(filteredItens, filterText);
		if (shouldAddCustomItens) filteredItens.add(0, customItemFactory.createCustomItem(filterText));

		menu.setItens(filteredItens);
		menu.selectFirstItem();
		if (shouldAddCustomItens) menu.selectItemDown();

		ajustDimentions();
	}

	private boolean hasTextMatchInItemList(final List<CommandMenuItem> itens, final String text) {
		for (final CommandMenuItem item : itens)
			if (item.getText().toLowerCase().equals(text.toLowerCase())) return true;
		return false;
	}

	private List<CommandMenuItem> getFilteredItens(final String filterText) {
		if (filterText.isEmpty()) return new ArrayList<CommandMenuItem>(itens);

		final String lowerCaseFilterText = filterText.toLowerCase();

		int itensStartingWithIndex = 0;
		final List<CommandMenuItem> filteredItens = new ArrayList<CommandMenuItem>();
		for (final CommandMenuItem item : itens) {
			final String itemText = item.getText().toLowerCase();
			if (itemText.contains(lowerCaseFilterText)) {
				if (itemText.startsWith(lowerCaseFilterText)) filteredItens.add(itensStartingWithIndex++, item);
				else filteredItens.add(item);
			}

		}

		return filteredItens;
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
	private void ajustDimentions() {
		scrollPanel.setWidth("");
		int calculatedMaxHeight;
		if (menu.getOffsetWidth() > maxWidth) {
			scrollPanel.setWidth((maxWidth + 20) + "px");
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.SCROLL);
			calculatedMaxHeight = maxHeight + 10;
		}
		else {
			scrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			calculatedMaxHeight = maxHeight;
		}

		scrollPanel.setHeight("");
		if (scrollPanel.getOffsetHeight() > calculatedMaxHeight) scrollPanel.setHeight(calculatedMaxHeight + "px");
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
				hide();
			}
		});
	}

	public void selectFirstItem() {
		menu.selectFirstItem();
	}

	@Override
	public HandlerRegistration addCloseHandler(final com.google.gwt.event.logical.shared.CloseHandler<FiltrableCommandMenu> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@UiHandler("focusPanel")
	protected void onAttach(final AttachEvent event) {
		if (!event.isAttached()) return;
		ajustDimentions();
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (true) ajustDimentions();
	}
}
