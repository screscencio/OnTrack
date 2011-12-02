package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FiltrableCommandMenu extends Composite implements HasCloseHandlers<FiltrableCommandMenu>, PopupAware {

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

	private final Timer filteringTimer = new Timer() {

		@Override
		public void run() {
			filterMenuItens();
		}
	};

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
	}

	@Override
	public void show() {
		this.setVisible(true);

		menu.show();
		menu.selectFirstItem();
		ajustDimentions();
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
		if (event.getNativeKeyCode() == KEY_ESCAPE) hide();

		else if (event.getNativeKeyCode() == KEY_ENTER) {
			executeSelectedItemCommand();
			hide();
		}
		else if (event.getNativeKeyCode() == KEY_DOWN || event.getNativeKeyCode() == KEY_UP) eatEvent(event);
		else {
			filteringTimer.cancel();
			filteringTimer.schedule(300);
		}

		eatEvent(event);
	}

	@UiHandler("filterArea")
	protected void handleKeyDown(final KeyDownEvent event) {
		if (event.getNativeKeyCode() == KEY_DOWN) {
			menu.selectItemDown();
			eatEvent(event);
		}

		else if (event.getNativeKeyCode() == KEY_UP) {
			menu.selectItemUp();
			eatEvent(event);
		}
		else if (event.getNativeKeyCode() == KEY_TAB) {
			eatEvent(event);
		}
	}

	@UiHandler("focusPanel")
	protected void handleMouseUpfocusPanel(final MouseUpEvent event) {
		filterArea.setFocus(true);
	}

	private void executeSelectedItemCommand() {
		final CommandMenuItem selectedItem = menu.getSelectedItem();
		if (selectedItem == null) return;

		selectedItem.getCommand().execute();
	}

	private void filterMenuItens() {
		final String filterText = filterArea.getText().trim();

		final List<CommandMenuItem> filteredItens = getFilteredItens(filterText);
		if (!filterText.isEmpty() && !hasTextMatchInItemList(filteredItens, filterText)) filteredItens.add(customItemFactory.createCustomItem(filterText));

		final CommandMenuItem selectedItem = menu.getSelectedItem();
		menu.setItens(filteredItens);

		if (filteredItens.contains(selectedItem)) menu.setSelected(selectedItem);
		else menu.selectFirstItem();

		ajustDimentions();
	}

	private boolean hasTextMatchInItemList(final List<CommandMenuItem> itens, final String text) {
		for (final CommandMenuItem item : itens)
			if (item.getText().toLowerCase().equals(text.toLowerCase())) return true;
		return false;
	}

	// TODO Cache filtering results and clean them when a new list is set.
	private List<CommandMenuItem> getFilteredItens(final String filterText) {
		if (filterText.isEmpty()) return new ArrayList<CommandMenuItem>(itens);

		final String lowerCaseFIlterText = filterText.toLowerCase();

		final List<CommandMenuItem> filteredItens = new ArrayList<CommandMenuItem>();
		for (final CommandMenuItem item : itens)
			if (item.getText().toLowerCase().startsWith(lowerCaseFIlterText)) filteredItens.add(item);

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
}
