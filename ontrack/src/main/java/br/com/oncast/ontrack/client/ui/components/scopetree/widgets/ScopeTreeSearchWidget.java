package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.SearchScopeFiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.SearchScopeFiltrableCommandMenu.FiltrableCommandMenuListener;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ScopeTreeSearchWidget {

	private ScopeTreeSearchWidget() {}

	public static void show(final List<ScopeTreeItem> scopeTreeItemList) {
		final SearchScopeFiltrableCommandMenu searchMenu = new SearchScopeFiltrableCommandMenu(700, 400);

		registerHandlers(searchMenu, scopeTreeItemList.get(0).getTree());

		searchMenu.setOrderedItens(asCommandMenuItens(scopeTreeItemList));

		asPoupup(searchMenu).pop();
	}

	private static void registerHandlers(final SearchScopeFiltrableCommandMenu searchMenu, final Tree tree) {
		final HandlerRegistration treeFocusHandler = tree.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				searchMenu.focus();
			}
		});

		final TreeItem previouslySelectedItem = tree.getSelectedItem();
		searchMenu.setListener(new FiltrableCommandMenuListener() {
			@Override
			public void onItemSelected(final CommandMenuItem selectedItem) {
				selectedItem.executeCommand();
			}

			@Override
			public void onCancel() {
				selectItem(previouslySelectedItem);
			}
		});

		searchMenu.addCloseHandler(new CloseHandler<SearchScopeFiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<SearchScopeFiltrableCommandMenu> event) {
				treeFocusHandler.removeHandler();
			}

		});
	}

	private static List<CommandMenuItem> asCommandMenuItens(final List<ScopeTreeItem> scopeTreeItemList) {
		final List<CommandMenuItem> menuItens = new ArrayList<CommandMenuItem>();
		for (final ScopeTreeItem item : scopeTreeItemList) {
			menuItens.add(new CommandMenuItem(getItemText(item), new Command() {
				@Override
				public void execute() {
					selectItem(item);
				}
			}));
		}
		return menuItens;
	}

	private static PopupConfig asPoupup(final SearchScopeFiltrableCommandMenu searchMenu) {
		searchMenu.getElement().getStyle().setTop(10, Unit.PX);
		searchMenu.getElement().getStyle().setRight(10, Unit.PX);
		return PopupConfig.configPopup().popup(searchMenu);
	}

	private static void selectItem(final TreeItem selectedItem) {
		final Tree tree = selectedItem.getTree();
		tree.setSelectedItem(null);
		tree.setSelectedItem(selectedItem);
		tree.ensureSelectedItemVisible();
		tree.setSelectedItem(selectedItem);
	}

	private static String getItemText(final ScopeTreeItem item) {
		return item.getScopeTreeItemWidget().getScope().getDescription();
	}
}
