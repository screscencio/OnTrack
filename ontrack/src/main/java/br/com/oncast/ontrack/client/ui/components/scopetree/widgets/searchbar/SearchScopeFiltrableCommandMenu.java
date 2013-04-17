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

import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.ContainerAlignment;
import br.com.oncast.ontrack.client.WidgetVisibilityEnsurer.Orientation;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.IncrementalAdditionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ItemSelectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

// TODO refactor this class and FiltableCommandMenu to extract duplicated code
public class SearchScopeFiltrableCommandMenu extends Composite implements HasFocusHandlers, HasBlurHandlers, HasKeyDownHandlers {

	private static final int FILTER_ACTIVATION_DELAY = 400;

	private static final SearchScopeMenuMessages messages = GWT.create(SearchScopeMenuMessages.class);

	private static SearchScopeFiltrableCommandMenuUiBinder uiBinder = GWT.create(SearchScopeFiltrableCommandMenuUiBinder.class);

	interface SearchScopeFiltrableCommandMenuUiBinder extends UiBinder<Widget, SearchScopeFiltrableCommandMenu> {}

	private static final List<Integer> KEY_DOWN_HANDLED_KEYS = Arrays.asList(new Integer[] { KEY_DOWN, KEY_UP, KEY_TAB });

	@UiField
	protected SimplePanel scrollPanel;

	@UiField
	protected HTMLPanel result;

	@UiField
	protected FocusPanel focusPanel;

	@UiField
	protected CommandMenu menu;

	@UiField
	protected HTMLPanel rootPanel;

	@UiField
	protected HTMLPanel container;

	@UiField
	protected TextBox filterArea;

	@UiField
	protected Label resultInfo;

	@UiField
	protected Label searchIcon;

	private List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();

	private boolean mouseLeft = true;

	private String filterText;

	private final Timer typeFilterTimer;

	public SearchScopeFiltrableCommandMenu() {
		initWidget(uiBinder.createAndBindUi(this));
		configureMenu();
		hide();

		typeFilterTimer = new Timer() {
			@Override
			public void run() {
				filterMenuItens();
			}
		};

		result.setVisible(false);
	}

	public void setItems(final List<CommandMenuItem> itens) {
		Collections.sort(itens);
		setOrderedItens(itens);
	}

	public void setOrderedItens(final List<CommandMenuItem> itens) {
		this.itens = itens;
		menu.setItems(itens);
	}

	public void focus() {
		container.getElement().getStyle().setProperty("width", "auto");
		filterArea.setFocus(true);
	}

	private void hide() {
		filterArea.setText("");
		result.setVisible(false);
		container.getElement().getStyle().clearWidth();
	}

	@UiHandler("searchIcon")
	void onSearchIconClick(final ClickEvent event) {
		focus();
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
			typeFilterTimer.cancel();
			typeFilterTimer.schedule(FILTER_ACTIVATION_DELAY);
		}

		eatEvent(event);
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
		if (filterArea.getText().trim().equals(filterText)) return;
		filterText = filterArea.getText().trim();
		result.setVisible(!filterText.isEmpty());

		final List<CommandMenuItem> filteredItens = getFilteredItens(filterText);
		resultInfo.setText(messages.showingMatchingResults(filteredItens.size()));

		menu.setItems(filteredItens, new IncrementalAdditionListener<CommandMenuItem>() {

			boolean first = true;

			@Override
			public void onItemAdded(final CommandMenuItem item) {
				if (first) {
					menu.selectFirstItem();
					first = false;
				}
			}

			@Override
			public void onFinished(final boolean allItemsAdded) {}
		});

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

		WidgetVisibilityEnsurer.ensureVisible(selectedItem.getMenuItem().getElement().getFirstChildElement(),
				scrollPanel.getElement(), Orientation.VERTICAL, ContainerAlignment.BEGIN, ContainerAlignment.END, 3, 3);
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
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}

	public interface FiltrableCommandMenuListener {

		void onItemSelected(CommandMenuItem selectedItem);

		void onCancel();
	}

	@Override
	public HandlerRegistration addFocusHandler(final FocusHandler handler) {
		return filterArea.addFocusHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(final BlurHandler handler) {
		return filterArea.addBlurHandler(handler);
	}

	@Override
	public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
		return filterArea.addKeyDownHandler(handler);
	}

	public void clear() {
		filterArea.setText("");
		filterMenuItens();
	}

}
