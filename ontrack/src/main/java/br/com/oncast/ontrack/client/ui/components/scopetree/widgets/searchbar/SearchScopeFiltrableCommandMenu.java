package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ESCAPE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_TAB;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.ItemSelectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

// TODO refactor this class and FiltableCommandMenu to extract duplicated code
public class SearchScopeFiltrableCommandMenu extends Composite {

	private static SearchScopeFiltrableCommandMenuUiBinder uiBinder = GWT.create(SearchScopeFiltrableCommandMenuUiBinder.class);

	interface SearchScopeFiltrableCommandMenuUiBinder extends UiBinder<Widget, SearchScopeFiltrableCommandMenu> {}

	private static final List<Integer> KEY_DOWN_HANDLED_KEYS = Arrays.asList(new Integer[] { KEY_DOWN, KEY_UP, KEY_TAB });

	@UiField
	protected ScrollPanel scrollPanel;

	@UiField
	protected HTMLPanel result;

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected CommandMenu menu;

	@UiField
	protected HTMLPanel rootPanel;

	@UiField
	protected TextBox filterArea;

	@UiField
	protected Label resultInfo;

	private List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();

	private boolean mouseLeft = true;

	private final int maxHeight;

	public SearchScopeFiltrableCommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		this.maxHeight = 275;

		configureMenu();
		hide();
	}

	public void setItems(final List<CommandMenuItem> itens) {
		Collections.sort(itens);
		setOrderedItens(itens);
	}

	public void setOrderedItens(final List<CommandMenuItem> itens) {
		this.itens = itens;
		menu.setItems(itens);

		ajustDimentions();
	}

	public void focus() {
		filterArea.setFocus(true);
	}

	@UiHandler("focusPanel")
	protected void onMouseOut(final MouseOutEvent event) {
		mouseLeft = true;
	}

	@UiHandler("focusPanel")
	protected void onMouseOver(final MouseOverEvent event) {
		mouseLeft = false;
	}

	@UiHandler("filterArea")
	protected void onBlur(final BlurEvent event) {
		if (mouseLeft) hide();
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

	private void hide() {
		filterArea.setText("");
		result.setVisible(false);
	}

	@UiHandler("filterArea")
	protected void handleKeyDown(final KeyDownEvent event) {
		final int keyCode = event.getNativeKeyCode();
		event.stopPropagation();

		if (!KEY_DOWN_HANDLED_KEYS.contains(keyCode)) return;
		event.preventDefault();

		if (keyCode == KEY_UP) menu.selectItemUp();
		else if (keyCode == KEY_DOWN) menu.selectItemDown();

		if (keyCode == KEY_TAB) filterArea.setText(menu.getSelectedItem().getValue());
	}

	@UiHandler("focusPanel")
	protected void handleMouseUpfocusPanel(final MouseUpEvent event) {
		filterArea.setFocus(true);
	}

	private boolean executeSelectedItemCommand() {
		final CommandMenuItem selectedItem = menu.getSelectedItem();
		if (selectedItem == null) return false;

		return selectedItem.executeCommand();
	}

	private void filterMenuItens() {
		final String filterText = filterArea.getText().trim();
		result.setVisible(!filterText.isEmpty());

		final List<CommandMenuItem> filteredItens = getFilteredItens(filterText);
		resultInfo.setText("Showing " + filteredItens.size() + " matching results");

		menu.setItems(filteredItens);
		menu.selectFirstItem();

		ajustDimentions();
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
				hide();
			}
		});
	}

	public void selectFirstItem() {
		menu.selectFirstItem();
	}

	@UiHandler("focusPanel")
	protected void onAttach(final AttachEvent event) {
		if (!event.isAttached()) return;
		ajustDimentions();
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) ajustDimentions();
	}

	public interface FiltrableCommandMenuListener {

		void onItemSelected(CommandMenuItem selectedItem);

		void onCancel();
	}

}
